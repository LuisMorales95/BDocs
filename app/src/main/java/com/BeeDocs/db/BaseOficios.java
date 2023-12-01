package com.BeeDocs.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BaseOficios extends SQLiteOpenHelper {
    
    private static final int VersionBase = 1;
    private static final String NombreBase = "BaseOficios";
    
    private static final String TABLA_PERSONAS = "CREATE TABLE personas"+
            "(id_persona INTEGER PRIMARY KEY AUTOINCREMENT,"+
            "nom_persona TEXT)";
    private static final String TABLA_DEP_SOLICITANTE = "CREATE TABLE dep_solicitante"+
            "(id_dep_sol INTEGER PRIMARY KEY AUTOINCREMENT,"+
            "nom_dep_sol TEXT)";
    private static final String TABLA_DEP_ENVIADA = "CREATE TABLE dep_enviada"+
            "(id_dep_envi INTEGER PRIMARY KEY AUTOINCREMENT,"+
            "nom_dep_envi TEXT)";
    private static final String TABLA_ASUNTOS = "CREATE TABLE asuntos"+
            "(id_asunto INTEGER PRIMARY KEY AUTOINCREMENT,"+
            "nom_asunto TEXT)";
    private static final String TABLA_ESTADOS = "CREATE TABLE estados"+
            "(id_estado INTEGER PRIMARY KEY AUTOINCREMENT,"+
            "nom_estado TEXT)";
    private static final String TABLA_UBICACION = "CREATE TABLE ubicacion"+
            "(id_ubica INTEGER PRIMARY KEY AUTOINCREMENT,"+
            "nom_ubica TEXT)";
    private static final String TABLA_NOTAS = "CREATE TABLE notas"+
            "(id_nota INTEGER PRIMARY KEY AUTOINCREMENT,"+
            "nom_nota TEXT)";
    
    private static final String TABLA_OFICIOCOMPLETO = "CREATE TABLE oficio"+
            "(id_oficio INTEGER PRIMARY KEY AUTOINCREMENT,"+
            "fecha_oficio TEXT,"+
            "fk_id_persona INT,"+
            "fk_id_dep_sol INT,"+
            "fk_id_dep_envi INT,"+
            "fk_id_asunto INT,"+
            "fk_id_estado INT,"+
            "fk_id_ubica INT,"+
            "fk_id_nota INT)";
    
    public BaseOficios(Context context) {
        super(context, NombreBase, null, VersionBase);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLA_PERSONAS);
        db.execSQL(TABLA_DEP_SOLICITANTE);
        db.execSQL(TABLA_DEP_ENVIADA);
        db.execSQL(TABLA_ASUNTOS);
        db.execSQL(TABLA_ESTADOS);
        db.execSQL(TABLA_UBICACION);
        db.execSQL(TABLA_NOTAS);
        db.execSQL(TABLA_OFICIOCOMPLETO);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
    
    public  String [] Return_AutoCompletado(String [] Columnas, String Tabla){
        String [] INFO ={};
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(Tabla,Columnas,null,null,null,null,null,null);
        cursor.moveToFirst();
        if (cursor.getCount()>0){
            System.out.println(Tabla+" Contiene elementos");
            INFO = new String[cursor.getCount()];
            Integer count = 0;
            do {
                INFO[count] = cursor.getString(1);
                count++;
            } while (cursor.moveToNext());
        }else{
            System.out.println(Tabla+" Esta Vacio");
        }
        database.close();
        cursor.close();
    
        return INFO;
    }
    
    public void SearchAddIfExists(String Attribute, String Tabla, String [] columnas_recuperar){
        int Existe =0;
        
        SQLiteDatabase sqLiteReadableDatabase = getReadableDatabase();
        Cursor cursor = sqLiteReadableDatabase.query(Tabla, columnas_recuperar, null, null, null, null, null, null);
        
        cursor.moveToFirst();
        if (cursor.getCount()>0){
            do {
                if (cursor.getString(1).equals(Attribute)) {
                    Existe = 1;
                }
        
            } while (cursor.moveToNext());
        }
        
        
        if (Existe==0 || cursor.getCount()==0){
            SQLiteDatabase sqLiteWritableDatabase = getWritableDatabase();
            if (sqLiteWritableDatabase!=null){
                ContentValues contentValues = new ContentValues();
                contentValues.put(columnas_recuperar[1],Attribute);
                sqLiteWritableDatabase.insert(Tabla,null,contentValues);
            }
            sqLiteWritableDatabase.close();
        }
        sqLiteReadableDatabase.close();
        cursor.close();
    
    }
}
