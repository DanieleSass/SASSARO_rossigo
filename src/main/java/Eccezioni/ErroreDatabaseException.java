package Eccezioni;

public class ErroreDatabaseException extends RossiGoException {
    public ErroreDatabaseException(String message) {
        super(message);
    }


    public ErroreDatabaseException(String message, Throwable causa) {
        super(message, causa);
    }
}
