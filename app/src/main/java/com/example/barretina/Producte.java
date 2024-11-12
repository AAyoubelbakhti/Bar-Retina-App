package com.example.barretina;

public class Producte {
    private String id;
    private String nom;
    private String descripcio;
    private String imatge;
    private double preu;

    public Producte(String id, String nom, String descripcio, String imatge, double preu) {
        this.id = id;
        this.nom = nom;
        this.descripcio = descripcio;
        this.imatge = imatge;
        this.preu = preu;
    }

    public String getId() { return id; }
    public String getNom() { return nom; }
    public String getDescripcio() { return descripcio; }
    public String getImatge() { return imatge; }
    public double getPreu() { return preu; }
}

