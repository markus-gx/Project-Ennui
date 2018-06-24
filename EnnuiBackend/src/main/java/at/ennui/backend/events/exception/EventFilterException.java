package at.ennui.backend.events.exception;

public class EventFilterException extends RuntimeException {
    public EventFilterException() {super();}
    public EventFilterException(String message){
        super(message);
    }
    public EventFilterException(String message, Throwable t){
        super(message,t);
    }
    public EventFilterException(Throwable cause){
        super(cause);
    }
}

