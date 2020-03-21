package summer.threadpool.strategy;

import summer.threadpool.threadpool.ThreadPool;
import summer.threadpool.exception.RunnableDenyException;

@FunctionalInterface
public interface DenyPolicy {

    void reject(Runnable runnable, ThreadPool threadPool);


    //直接丢掉
    class DiscardDenyPolicy implements DenyPolicy {

        @Override
        public void reject(Runnable runnable, ThreadPool threadPool) {
        }
    }


    //抛出异常给调用者
    class AbortDenyPolicy implements DenyPolicy {

        @Override
        public void reject(Runnable runnable, ThreadPool threadPool) {
            throw new RunnableDenyException("abort");
        }
    }


    //让任务在提交的线程中执行
    class RunnerDenyPolicy implements DenyPolicy {

        @Override
        public void reject(Runnable runnable, ThreadPool threadPool) {
            if (threadPool.shutdownComplete()) {
                runnable.run();
            }
        }
    }

}
