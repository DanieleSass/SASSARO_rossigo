package Eccezioni;

public class CreditoInsufficienteException extends RossiGoException {

    private double credito = -1;

    public CreditoInsufficienteException(String message) {
        super(message);
    }

    public CreditoInsufficienteException(String message, Throwable causa, double c) {
        super(message,causa);
        credito = c;
    }

    public CreditoInsufficienteException(String message, Throwable causa) {
        super(message,causa);
    }

    public CreditoInsufficienteException(String message, double c) {
        super(message);
        credito = c;
    }

    public double getCredito() {
        return credito;
    }
}
