package at.ennui.backend.facebook.exception;

public class FacebookAuthenticationException extends RuntimeException {
    public FacebookAuthenticationException() {super();}
    public FacebookAuthenticationException(String message){
        super(message);
    }
    public FacebookAuthenticationException(String message, Throwable t){
        super(message,t);
    }
    public FacebookAuthenticationException(Throwable cause){
        super(cause);
    }
}
