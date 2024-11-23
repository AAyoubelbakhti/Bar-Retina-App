package com.example.barretina;

public class Producte {
    private String id;
    private String nom;
    private String descripcio;
    private String imatge;
    private double preu;
    private int quantitat;
    private int fotoResId;

    public Producte(String id, String nom, String descripcio, String imatge, double preu, int quantitat, int fotoResId) {
        this.id = id;
        this.nom = nom;
        this.descripcio = descripcio;
        this.imatge = imatge;
        this.preu = preu;
        this.quantitat = quantitat;
        this.fotoResId = fotoResId;
    }

    public String getId() { return id; }
    public String getNom() { return nom; }
    public String getDescripcio() { return descripcio; }
    public String getImatge() { return imatge; }
    public double getPreu() { return preu; }
    public int getQuantitat() { return quantitat; }
    public void setQuantitat(int quantitat) { this.quantitat = quantitat; }
    public int getFotoResId() {return fotoResId;}
}



