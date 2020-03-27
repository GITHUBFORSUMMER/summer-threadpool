package summer.threadpool.threadpool;

import summer.threadpool.factory.ThreadFactory;
import summer.threadpool.queue.LinkedRunnableQueue;
import summer.threadpool.queue.RunnableQueue;
import summer.threadpool.strategy.DenyPolicy;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author BasicThreadPool
 */
public class BasicThreadPool extends Thread implements ThreadPool {

    /**
     * 初始化线程池的线程数量
     */
    private final int initSize;

    /**
     * 最大线程数量
     */
    private final int maxSize;

    /**
     * 核心线程数量
     */
    private final int coreSize;

    /**
     * 活跃线程数量
     */
    private int activeCount;

    /**
     * 线程工厂 (每一个线程都来自于这里)
     */
    private final ThreadFactory threadFactory;

    /**
     * 任务队列
     */
    private final RunnableQueue runnableQueue;

    /**
     * 是否 shutdown 拒绝任务
     */
    private volatile boolean shutdownComplete;

    /**
     * 线程队列 , 不同于任务队列 , 这里是正在工作的线程队列
     */
    private final Queue<ThreadTask> threadQueue = new ArrayDeque<>();

    /**
     * 任务拒绝策略 (这里用的是 DiscardDenyPolicy - 多余任务直接丢掉)
     */
    private final static DenyPolicy DEFAULT_DENY_POLICY = new DenyPolicy.DiscardDenyPolicy();

    /**
     * 默认的线程工厂 (构造函数没有接收到自定义工厂就用这个工厂)
     */
    private final static ThreadFactory DEFAULT_THREAD_FACTORY = new DefaultThreadFactory();


    /**
     * 不用多说了吧
     */
    private final long keepAliveTime;

    /**
     * 不用多说了吧
     */
    private final TimeUnit timeUnit;

    /**
     * queueSize 类似线程池的阻塞队列,这里用传入队列大小的方式，自己写队列 LinkedList
     */
    public BasicThreadPool(int initSize, int maxSize, int coreSize, int queueSize) {
        this(initSize, maxSize, coreSize, DEFAULT_THREAD_FACTORY, queueSize, DEFAULT_DENY_POLICY, 10, TimeUnit.SECONDS);
    }

    /**
     * 不用多说了吧
     */
    public BasicThreadPool(int initSize, int maxSize, int coreSize, ThreadFactory threadFactory, int queueSize, DenyPolicy denyPolicy, long keepAliveTime, TimeUnit timeUnit) {
        this.initSize = initSize;
        this.maxSize = maxSize;
        this.coreSize = coreSize;
        this.threadFactory = threadFactory;
        this.runnableQueue = new LinkedRunnableQueue(queueSize, denyPolicy, this);
        this.keepAliveTime = keepAliveTime;
        this.timeUnit = timeUnit;
        this.init();
    }


    /**
     * 立即执行 Start 有点 ThreadPoolExecutor 中 addWork 的意思
     * for 初始化一点线程其实这个操作不做也行,看喜好
     */
    private void init() {
        start();
        for (int i = 0; i < initSize; i++) {
            newThread();
        }
    }

    /**
     * 先创建一个任务 InternalTask
     * 将该任务包装成线程 Thread
     * 将任和线程包装成线程对象扔进线程队列中
     * 活跃数加一
     * 执行任务
     * <p>
     * 总结:就是执行一个任务
     */
    private void newThread() {
        InternalTask internalTask = new InternalTask(runnableQueue);
        Thread thread = this.threadFactory.createThread(internalTask);
        ThreadTask threadTask = new ThreadTask(thread, internalTask);
        threadQueue.offer(threadTask);
        this.activeCount++;
        thread.start();
    }

    /**
     * 不用多说了吧
     */
    private void removeThread() {
        ThreadTask threadTask = threadQueue.remove();
        threadTask.internalTask.stop();
        this.activeCount--;
    }

