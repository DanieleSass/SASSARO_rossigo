package org.example;

import Persistenza.Database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.IO.println;

public class FlottaService {

    static private HashMap<String, Veicolo> veicoli = new HashMap<String, Veicolo>();
    static private HashMap<String, Utente> utenti = new HashMap<String, Utente>();
    static private ArrayList<Noleggio> noleggi = new ArrayList<Noleggio>();

    static Database database = null;

    public static void InizializzaDbDati(){
        try{
            database = Database.getInstance();
        }
        catch (SQLException e) {
            println("Impossibile connettersi al DB");
            System.exit(-1);
        }
    }

    public static void registraVeicolo(Veicolo v){
        //lo aggiunge in runtime
        veicoli.put(v.getCodice(), v);

        //modifica il db
        database.insertVeicolo(v);

    }

    public static void registraUtente(Utente u){
        utenti.put(u.getCodiceFiscale(), u);

        //modifica il db
        utenti.insertUtente();
    }

    //lancia le eccezioni
    public static void sbloccaVeicolo(String codice, String idUtente){

    }

    public static void terminaNoleggio(String codice, int minutiUsati){
        //calcola costo
        double addebito = 1.0;
        addebito+=veicoli.get(codice).calcolaCosto(minutiUsati);


        //scala batteria
        veicoli.get(codice).consumoBatteriaPerMinuto();
    }

    public static void mettiInRicarica(String codice){
        //veicoli.get(codice).
    }
}
