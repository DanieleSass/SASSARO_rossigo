package Eccezioni;

public class BatteriaScaricaException extends RossiGoException {

    private int livelloAttuale = -1;

    public BatteriaScaricaException(String message) {
        super(message);
    }

    //può avere oppure no (come dice il bonus) il campo livello attuale
    //sotto tutte le possibilità di parametri
    public BatteriaScaricaException(String message, Throwable causa, int lvl) {
        super(message, causa);
        this.livelloAttuale = lvl;
    }

    public BatteriaScaricaException(String message, Throwable causa) {
        super(message, causa);
    }

    public BatteriaScaricaException(String message, int lvl) {
        super(message);
        this.livelloAttuale = lvl;
    }

    public int getLivelloAttuale() {
        return livelloAttuale;
    }
}
