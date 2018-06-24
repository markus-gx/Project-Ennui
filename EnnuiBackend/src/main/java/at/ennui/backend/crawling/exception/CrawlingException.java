package at.ennui.backend.crawling.exception;

public class CrawlingException extends RuntimeException {
    public CrawlingException() {super();}
    public CrawlingException(String message){
        super(message);
    }
    public CrawlingException(String message, Throwable t){
        super(message,t);
    }
    public CrawlingException(Throwable cause){
        super(cause);
    }
}

