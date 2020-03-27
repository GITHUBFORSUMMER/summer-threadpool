package summer.threadpool.exception;

/**
 * 任务队列自定义异常
 */
public class RunnableDenyException extends RuntimeException {


    /**
     * 构造函数
     *
     * @param message
     */
    public RunnableDenyException(String message) {
        super(message);
    }

}
