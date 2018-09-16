package com.BeeDocs;

public class ModelDepartment {
    private String id_department;
    private String nom_department;
    private String clave;
    private String ncla_department;
    
    public ModelDepartment() {
    }
    
    public ModelDepartment(String id_department, String nom_department, String clave, String ncla_department) {
        this.id_department = id_department;
        this.nom_department = nom_department;
        this.clave = clave;
        this.ncla_department = ncla_department;
    }
    
    public String getId_department() {
        return id_department;
    }
    
    public void setId_department(String id_department) {
        this.id_department = id_department;
    }
    
    public String getNom_department() {
        return nom_department;
    }
    
    public void setNom_department(String nom_department) {
        this.nom_department = nom_department;
    }
    
    public String getClave() {
        return clave;
    }
    
    public void setClave(String clave) {
        this.clave = clave;
    }
    
    public String getNcla_department() {
        return ncla_department;
    }
    
    public void setNcla_department(String ncla_department) {
        this.ncla_department = ncla_department;
    }
}
