package Modello;

public class Monopattino extends Veicolo {
    private double velocitaMassima;

    public Monopattino(double v, String c, int l, Stato s){
        super(c,l, s);
        velocitaMassima = v;
    }

    @Override
    public double calcolaCosto(int minuti) {
        return 0.25 * minuti;
    }

    @Override
    public double consumoBatteriaPerMinuto() {
        return 1.5;
    }

    public double getVelocitaMassima() {
        return velocitaMassima;
    }
}
