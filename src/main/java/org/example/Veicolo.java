package org.example;

public abstract class Veicolo
{
    private String codice;
    private int livello;
    private int batteria;
    private stato stato;

    public Veicolo(String c, int l, int b, stato s){
        codice=c;
        livello=l;
        batteria=b;
        stato=s;
    }

    public String getCodice() {
        return codice;
    }

    public int getBatteria() {
        return batteria;
    }

    public int getLivello() {
        return livello;
    }

    public stato getStato() {
        return stato;
    }

    public abstract double calcolaCosto(int minuti);
    public abstract double consumoBatteriaPerMinuto();
}
