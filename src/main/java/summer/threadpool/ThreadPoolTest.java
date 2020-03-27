package summer.threadpool;

import summer.threadpool.threadpool.BasicThreadPool;
import summer.threadpool.threadpool.ThreadPool;

import java.util.concurrent.TimeUnit;

/**
 * test
 */
public class ThreadPoolTest {
    public static void main(String[] args) throws InterruptedException {
        //初始化线程池 最大任务队列设置 1000 个
        final ThreadPool threadPool = new BasicThreadPool(2, 6, 4, 1000);

        Integer testRunnableNumber = Integer.valueOf(20);
        for (int i = 0; i < testRunnableNumber; i++) {
            threadPool.execute(() -> {
                try {
                    TimeUnit.SECONDS.sleep(10);
                    System.out.println(Thread.currentThread().getName() + " is running and done.");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }


        //每隔5秒监听一次当前的线程池中对象的数量变化
        for (; ; ) {
            System.out.println("activeCount:" + threadPool.getActiveCount());
            System.out.println("runnableQueueSize:" + threadPool.getRunnableQueueSize());
            System.out.println("coreSize:" + threadPool.getCoreSize());
            System.out.println("maxSize:" + threadPool.getMaxSize());
            System.out.println("=========================================");
            TimeUnit.SECONDS.sleep(5);
        }
    }
}
