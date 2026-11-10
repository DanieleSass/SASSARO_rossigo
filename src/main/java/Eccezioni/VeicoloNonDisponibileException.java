package Eccezioni;

public class VeicoloNonDisponibileException extends RossiGoException {
    public VeicoloNonDisponibileException(String message) {
        super(message);
    }

    public VeicoloNonDisponibileException(String message, Throwable causa) {
        super(message, causa);
    }
}
