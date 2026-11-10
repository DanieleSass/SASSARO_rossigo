package Persistenza;

import Eccezioni.ErroreDatabaseException;
import Modello.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.IO.println;

public class Database {
    private Connection connection = null;
    private static Database instance = null;

    private Database() throws ErroreDatabaseException  {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:rossigo.db");
            CreaTabelle();
        } catch (SQLException e) {
            throw new ErroreDatabaseException("Impossibile connettersi al database.", e);
        }
    }

    public static Database getInstance() throws ErroreDatabaseException {
        if(instance==null){
            instance = new Database();

        }
        return instance;
    }

    private void CreaTabelle() throws ErroreDatabaseException{
        String queryUtente = """
        CREATE TABLE IF NOT EXISTS Utente (
            Id INTEGER PRIMARY KEY,
            Nome VARCHAR(100) NOT NULL,
            Email VARCHAR(100) NOT NULL,
            CodiceFiscale VARCHAR(16) NOT NULL,
            Credito DOUBLE
        )
        """;

        String queryVeicolo = """
        CREATE TABLE IF NOT EXISTS Veicolo (
            Codice VARCHAR(255) PRIMARY KEY,
            Batteria INTEGER NOT NULL,
            Stato VARCHAR(50) NOT NULL,
            TipoVeicolo VARCHAR(50) NOT NULL,
            NumeroMarce INTEGER,
            VelocitaMassima DOUBLE
        )
        """;

        String queryNoleggio = """
        CREATE TABLE IF NOT EXISTS Noleggio (
            Id INTEGER PRIMARY KEY AUTOINCREMENT,
            IdUtente INTEGER NOT NULL,
            CodiceVeicolo VARCHAR(255) NOT NULL,
            OraInizio VARCHAR(100) NOT NULL,
            OraFine VARCHAR(100),
            FOREIGN KEY (IdUtente) REFERENCES Utente(Id),
            FOREIGN KEY (CodiceVeicolo) REFERENCES Veicolo(Codice)
        )
        """;

        try (Statement statement = connection.createStatement()) {

            statement.executeUpdate(queryUtente);
            println("Tabella Utente creata");

            statement.executeUpdate(queryVeicolo);
            println("Tabella Veicolo creata");

            statement.executeUpdate(queryNoleggio);
            println("Tabella Noleggio creata");

        } catch (SQLException e) {
            throw new ErroreDatabaseException(
                    "Errore durante la creazione delle tabelle: " + e.getMessage(), e);
        }

    }

    public HashMap<Integer, Utente> getUtentiMap() throws ErroreDatabaseException {
        if (!testConnection()) {
            throw new RuntimeException("Impossibile connettersi al db");
        }

        HashMap<Integer, Utente> mappaUtenti = new HashMap<>();
        String query = "SELECT Id, Nome, Email, CodiceFiscale, Credito FROM Utente";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("Id");
                String nome = resultSet.getString("Nome");
                String email = resultSet.getString("Email");
                String codiceFiscale = resultSet.getString("CodiceFiscale");
                double credito = resultSet.getDouble("Credito");

                Utente u = new Utente(id, nome, email, codiceFiscale, credito);

                mappaUtenti.put(id, u);
            }
        } catch (SQLException e) {
            throw new ErroreDatabaseException("Errore nel recupero degli utenti: " + e.getMessage(), e);
        }
        return mappaUtenti;
    }



    public HashMap<String, Veicolo> getVeicoliMap() throws ErroreDatabaseException {
        if (!testConnection()) {
            throw new RuntimeException("Impossibile connettersi al db");
        }

        HashMap<String, Veicolo> mappaVeicoli = new HashMap<>();
        String query = "SELECT Codice, Batteria, Stato, TipoVeicolo, NumeroMarce, VelocitaMassima FROM Veicolo";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String cod = resultSet.getString("Codice");
                int batteria = resultSet.getInt("Batteria");
                Stato stato = Stato.valueOf(resultSet.getString("Stato"));
                String tipoVeicolo = resultSet.getString("TipoVeicolo");

                Veicolo v = null;
                if (tipoVeicolo.equalsIgnoreCase("BICI")) {
                    int numeroMarce = resultSet.getInt("NumeroMarce");
                    v = new Bici(numeroMarce, cod, batteria, stato);
                } else if (tipoVeicolo.equalsIgnoreCase("MONOPATTINO")) {
                    double velocitaMassima = resultSet.getDouble("VelocitaMassima");
                    v = new Monopattino(velocitaMassima, cod, batteria, stato);
                }

                if (v != null) {
                    mappaVeicoli.put(cod, v);
                }
            }

        } catch (SQLException e) {
            throw new ErroreDatabaseException("Errore nel recupero dei veicoli: " + e.getMessage(), e);
        }

        return mappaVeicoli;
    }


    public ArrayList<Noleggio> getNoleggiList() throws ErroreDatabaseException {
        if (!testConnection()) {
            throw new RuntimeException("Impossibile connettersi al db");
        }

        ArrayList<Noleggio> listaNoleggi = new ArrayList<>();

        // Per associare utente e veicolo al noleggio, recuperiamo le rispettive mappe
        HashMap<Integer, Utente> utentiMap = getUtentiMap();
        HashMap<String, Veicolo> veicoliMap = getVeicoliMap();

        String query = "SELECT Id, IdUtente, CodiceVeicolo, OraInizio, OraFine FROM Noleggio";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int idUtente = resultSet.getInt("IdUtente");
                String codiceVeicolo = resultSet.getString("CodiceVeicolo");

                String oraInizioStr = resultSet.getString("OraInizio");
                String oraFineStr = resultSet.getString("OraFine");

                Utente utente = utentiMap.get(idUtente);
                Veicolo veicolo = veicoliMap.get(codiceVeicolo);

                if (utente != null && veicolo != null) {
                    //convertiamo la stringa di inizio in LocalDateTime
                    LocalDateTime oraInizio = LocalDateTime.parse(oraInizioStr);

                    LocalDateTime oraFine = null;
                    if (oraFineStr != null && !oraFineStr.trim().isEmpty()) {
                        oraFine = LocalDateTime.parse(oraFineStr);
                    }

                    Noleggio noleggio = new Noleggio(utente, veicolo, oraInizio, oraFine);

                    listaNoleggi.add(noleggio);
                }
            }

        } catch (SQLException e) {
            throw new ErroreDatabaseException("Errore nel recupero dei noleggi: " + e.getMessage(), e);
        }

        return listaNoleggi;
    }


    public void insertVeicolo(Veicolo v) throws ErroreDatabaseException{
        //test connessione
        if(! testConnection()){
            throw new RuntimeException("Impossibile connettersi al db");
        }

        //preparazione query
        String query = "INSERT INTO Veicolo (Codice, Batteria, Stato, TipoVeicolo, NumeroMarce, VelocitaMassima) VALUES (?, ?, ?, ?, ?, ?)";
        //println(query);

        //esecuzione query
        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setString(1,v.getCodice());
            statement.setInt(2,v.getBatteria());
            //enum come stringa
            statement.setString(3, v.getStato().name());

            if(v instanceof Bici b){
                statement.setString(4, "BICI");
                statement.setInt(5, b.getNumeroMarce());
                statement.setNull(6, Types.DOUBLE); //vel max è null per bici
            }
            else if(v instanceof Monopattino m){
                statement.setString(4, "MONOPATTINO");
                statement.setNull(5, Types.INTEGER); //num marec è null per monopattino
                statement.setDouble(6, m.getVelocitaMassima());
            }

            statement.execute();
        }
        catch (SQLException e){
            throw new ErroreDatabaseException("Insert fallito: "+ e.getMessage(), e);
        }
        /*
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            //setta i parametri
            statement.setString(1, v.getCodice());
            statement.setInt(2, v.getBatteria());
            statement.setInt(3, v.getStato());
            statement.setInt(4, );

            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Insert fallito: "+ e.getMessage());
        }
        */

    }

    public void insertUtente(Utente u) throws ErroreDatabaseException{
        //test connessione

        if(! testConnection()){
            throw new RuntimeException("Impossibile connettersi al db");
        }

        //preparazione query
        String query = "INSERT INTO Utente (Id, Nome, Email, CodiceFiscale, Credito) VALUES (?, ?, ?, ?, ?)";
        //println(query);

        //esecuzione query
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, u.getId());
            statement.setString(2, u.getNome());
            statement.setString(3, u.getEmail());
            statement.setString(4, u.getCodiceFiscale());
            statement.setDouble(5, u.getCredito());
            statement.execute(); //executeUpdate()???
        } catch (SQLException e) {
            throw new ErroreDatabaseException("Insert fallito: " + e.getMessage(), e);
        }
    }

    public void insertNoleggio(Noleggio n) throws ErroreDatabaseException{
        //test connessione

        if(! testConnection()){
            throw new RuntimeException("Impossibile connettersi al db");
        }

        //preparazione query
        String query = "INSERT INTO Noleggio (IdUtente, CodiceVeicolo, OraInizio, OraFine) VALUES (?, ?, ?, ?)";
        //println(query);

        try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1, n.getUtente().getId());
            statement.setString(2, n.getVeicolo().getCodice());
            statement.setString(3, n.getOraInizio().toString());

            if(n.getOraFine() == null){
                statement.setNull(4, Types.VARCHAR);
            }
            else{
                statement.setString(4, n.getOraFine().toString());
            }
            statement.execute();
        }catch (SQLException  e){
            throw new ErroreDatabaseException ("Insert fallito: " + e.getMessage(), e);
        }

    }

    public void updateUtente(Utente u)throws ErroreDatabaseException{
        //test connessione

        if(! testConnection()){
            throw new RuntimeException("Impossibile connettersi al db");
        }

        //preparazione query
        String query = "UPDATE Utente SET Credito = ? WHERE Id= ?";
        //println(query);

        //esecuzione query
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDouble(1, u.getCredito());
            statement.setInt(2, u.getId());
            statement.execute(); //executeUpdate()???
        } catch (SQLException e) {
            throw new ErroreDatabaseException("Insert fallito: " + e.getMessage(), e);
        }
    }

    public void updateVeicolo(Veicolo v) throws ErroreDatabaseException{
        //test connessione

        if(! testConnection()){
            throw new RuntimeException("Impossibile connettersi al db");
        }

        //preparazione query
        String query = "UPDATE Veicolo SET Batteria = ?, Stato = ?  WHERE Codice= ?";
        //println(query);

        //esecuzione query
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, v.getBatteria());
            statement.setString(2, v.getStato().name());
            statement.setString(3, v.getCodice());
            statement.execute(); //executeUpdate()???
        } catch (SQLException e) {
            throw new ErroreDatabaseException("Insert fallito: " + e.getMessage(),e );
        }
    }

    public void updateNoleggio(Noleggio n) throws ErroreDatabaseException{
        //test connessione

        if(! testConnection()){
            throw new RuntimeException("Impossibile connettersi al db");
        }

        //preparazione query
        String query = "UPDATE Noleggio SET OraFine = ? WHERE IdUtente= ? AND CodiceVeicolo = ? AND OraInizio = ?";
        //println(query);

        //esecuzione query
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, n.getOraFine().toString());
            statement.setInt(2, n.getUtente().getId());
            statement.setString(3, n.getVeicolo().getCodice());
            statement.setString(4, n.getOraInizio().toString());
            statement.execute(); //executeUpdate()???
        } catch (SQLException e) {
            throw new ErroreDatabaseException("Insert fallito: " + e.getMessage(), e);
        }
    }

    private boolean testConnection(){
        try {
            return connection.isValid(5);
        } catch (SQLException e) {
            return false;
        }
    }
}
