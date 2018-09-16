package com.BeeDocs;

public class DepartamentoAsignado {
    String id_departamento,nom_departamento,cla_departamento,ncla_departamento;
    
    public DepartamentoAsignado() {
    }
    
    public DepartamentoAsignado(String id_departamento, String nom_departamento, String cla_departamento, String ncla_departamento) {
        this.id_departamento = id_departamento;
        this.nom_departamento = nom_departamento;
        this.cla_departamento = cla_departamento;
        this.ncla_departamento = ncla_departamento;
    }
    
    public String getId_departamento() {
        return id_departamento;
    }
    
    public void setId_departamento(String id_departamento) {
        this.id_departamento = id_departamento;
    }
    
    public String getNom_departamento() {
        return nom_departamento;
    }
    
    public void setNom_departamento(String nom_departamento) {
        this.nom_departamento = nom_departamento;
    }
    
    public String getCla_departamento() {
        return cla_departamento;
    }
    
    public void setCla_departamento(String cla_departamento) {
        this.cla_departamento = cla_departamento;
    }
    
    public String getNcla_departamento() {
        return ncla_departamento;
    }
    
    public void setNcla_departamento(String ncla_departamento) {
        this.ncla_departamento = ncla_departamento;
    }
}
