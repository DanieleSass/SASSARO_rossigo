package Service;

import Eccezioni.*;
import Modello.Noleggio;
import Modello.Stato;
import Modello.Utente;
import Modello.Veicolo;
import Persistenza.Database;
import it.kamaladafrica.codicefiscale.CodiceFiscale;
import org.apache.commons.validator.routines.EmailValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.IO.println;

public class FlottaService {

    static private HashMap<String, Veicolo> veicoli = new HashMap<String, Veicolo>();
    static private HashMap<Integer, Utente> utenti = new HashMap<Integer, Utente>();
    static private ArrayList<Noleggio> noleggi = new ArrayList<Noleggio>();

    static Database database = null;

    public static void InizializzaDbDati(){
        try{
            database = Database.getInstance();

            veicoli = database.getVeicoliMap();
            utenti = database.getUtentiMap();
            noleggi = database.getNoleggiList();
        }
        catch (ErroreDatabaseException e) {
            println("Impossibile connettersi al DB");
            System.exit(-1);
        }
    }

    //metodo di prova
    public static String stampaUtenti(){
        String c = "";
        for (Utente u : utenti.values()){
            //println(u.getNome());
            c+=u.getId()+"\n";
        }
        return c;
    }

    public static void registraVeicolo(Veicolo v) throws RossiGoException {
        //lo aggiunge in runtime

        if(veicoli.containsKey(v.getCodice())){
            throw new VeicoloNonDisponibileException("Veicolo già registrato");
        }

            //modifica il db
            database.insertVeicolo(v);

            veicoli.put(v.getCodice(), v);


    }

    public static void registraUtente(Utente u) throws RossiGoException {

        EmailValidator validatoreEmail = EmailValidator.getInstance();

        if(validatoreEmail.isValid(u.getEmail()) == false){
            throw new RossiGoException("Indirizzo email non valido (email: "+u.getEmail()+" )");
        }

        //controllo codice fiscale
        try {
            CodiceFiscale.of(u.getCodiceFiscale());
        }
        catch (RuntimeException e) {
            throw new CodiceFiscaleNonValidoException(
                    "Codice fiscale non valido",u.getCodiceFiscale(),e);
        }

        if(utenti.containsKey(u.getId())){
            throw new RossiGoException("Utente già registrato");
        }

            //modifica il db
            database.insertUtente(u);

            utenti.put(u.getId(), u);


    }

    //lancia le eccezioni
    public static void sbloccaVeicolo(String codice, int idUtente) throws RossiGoException{
        Veicolo v = veicoli.get(codice);
        Utente u = utenti.get(idUtente);
        if(v==null){
            throw new VeicoloNonDisponibileException("Veicolo inesistente");
        }
        if(u==null){
            throw new UtenteNonTrovatoException("Utente non trovato", idUtente);
        }

        if(v.getStato()!= Stato.Libero){
            throw new VeicoloNonDisponibileException("Veicolo non disponibile");
        }
        if (v.getBatteria() < 15) {
            throw new BatteriaScaricaException("Batteria insufficiente", v.getBatteria());
        }
        if(u.getCredito()<1){
            throw new CreditoInsufficienteException("Credito insufficiente per sbloccare", u.getCredito());
        }

        //salvo in memoria e ripristino nel trycatch se modifica nel db fallisce
            Stato statoPrec = v.getStato();

            //qua vuol dire che può sbloccare
            v.setStato(Stato.In_Uso);

            LocalDateTime oraInizio = LocalDateTime.now();
            Noleggio n = new Noleggio(u, v, oraInizio, null);
            try{

                //aggiorno anche db
                database.insertNoleggio(n);

                database.updateVeicolo(v);

                noleggi.add(n);

            }
            catch (ErroreDatabaseException e ){
                v.setStato(statoPrec);
                throw e;
            }
    }

    public static void terminaNoleggio(String codice, int minutiUsati) throws RossiGoException{

        if (minutiUsati <= 0) {
            throw new IllegalArgumentException("Durata non valida");
        }

        Veicolo v = veicoli.get(codice);
        Utente u;

        if(v==null){
            throw new VeicoloNonDisponibileException("Veicolo inesistente");
        }

        if (v.getStato() != Stato.In_Uso) {
            throw new VeicoloNonDisponibileException("Veicolo non in uso");
        }

            Noleggio n = null;
            for(Noleggio nn : FlottaService.noleggi){
                //trova noleggio associato a quel veicolo che non si è ancora concluso
                if(nn.getVeicolo().getCodice().equals(codice) && nn.getOraFine()==null){
                    n = nn;
                    break;
                }
            }

            if(n==null){
                throw new VeicoloNonDisponibileException("Noleggio non trovato");
            }

            u = n.getUtente();

            //calcola costo
            double addebito = 1.0;
            addebito+=v.calcolaCosto(minutiUsati);

            if(u.getCredito()<addebito){
                throw new CreditoInsufficienteException( "Credito insufficiente per terminare il noleggio", u.getCredito());
            }


            //salvo i valori in variabili così dopo nel catch li ripristino se query fallisce
            double creditoPrecedente = u.getCredito();
            int batteriaPrecedente = v.getBatteria();
            Stato statoPrecedente = v.getStato();
            LocalDateTime oraFinePrecedente = n.getOraFine();

            u.AggiungiCredito(-addebito);

            //scala batteria
            double batteria = minutiUsati * v.consumoBatteriaPerMinuto();

            int nuovaBatteria = (int)Math.round(v.getBatteria() - batteria);

            if(nuovaBatteria < 0){
                nuovaBatteria = 0;
            }

            v.setLivelloBatteria(nuovaBatteria);

            v.setStato(Stato.Libero);

            n.setOraFine(LocalDateTime.now());

            //aggiorno anche db
        try{
            database.updateVeicolo(v);
            database.updateUtente(u);
            database.updateNoleggio(n);
        }
        catch (ErroreDatabaseException e){
            //resetto le varianili
            u.setCredito(creditoPrecedente);
            v.setLivelloBatteria(batteriaPrecedente);
            v.setStato(statoPrecedente);
            n.setOraFine(oraFinePrecedente);

            throw e;
        }
    }

    public static void mettiInRicarica(String codice) throws RossiGoException{
        Veicolo v = veicoli.get(codice);
        if(v==null){
            throw new VeicoloNonDisponibileException("Veicolo inesistente");
        }

        if(v.getStato() != Stato.Libero){
            throw new VeicoloNonDisponibileException("Veicolo già in uso o già in carica");

        }
        Stato statoPrec = v.getStato();
        v.setStato(Stato.In_Ricarica);
        try{

            //aggiorno db
            database.updateVeicolo(v);
        }
        catch (ErroreDatabaseException e){

            v.setStato(statoPrec);

            throw e;
        }




    }
}
