package org.example;

public class Monopattino extends Veicolo{
    double velocitaMassima;

    public Monopattino(double v, String c, int l, int b, stato s){
        velocitaMassima = v;
        super(c,l,b,s);

    }

    @Override
    public double calcolaCosto(int minuti) {
        return 0.25 * minuti;
    }

    @Override
    public double consumoBatteriaPerMinuto() {
        return 1.5;
    }
}
