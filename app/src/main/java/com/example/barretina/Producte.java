package com.example.barretina;

public class Producte {
    private String id;
    private String nom;
    private String descripcio;
    private String imatge;
    private double preu;
    private int quantiat;



    public Producte(String id, String nom, String descripcio, String imatge, double preu,  int quantiat ) {
        this.id = id;
        this.nom = nom;
        this.descripcio = descripcio;
        this.imatge = imatge;
        this.preu = preu;
        this.quantiat = quantiat;
    }

    public String getId() { return id; }
    public String getNom() { return nom; }
    public String getDescripcio() { return descripcio; }
    public String getImatge() { return imatge; }
    public double getPreu() { return preu; }
    public int getQuantiat() { return quantiat; }
    public void setQuantiat(int quantiat) {  this.quantiat=quantiat; }
}

