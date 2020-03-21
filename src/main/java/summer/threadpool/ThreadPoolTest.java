package summer.threadpool;

import summer.threadpool.threadpool.BasicThreadPool;
import summer.threadpool.threadpool.ThreadPool;

import java.util.concurrent.TimeUnit;

public class ThreadPoolTest {
    public static void main(String[] args) throws InterruptedException {
        final ThreadPool threadPool = new BasicThreadPool(2, 6, 4, 1000);

        for (int i = 0; i < 20; i++) {
            threadPool.execute(() -> {
                try {
                    TimeUnit.SECONDS.sleep(10);
                    System.out.println(Thread.currentThread().getName() + " is running and done.");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }


        for (; ; ) {
            System.out.println("activeCount:" + threadPool.getActiveCount());
            System.out.println("queueSize:" + threadPool.getQueueSize());
            System.out.println("coreSize:" + threadPool.getCoreSize());
            System.out.println("maxSize:" + threadPool.getMaxSize());
            System.out.println("=========================================");
            TimeUnit.SECONDS.sleep(5);
        }
    }
}
