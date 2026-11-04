package org.example;

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
}
