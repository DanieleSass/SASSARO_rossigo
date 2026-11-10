package Eccezioni;

public class UtenteNonTrovatoException extends RossiGoException {

    private int id = -1;

    public UtenteNonTrovatoException(String message) {
        super(message);
    }

    public UtenteNonTrovatoException(String message, Throwable causa) {
        super(message, causa);
    }

    public UtenteNonTrovatoException(String message, Throwable causa, int i) {
        super(message, causa);
        id = i;
    }

    public UtenteNonTrovatoException(String message, int i) {
        super(message);
        id = i;
    }

    public int getId() {
        return id;
    }
}
