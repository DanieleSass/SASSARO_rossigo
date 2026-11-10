import Eccezioni.RossiGoException;
import Modello.Bici;
import Modello.Monopattino;
import Modello.Stato;
import Modello.Utente;
import Service.FlottaService;
import com.github.lalyos.jfiglet.FigletFont;

import java.io.IOException;

import static java.lang.IO.println;

void main() {

    //inizializzazione database
    FlottaService.InizializzaDbDati();

    //println("Print di test");
    //println(FlottaService.stampaUtenti());
    //println();

    //banner iniziale
    try {
        String titolo = FigletFont.convertOneLine("Rossi GO");
        println(titolo);
    } catch (IOException e) {
        System.err.println("Errore generazione dell'ASCII art: " + e.getMessage());
    }

    // n -> numero marce
    // v -> velocità
    Bici bici1 = new Bici(5, "BICI15", 100, Stato.Libero);
    Monopattino monopattino = new Monopattino(25, "MONO15", 100, Stato.Libero);

    Utente utente1 = new Utente(15, "Mario Rossi", "mario@email.it", "RSSMRA80A01H501U", 50.0);
    Utente utente2 = new Utente(25, "Luca Bianchi", "luca@email.it", "BNCLCU80A01H501X", 0.50);
    Utente utenteConPocoCredito = new Utente(3, "Luca Rossi", "luca.rossi@email.it", "RSSLCU80A01H501X", 10.0);

    try {
        FlottaService.registraVeicolo(bici1);
        FlottaService.registraVeicolo(monopattino);

        FlottaService.registraUtente(utente1);
        FlottaService.registraUtente(utente2);
        FlottaService.registraUtente(utenteConPocoCredito);

    } catch (RossiGoException e) {
        println("Errore registrazione: " + e.getMessage());
    } catch (Exception e) {
        println("Errore: " + e.getMessage());
    }

    println();

    //noleggio normalissimo
    println("Noleggio normale");
    try {
        FlottaService.sbloccaVeicolo("BICI15", 15);
        FlottaService.terminaNoleggio("BICI15", 10);
        println("Noleggio terminato correttamente.");
    } catch (RossiGoException e) {
        println(e.getMessage());
    }

    println();

    //veicolo che non esiste
    println("Veicolo che non esiste");
    try {
        FlottaService.sbloccaVeicolo("BICI-XXXX", 15);
    } catch (RossiGoException e) {
        println(e.getMessage());
    }

    println();

    //utente che non esiste
    println("utente che non esiste");
    try {
        FlottaService.sbloccaVeicolo("MONO15", 4);
    } catch (RossiGoException e) {
        println(e.getMessage());
    }

    println();

    //batteria non sufficiente
    println("batteria non sufficiente");
    try {
        Bici biciConBatteriaScarica = new Bici(30, "BICI25", 10, Stato.Libero);
        FlottaService.registraVeicolo(biciConBatteriaScarica);

        FlottaService.sbloccaVeicolo("BICI25", 25);
    } catch (RossiGoException e) {
        println(e.getMessage());
    }

    println();

    //credito non sufficiente
    println("credito non sufficiente");
    try {
        FlottaService.sbloccaVeicolo("MONO12", 25);
    } catch (RossiGoException e) {
        println(e.getMessage());
    }

    println();

    //veicolo non disponibile
    println("veicolo non disponibile");
    try {
        FlottaService.mettiInRicarica("MONO12");
        FlottaService.sbloccaVeicolo("MONO12", 15);
    } catch (RossiGoException e) {
        println(e.getMessage());
    }

    println();
}