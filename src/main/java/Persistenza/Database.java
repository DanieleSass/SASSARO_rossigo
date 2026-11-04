package Persistenza;

import org.example.Veicolo;
import org.example.stato;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static java.lang.IO.println;

public class Database {
    private Connection connection = null;
    private static Database instance = null;

    private Database() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:database.db");
    }

    public static Database getInstance() throws SQLException {
        if(instance==null){
            instance = new Database();
        }
        return instance;
    }

    public void insertVeicolo(Veicolo v){
        //test connessione

        if(! testConnection()){
            throw new RuntimeException("Impossibile connettersi al db");
        }

        //preparazione query
        String query = "INSERT INTO Veicolo (Codice, Batteria, Stato, TipoVeicolo, NumeroMarce, VelocitaMassima) VALUES (?,?,?,?,?,?)";
        println(query);

        //esecuzione query
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
    }

    public void insertUtente(String nome, String email, String codiceFiscale, double credito){
        //test connessione

        if(! testConnection()){
            throw new RuntimeException("Impossibile connettersi al db");
        }

        //preparazione query
        String query = "INSERT INTO Utente (Nome, Email, CodiceFiscale, Credito) VALUES (?,?,?, ?)";
        println(query);

        //esecuzione query
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            //setta i parametri
            statement.setString(1,nome);
            statement.setString(2,email);
            statement.setString(3,codiceFiscale);
            statement.setDouble(4,credito);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Insert fallito: "+ e.getMessage());
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
