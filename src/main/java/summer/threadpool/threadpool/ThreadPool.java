package summer.threadpool.threadpool;

/**
 * 线程池接口功能定义
 */
public interface ThreadPool {

    /**
     * 提交任务
     *
     * @param runnable
     */
    void execute(Runnable runnable);

    /**
     * 拒绝任务 (当前实现: 这会让线程结束当前任务后不再继续执行新任务)
     */
    void shutdown();


    /**
     * 获取初始化线程数量
     *
     * @return
     */
    int getInitSize();

    /**
     * 获取最大线程数量
     *
     * @return
     */
    int getMaxSize();

    /**
     * 获取核心线程数量
     *
     * @return
     */
    int getCoreSize();

    /**
     * 获取任务队列中的任务数量
     *
     * @return
     */
    int getRunnableQueueSize();

    /**
     * 获取活跃线程数量
     *
     * @return
     */
    int getActiveCount();

    /**
     * 获取是否 shutdown
     *
     * @return
     */
    boolean shutdownComplete();

}
