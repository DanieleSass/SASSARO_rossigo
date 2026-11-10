package Eccezioni;

public class RossiGoException extends Exception {
    public RossiGoException(String message) {
        super(message);
    }

    public RossiGoException(String message, Throwable causa){
        super(message, causa);
    }
}
