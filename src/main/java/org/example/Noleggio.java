package org.example;

public class Noleggio {
    Utente utente;
    Veicolo veicolo;
    int oraInizio;
    int oraFine;

    public Noleggio(Utente u, Veicolo v, int oraI, int oraF){
        utente=u;
        veicolo=v;
        oraInizio=oraI;
        oraFine=oraF;
    }
}
