package Modello;

public class Bici extends Veicolo {
    private int numeroMarce;

    public Bici(int n, String c, int l, Stato s){
        super(c,l,s);
        numeroMarce = n;
    }


    @Override
    public double calcolaCosto(int minuti) {
        return 0.15 * minuti;
    }

    @Override
    public double consumoBatteriaPerMinuto() {
        return 0.5;
    }

    public int getNumeroMarce() {
        return numeroMarce;
    }
}
