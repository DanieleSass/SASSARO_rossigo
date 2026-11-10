package Modello;

public class Utente {
    private int id;
    private String nome;
    private String email;
    private String codiceFiscale;
    private double credito;

    public Utente(int i, String n, String e, String c, double cr){
        id = i;
        nome = n;
        email = e;
        codiceFiscale = c;
        credito = cr;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public String getNome() {
        return nome;
    }

    public double getCredito() {
        return credito;
    }

    public String getEmail() {
        return email;
    }

    public int getId() {
        return id;
    }

    public void setCredito(double credito) {
        this.credito = credito;
    }

    public void AggiungiCredito (double quantita){
        credito+=quantita;
    }
}
