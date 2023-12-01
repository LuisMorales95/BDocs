package com.beedocs.model;

public class ModelOficio {
    
    private String id_oficio;
    private String cla_oficio;
    private String nom_oficio;
    private String asu_oficio;
    private String ubi_oficio;
    private String nota_oficio;
    private String depenviada;
    private String fkid_depepartamento;
    private String fkid_estado;
    private String fkid_persona;
    private String RutaOficioP;
    private String RutaOficioR;
    private String nom_personaVA;
    
    public ModelOficio() {
    }
    
    public ModelOficio(String id_oficio, String cla_oficio, String nom_oficio, String asu_oficio, String ubi_oficio, String nota_oficio, String depenviada, String fkid_depepartamento, String fkid_estado, String fkid_persona, String rutaOficioP, String rutaOficioR, String nom_personaVA) {
        this.id_oficio = id_oficio;
        this.cla_oficio = cla_oficio;
        this.nom_oficio = nom_oficio;
        this.asu_oficio = asu_oficio;
        this.ubi_oficio = ubi_oficio;
        this.nota_oficio = nota_oficio;
        this.depenviada = depenviada;
        this.fkid_depepartamento = fkid_depepartamento;
        this.fkid_estado = fkid_estado;
        this.fkid_persona = fkid_persona;
        RutaOficioP = rutaOficioP;
        RutaOficioR = rutaOficioR;
        this.nom_personaVA = nom_personaVA;
    }
    
    public String getId_oficio() {
        return id_oficio;
    }
    
    public void setId_oficio(String id_oficio) {
        this.id_oficio = id_oficio;
    }
    
    public String getCla_oficio() {
        return cla_oficio;
    }
    
    public void setCla_oficio(String cla_oficio) {
        this.cla_oficio = cla_oficio;
    }
    
    public String getNom_oficio() {
        return nom_oficio;
    }
    
    public void setNom_oficio(String nom_oficio) {
        this.nom_oficio = nom_oficio;
    }
    
    public String getAsu_oficio() {
        return asu_oficio;
    }
    
    public void setAsu_oficio(String asu_oficio) {
        this.asu_oficio = asu_oficio;
    }
    
    public String getUbi_oficio() {
        return ubi_oficio;
    }
    
    public void setUbi_oficio(String ubi_oficio) {
        this.ubi_oficio = ubi_oficio;
    }
    
    public String getNota_oficio() {
        return nota_oficio;
    }
    
    public void setNota_oficio(String nota_oficio) {
        this.nota_oficio = nota_oficio;
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
    
    public String getRutaOficioP() {
        return RutaOficioP;
    }
    
    public void setRutaOficioP(String rutaOficioP) {
        RutaOficioP = rutaOficioP;
    }
    
    public String getRutaOficioR() {
        return RutaOficioR;
    }
    
    public void setRutaOficioR(String rutaOficioR) {
        RutaOficioR = rutaOficioR;
    }
    
    public String getNom_personaVA() {
        return nom_personaVA;
    }
    
    public void setNom_personaVA(String nom_personaVA) {
        this.nom_personaVA = nom_personaVA;
    }
}