    /**
     * 线程队列接收对象
     */
    private static class ThreadTask {
        public ThreadTask(Thread thread, InternalTask internalTask) {
            this.thread = thread;
            this.internalTask = internalTask;
        }

        Thread thread;
        InternalTask internalTask;
    }


    /**
     * 初始化执行 Start 会执行这个方法
     * 不断轮循当线程数量的情况,决定是否继续获取任务队列中执行任务
     * 这里用的 synchronized 关键字
     */
    @Override
    public void run() {
        while (!shutdownComplete && !isInterrupted()) {
            try {
                timeUnit.sleep(keepAliveTime);
            } catch (InterruptedException e) {
                shutdownComplete = true;
                break;
            }
            synchronized (this) {
                if (shutdownComplete) {
                    break;
                }

                if (runnableQueue.size() > 0 && activeCount < coreSize) {
                    for (int i = initSize; i < coreSize; i++) {
                        newThread();
                    }
                    continue;
                }

                if (runnableQueue.size() > 0 && activeCount < maxSize) {
                    for (int i = coreSize; i < maxSize; i++) {
                        newThread();
                    }
                }
                if (runnableQueue.size() == 0 && activeCount > coreSize) {
                    for (int i = coreSize; i < activeCount; i++) {
                        removeThread();
                    }
                }

            }
        }
    }

    /**
     * 提交任务入口
     * 若任务满了会根据线程池初始化时的拒绝策略对任务做处理
     *
     * @param runnable
     */
    @Override
    public void execute(Runnable runnable) {

        if (this.shutdownComplete) {
            throw new IllegalStateException("thead pool is destroy");
        }
        this.runnableQueue.offer(runnable);
    }

    /**
     * 不用多说了吧
     */
    @Override
    public void shutdown() {
        synchronized (this) {
            if (shutdownComplete) {
                return;
            }
            shutdownComplete = true;
            threadQueue.forEach(threadTask -> {
                threadTask.internalTask.stop();
                threadTask.thread.interrupt();
            });
            this.interrupt();
        }
    }

    /**
     * 不用多说了吧
     */
    @Override
    public int getInitSize() {
        if (shutdownComplete) {
            throw new IllegalStateException("thread pool is destroy");
        }
        return this.initSize;
    }

    /**
     * 不用多说了吧
     */
    @Override
    public int getMaxSize() {
        if (shutdownComplete) {
            throw new IllegalStateException("thread pool is destroy");
        }
        return this.maxSize;
    }

    /**
     * 不用多说了吧
     */
    @Override
    public int getCoreSize() {
        if (shutdownComplete) {
            throw new IllegalStateException("thread pool is destroy");
        }
        return this.coreSize;
    }

    /**
     * 不用多说了吧
     */
    @Override
    public int getRunnableQueueSize() {
        if (shutdownComplete) {
            throw new IllegalStateException("thread pool is destroy");
        }
        return runnableQueue.size();
    }

    /**
     * 不用多说了吧
     */
    @Override
    public int getActiveCount() {
        synchronized (this) {
            return this.activeCount;
        }
    }

    /**
     * 不用多说了吧
     */
    @Override
    public boolean shutdownComplete() {
        return this.shutdownComplete;
    }


    /**
     * 线程工厂具体实现
     */
    private static class DefaultThreadFactory implements ThreadFactory {


        /**
         * 线程组名称参数变量
         */
        private static final AtomicInteger GROUP_COUNTER = new AtomicInteger();

        private static final ThreadGroup group = new ThreadGroup("my_thread_group_" + GROUP_COUNTER.getAndDecrement());

        private static final AtomicInteger COUNTER = new AtomicInteger(0);

        /**
         * 初始化线程 指定线程组 相关任务 线程名称
         *
         * @param runnable
         * @return
         */
        @Override
        public Thread createThread(Runnable runnable) {
            return new Thread(group, runnable, "thread_pool_" + COUNTER.getAndDecrement());
        }
    }


}
