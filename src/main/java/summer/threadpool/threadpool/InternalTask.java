package summer.threadpool.threadpool;

import summer.threadpool.queue.RunnableQueue;

/**
 * 线程队列对象
 */
public class InternalTask implements Runnable {

    private final RunnableQueue runnableQueue;

    private volatile boolean running = true;

    public InternalTask(RunnableQueue runnableQueue) {
        this.runnableQueue = runnableQueue;
    }


    /**
     * 不断的获取任务队列中的第一个任务并执行
     */
    @Override
    public void run() {
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                Runnable task = runnableQueue.take();
                task.run();
            } catch (Exception e) {
                running = false;
                break;
            }
        }
    }

    /**
     * Stop 这会让线程结束当前任务后不再继续执行新任务
     */
    public void stop() {
        running = false;
    }
}
