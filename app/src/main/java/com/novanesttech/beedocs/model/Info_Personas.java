package com.novanesttech.beedocs.model;

public class Info_Personas {
    String id_persona;
    String nom_persona;
    String usu_usuario;
    String fk_id_rol;
    String activo;
    
    public Info_Personas() {
    }
    
    public Info_Personas(String id_persona, String nom_persona, String usu_usuario, String fk_id_rol, String activo) {
        this.id_persona = id_persona;
        this.nom_persona = nom_persona;
        this.usu_usuario = usu_usuario;
        this.fk_id_rol = fk_id_rol;
        this.activo = activo;
    }
    
    public String getId_persona() {
        return id_persona;
    }
    
    public void setId_persona(String id_persona) {
        this.id_persona = id_persona;
    }
    
    public String getNom_persona() {
        return nom_persona;
    }
    
    public void setNom_persona(String nom_persona) {
        this.nom_persona = nom_persona;
    }
    
    public String getUsu_usuario() {
        return usu_usuario;
    }
    
    public void setUsu_usuario(String usu_usuario) {
        this.usu_usuario = usu_usuario;
    }
    
    public String getFk_id_rol() {
        return fk_id_rol;
    }
    
    public void setFk_id_rol(String fk_id_rol) {
        this.fk_id_rol = fk_id_rol;
    }
    
    public String getActivo() {
        return activo;
    }
    
    public void setActivo(String activo) {
        this.activo = activo;
    }
}
