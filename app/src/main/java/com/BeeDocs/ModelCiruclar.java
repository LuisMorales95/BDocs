package com.BeeDocs;

public class ModelCiruclar {
    private String id_circular;
    private String cla_circular;
    private String nom_circular;
    private String asu_circular;
    private String ubi_circular;
    private String nota_circular;
    private String depenviada;
    private String fkid_depepartamento;
    private String fkid_estado;
    private String fkid_persona;
    private String RutaCircularP;
    private String RutaCircularR;
    private String nom_personaVA;
    
    public ModelCiruclar() {
    }
    
    public ModelCiruclar(String id_circular, String cla_circular, String nom_circular, String asu_circular, String ubi_circular, String nota_circular, String depenviada, String fkid_depepartamento, String fkid_estado, String fkid_persona, String rutaCircularP, String rutaCircularR, String
            nom_personaVA) {
        this.id_circular = id_circular;
        this.cla_circular = cla_circular;
        this.nom_circular = nom_circular;
        this.asu_circular = asu_circular;
        this.ubi_circular = ubi_circular;
        this.nota_circular = nota_circular;
        this.depenviada = depenviada;
        this.fkid_depepartamento = fkid_depepartamento;
        this.fkid_estado = fkid_estado;
        this.fkid_persona = fkid_persona;
        RutaCircularP = rutaCircularP;
        RutaCircularR = rutaCircularR;
        this.nom_personaVA = nom_personaVA;
    }
    
    public String getId_circular() {
        return id_circular;
    }
    
    public void setId_circular(String id_circular) {
        this.id_circular = id_circular;
    }
    
    public String getCla_circular() {
        return cla_circular;
    }
    
    public void setCla_circular(String cla_circular) {
        this.cla_circular = cla_circular;
    }
    
    public String getNom_circular() {
        return nom_circular;
    }
    
    public void setNom_circular(String nom_circular) {
        this.nom_circular = nom_circular;
    }
    
    public String getAsu_circular() {
        return asu_circular;
    }
    
    public void setAsu_circular(String asu_circular) {
        this.asu_circular = asu_circular;
    }
    
    public String getUbi_circular() {
        return ubi_circular;
    }
    
    public void setUbi_circular(String ubi_circular) {
        this.ubi_circular = ubi_circular;
    }
    
    public String getNota_circular() {
        return nota_circular;
    }
    
    public void setNota_circular(String nota_circular) {
        this.nota_circular = nota_circular;
    }
    
    public String getDepenviada() {
        return depenviada;
    }
    
    public void setDepenviada(String depenviada) {
        this.depenviada = depenviada;
    }
    
    public String getFkid_depepartamento() {
        return fkid_depepartamento;
    }
    
    public void setFkid_depepartamento(String fkid_depepartamento) {
        this.fkid_depepartamento = fkid_depepartamento;
    }
    
    public String getFkid_estado() {
        return fkid_estado;
    }
    
    public void setFkid_estado(String fkid_estado) {
        this.fkid_estado = fkid_estado;
    }
    
    public String getFkid_persona() {
        return fkid_persona;
    }
    
    public void setFkid_persona(String fkid_persona) {
        this.fkid_persona = fkid_persona;
    }
    
    public String getRutaCircularP() {
        return RutaCircularP;
    }
    
    public void setRutaCircularP(String rutaCircularP) {
        RutaCircularP = rutaCircularP;
    }
    
    public String getRutaCircularR() {
        return RutaCircularR;
    }
    
    public void setRutaCircularR(String rutaCircularR) {
        RutaCircularR = rutaCircularR;
    }
    
    public String getNom_personaVA() {
        return nom_personaVA;
    }
    
    public void setNom_personaVA(String nom_personaVA) {
        this.nom_personaVA = nom_personaVA;
    }
}
