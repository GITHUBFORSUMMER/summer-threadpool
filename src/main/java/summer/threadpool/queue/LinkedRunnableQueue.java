package summer.threadpool.queue;

import summer.threadpool.threadpool.ThreadPool;
import summer.threadpool.strategy.DenyPolicy;

import java.util.LinkedList;

/**
 * 任务队列操作实现
 */
public class LinkedRunnableQueue implements RunnableQueue {

    /**
     * 最大任务数
     */
    private final int limit;

    /**
     * 拒绝策略
     */
    private final DenyPolicy denyPolicy;

    /**
     * 任务队列
     */
    private final LinkedList<Runnable> runnableList = new LinkedList<>();

    /**
     * 当前线程池 策略中可能会关闭线程池，看具体实现
     */
    private final ThreadPool threadPool;

    public LinkedRunnableQueue(int limit, DenyPolicy denyPolicy, ThreadPool threadPool) {
        this.limit = limit;
        this.denyPolicy = denyPolicy;
        this.threadPool = threadPool;
    }


    /**
     * 如果队列满了，就执行响应的拒绝策略
     * 若队列未满就添加任务，并唤醒阻塞中的线程
     *
     * @param runnable
     */
    @Override
    public void offer(Runnable runnable) {
        synchronized (runnableList) {
            if (runnableList.size() >= limit) {
                denyPolicy.reject(runnable, threadPool);
            } else {
                runnableList.add(runnable);
                runnableList.notifyAll();
            }
        }

    }


    /**
     * 队列为空，则阻塞线程
     * 队位不为空返回队列中的第一个任务
     *
     * @return
     * @throws InterruptedException
     */
    @Override
    public Runnable take() throws InterruptedException {

        synchronized (runnableList) {
            while (runnableList.isEmpty()) {
                try {
                    runnableList.wait();
                } catch (InterruptedException e) {
                    throw e;
                }
            }
            return runnableList.removeFirst();
        }
    }


    /**
     * 返回任务队列中的数量
     *
     * @return
     */
    @Override
    public int size() {
        synchronized (runnableList) {
            return runnableList.size();
        }
    }
}
