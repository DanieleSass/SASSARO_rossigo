package Eccezioni;

public class CodiceFiscaleNonValidoException extends RossiGoException {

    private String codice = null;

    public CodiceFiscaleNonValidoException(String message) {
        super(message);
    }

    public CodiceFiscaleNonValidoException(String message, String c, Throwable causa) {
        super(message, causa);
        codice = c;
    }

    public CodiceFiscaleNonValidoException(String message, Throwable causa) {
        super(message, causa);
    }

    public CodiceFiscaleNonValidoException(String message, String c) {
        super(message);
        codice = c;
    }

    public String getCodice() {
        return codice;
    }
}
