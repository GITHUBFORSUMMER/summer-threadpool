package summer.threadpool.factory;

@FunctionalInterface
public interface ThreadFactory {

    Thread createThread(Runnable runnable);
}
