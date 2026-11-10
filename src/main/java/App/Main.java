import Eccezioni.RossiGoException;
import Modello.Bici;
import Modello.Monopattino;
import Modello.Stato;
import Modello.Utente;
import Service.FlottaService;
import com.github.lalyos.jfiglet.FigletFont;
import com.github.lalyos.jfiglet.JFiglet;

import java.io.IOException;

import static java.lang.IO.println;

void main() {

    try {   //ascii art
        String titolo = FigletFont.convertOneLine("Rossi GO");
        println(titolo);
    } catch (IOException e) {
        println("Impossibile caricare jfiglet");
    }


    //inizializzazione del db e caricamento dati in memoria
    FlottaService.InizializzaDbDati();

    //creazione di entità di test
    Bici bici1 = new Bici(6, "BICI_CENTRO", 100, Stato.Libero);
    Monopattino mono1 = new Monopattino(25, "MONO_STAZIONE", 100, Stato.Libero);

    Utente utenteRicco = new Utente(101, "Mario Rossi", "mario.rossi@email.it", "RSSMRA80A01H501U", 30.0);
    Utente utentePovero = new Utente(102, "Luca Bianchi", "luca.bianchi@email.it", "SSSDNL08R01L736J", 0.50);

    //registrazione nel db (e ovviamente anche nelle strutture dat)
    try {
        FlottaService.registraVeicolo(bici1);
    } catch (RossiGoException e) {

    }

    try {
        FlottaService.registraVeicolo(mono1);
    } catch (RossiGoException e) {}

    try {
        FlottaService.registraUtente(utenteRicco);
    } catch (RossiGoException e) {
        println("Info: Utente 101 già registrato.");
    }

    try {
        FlottaService.registraUtente(utentePovero);
    } catch (RossiGoException e) {
        println("Info: Utente 102 già registrato / errore: " + e.getMessage());
    }

    println();

    //1 scenario->noleggio normale
    println("1 usa una bici con successo");
    try {
        FlottaService.sbloccaVeicolo("BICI_CENTRO", 101);
        println("BICI_CENTRO è stata sbloccata");

        FlottaService.terminaNoleggio("BICI_CENTRO", 15);
        println("Noleggio terminato dopo 15 minuti");
    } catch (RossiGoException e) {
        println("Errore: " + e.getMessage() + "\n");
    }

    println();

    //2 scenario->credito insufficiente (monopattin)
    println("2 tentativo di sblocco con credito insufficiente");
    try {
        FlottaService.sbloccaVeicolo("MONO_STAZIONE", 102);
        println("Veicolo sbloccato");
    } catch (RossiGoException e) {
        println("errore: " + e.getMessage() + " (credito insuffficiente)\n");
    }

    println();

    //3 scenario->registrazione con cf errato
    println("3 registrazione utente con cf errato.");
    try {
        Utente utenteErrato = new Utente(103, "Giuseppe Verdi", "verdi@email.it", "CF_ERRATO_123", 20.0);
        FlottaService.registraUtente(utenteErrato);
        println("utente registrato");
    } catch (RossiGoException e) {
        println("errore: " + e.getMessage() + " (cf non giusto secondo legge)\n");
    }

    println();

    //scenario 4->sbloccare un veicolo già in uso
    println("4 sbloccare un veicolo già in uso");
    try {
        //metto il mono in uso
        FlottaService.sbloccaVeicolo("MONO_STAZIONE", 101);

        //provo a sbloccarlo
        FlottaService.sbloccaVeicolo("MONO_STAZIONE", 102);

    } catch (RossiGoException e) {
        println("errore: " + e.getMessage() + " (occupato)\n");
    } finally {
        //lo sblocca cmq x dopo
        try {
            FlottaService.terminaNoleggio("MONO_STAZIONE", 5);
        } catch (Exception e) {
            //la ignora
        }
    }

    println();

    //scenario 5->batteria scarica <15%
    println("5 sbloccare un veicolo con batteria scarica");
    try {
        Bici biciScarica = new Bici(4, "BICI_SCARICA", 10, Stato.Libero);
        FlottaService.registraVeicolo(biciScarica);

        FlottaService.sbloccaVeicolo("BICI_SCARICA", 101);
    } catch (RossiGoException e) {
        println("errore: " + e.getMessage() + " batteria <soglia.\n");
    }

    println();

    //"scenario" 6->salvo dentro db
    println("6 persistenza");

    //con questo ricarica i dati, quindi ri-esegue le query
    FlottaService.InizializzaDbDati();
    println(FlottaService.stampaUtenti());   //di test
}