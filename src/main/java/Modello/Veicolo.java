package Modello;

public abstract class Veicolo
{
    private String codice;
    private int livelloBatteria;
    private Stato stato;

    public Veicolo(String c, int l,  Stato s){

        if (l < 0 || l > 100) {
            throw new IllegalArgumentException("Livello batteria non valido");
        }

        codice=c;
        livelloBatteria=l;
        stato=s;
    }

    public String getCodice() {
        return codice;
    }

    public int getBatteria() {
        return livelloBatteria;
    }

    public Stato getStato() {
        return stato;
    }

    public void setStato(Stato stato) {
        this.stato = stato;
    }

    public void setLivelloBatteria(int livelloBatteria) {

        if (livelloBatteria < 0 || livelloBatteria > 100) {
            throw new IllegalArgumentException("Livello batteria non valido");
        }

        this.livelloBatteria = livelloBatteria;
    }

    public abstract double calcolaCosto(int minuti);
    public abstract double consumoBatteriaPerMinuto();
}
