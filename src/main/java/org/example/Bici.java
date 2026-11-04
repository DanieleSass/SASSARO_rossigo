package org.example;

public class Bici extends Veicolo{
    int numeroMarce;

    public Bici(int n, String c, int l, int b, stato s){
        numeroMarce = n;
        super(c,l,b,s);
    }


    @Override
    public double calcolaCosto(int minuti) {
        return 0.15 * minuti;
    }

    @Override
    public double consumoBatteriaPerMinuto() {
        return 2;
    }

    public int getNumeroMarce() {
        return numeroMarce;
    }
}
