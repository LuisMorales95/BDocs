package com.BeeDocs;

public class ModelMemoran {
    
    private String id_memoran;
    private String cla_memoran;
    private String nom_memoran;
    private String asu_memoran;
    private String ubi_memoran;
    private String nota_memoran;
    private String depenviada;
    private String fkid_depepartamento;
    private String fkid_estado;
    private String fkid_persona;
    private String RutaMemoranP;
    private String RutaMemoranR;
    private String nom_personaVA;
    
    public ModelMemoran() {
    }
    
    public ModelMemoran(String id_memoran, String cla_memoran, String nom_memoran, String asu_memoran, String ubi_memoran, String nota_memoran, String depenviada, String fkid_depepartamento, String fkid_estado, String fkid_persona, String rutaMemoranP, String rutaMemoranR, String nom_personaVA) {
        this.id_memoran = id_memoran;
        this.cla_memoran = cla_memoran;
        this.nom_memoran = nom_memoran;
        this.asu_memoran = asu_memoran;
        this.ubi_memoran = ubi_memoran;
        this.nota_memoran = nota_memoran;
        this.depenviada = depenviada;
        this.fkid_depepartamento = fkid_depepartamento;
        this.fkid_estado = fkid_estado;
        this.fkid_persona = fkid_persona;
        RutaMemoranP = rutaMemoranP;
        RutaMemoranR = rutaMemoranR;
        this.nom_personaVA = nom_personaVA;
    }
    
    public String getId_memoran() {
        return id_memoran;
    }
    
    public void setId_memoran(String id_memoran) {
        this.id_memoran = id_memoran;
    }
    
    public String getCla_memoran() {
        return cla_memoran;
    }
    
    public void setCla_memoran(String cla_memoran) {
        this.cla_memoran = cla_memoran;
    }
    
    public String getNom_memoran() {
        return nom_memoran;
    }
    
    public void setNom_memoran(String nom_memoran) {
        this.nom_memoran = nom_memoran;
    }
    
    public String getAsu_memoran() {
        return asu_memoran;
    }
    
    public void setAsu_memoran(String asu_memoran) {
        this.asu_memoran = asu_memoran;
    }
    
    public String getUbi_memoran() {
        return ubi_memoran;
    }
    
    public void setUbi_memoran(String ubi_memoran) {
        this.ubi_memoran = ubi_memoran;
    }
    
    public String getNota_memoran() {
        return nota_memoran;
    }
    
    public void setNota_memoran(String nota_memoran) {
        this.nota_memoran = nota_memoran;
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
    
    public String getRutaMemoranP() {
        return RutaMemoranP;
    }
    
    public void setRutaMemoranP(String rutaMemoranP) {
        RutaMemoranP = rutaMemoranP;
    }
    
    public String getRutaMemoranR() {
        return RutaMemoranR;
    }
    
    public void setRutaMemoranR(String rutaMemoranR) {
        RutaMemoranR = rutaMemoranR;
    }
    
    public String getNom_personaVA() {
        return nom_personaVA;
    }
    
    public void setNom_personaVA(String nom_personaVA) {
        this.nom_personaVA = nom_personaVA;
    }
}
