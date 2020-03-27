package summer.threadpool.factory;

/**
 * 线程工厂
 */
@FunctionalInterface
public interface ThreadFactory {

    /**
     * 定义创建线程的接口
     *
     * @param runnable
     * @return
     */
    Thread createThread(Runnable runnable);
}
