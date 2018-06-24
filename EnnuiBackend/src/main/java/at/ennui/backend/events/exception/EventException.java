package at.ennui.backend.events.exception;

public class EventException extends RuntimeException {
    public EventException() {super();}
    public EventException(String message){
        super(message);
    }
    public EventException(String message, Throwable t){
        super(message,t);
    }
    public EventException(Throwable cause){
        super(cause);
    }
}

