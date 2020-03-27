package summer.threadpool.queue;

/**
 * 任务队列操作接口
 */
public interface RunnableQueue {

    /**
     * 添加任务
     *
     * @param runnable
     */
    void offer(Runnable runnable);

    /**
     * 获取任务
     *
     * @return
     * @throws InterruptedException
     */
    Runnable take() throws InterruptedException;

    /**
     * 获取任务队列当前size
     *
     * @return
     */
    int size();
}
