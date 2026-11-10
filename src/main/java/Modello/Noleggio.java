package Modello;

import java.time.LocalDateTime;

public class Noleggio {
    private Utente utente;
    private Veicolo veicolo;
    private LocalDateTime oraInizio;
    private LocalDateTime oraFine;

    public Noleggio(Utente u, Veicolo v, LocalDateTime oraI, LocalDateTime oraF){
        utente=u;
        veicolo=v;
        oraInizio=oraI;
        oraFine=oraF;
    }

    public LocalDateTime getOraFine() {
        return oraFine;
    }

    public LocalDateTime getOraInizio() {
        return oraInizio;
    }

    public Utente getUtente() {
        return utente;
    }

    public Veicolo getVeicolo() {
        return veicolo;
    }

    public void setOraInizio(LocalDateTime oraInizio) {
        this.oraInizio = oraInizio;
    }

    public void setOraFine(LocalDateTime oraFine) {
        this.oraFine = oraFine;
    }
}
