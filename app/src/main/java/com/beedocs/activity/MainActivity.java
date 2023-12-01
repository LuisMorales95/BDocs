package com.beedocs.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.BeeDocs.R;
import com.beedocs.fragment.Officios;
import com.beedocs.fragment.Memoran;
import com.beedocs.utils.SharedPreference;
import com.beedocs.BeeDocsApplication;
import com.beedocs.dialog.AlertDFont;
import com.beedocs.fragment.Circular;
import com.beedocs.fragment.DepartamentosAsignados;
import com.beedocs.fragment.DepartamentosAsignadosCircular;
import com.beedocs.fragment.DepartamentosAsignadosMemoran;
import com.beedocs.utils.Constant;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import static com.beedocs.utils.Constant.SPNomenclaturaCircular;
import static com.beedocs.utils.Constant.SPNomenclaturaMemoran;
import static com.beedocs.utils.Constant.SPNomenclaturaOficio;
import static com.beedocs.utils.Constant.WS_SelectDepartamento;
import static com.beedocs.utils.SharedPreference.GETSharedPreferences;
import static com.beedocs.utils.SharedPreference.SETSharedPreferences;

public class MainActivity extends AppCompatActivity
        implements
        Officios.OnFragmentInteractionListener,
        DepartamentosAsignados.OnFragmentInteractionListener,
        Memoran.OnFragmentInteractionListener,
        Circular.OnFragmentInteractionListener {

    ImageButton MainActivity_Add_Usuarios,MainActivity_Add_Departamento,MainActivity_Close;
    public static String[] departamentos;
    public static String[] id_departamentos;
    static String[] clave_departamento;
    static String[] ncla_departamento;
    
    AlertDialog alert;
    
    FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    
        if (savedInstanceState==null){
            Fragment fragment = null;
            Class fragmentClass = null;
            if (GETSharedPreferences(SPNomenclaturaOficio, "").equals("")) {
                fragmentClass = DepartamentosAsignados.class;
            }else{
                fragmentClass = Officios.class;
            }
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            fragmentManager.beginTransaction().replace(R.id.MainFragmentContainer,fragment).commit();
        }
        
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        MainActivity_Add_Usuarios = (ImageButton) findViewById(R.id.MainActivity_Add_Usuarios);
        MainActivity_Add_Departamento = (ImageButton) findViewById(R.id.MainActivity_Add_Departamento);
        
        
        
        MainActivity_Close = (ImageButton) findViewById(R.id.MainActivity_Close);
        MainActivity_Close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 new AlertDFont.Builder(MainActivity.this)
                        .setMessage("Esta cerrando sesion \n ¿Continuar?")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SETSharedPreferences(Constant.SPId_persona,"");
                                SETSharedPreferences(Constant.SPNombre_persona,"");
                                SETSharedPreferences(Constant.SPApellidoP_persona,"");
                                SETSharedPreferences(Constant.SPApellidoM_persona,"");
                                SETSharedPreferences(Constant.SPTelefono_persona,"");
                                SETSharedPreferences(Constant.SPCorreo_persona,"");
                                SETSharedPreferences(Constant.SPContraseña_persona,"");
                                SETSharedPreferences(Constant.SPToken,"");
                                SETSharedPreferences(Constant.SPRol,"");
                                SETSharedPreferences(Constant.SPUsuario_persona,"");
                                SETSharedPreferences(Constant.SPID_Oficio,"");
                                SETSharedPreferences(Constant.SPID_Memoran,"");
                                SETSharedPreferences(Constant.SPID_Circular,"");
                                SETSharedPreferences(Constant.SPNomenclaturaOficio,"");
                                SETSharedPreferences(Constant.SPNomenclaturaMemoran,"");
                                SETSharedPreferences(Constant.SPNomenclaturaCircular,"");
                                
                                finish();
                                startActivity(new Intent(MainActivity.this, SignIn.class));
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
        if (Integer.valueOf(SharedPreference.GETSharedPreferences(Constant.SPRol,""))==1||
                Integer.valueOf(SharedPreference.GETSharedPreferences(Constant.SPRol,""))==2){
            MainActivity_Add_Usuarios.setVisibility(View.INVISIBLE);
            MainActivity_Add_Departamento.setVisibility(View.INVISIBLE);
        }
        MainActivity_Add_Usuarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UsersActivation.class));
            }
        });
        MainActivity_Add_Departamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(MainActivity.this, NuevoDepartamento.class));
            }
        });
        SelectDepartamento();
    }
    
    
    private void SelectDepartamento() {
        JsonArrayRequest Req = new JsonArrayRequest(
                Request.Method.POST,
                WS_SelectDepartamento,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("WS-Response", response.toString());
                        try {
                            departamentos = new String[response.length()];
                            id_departamentos = new String[response.length()];
                            clave_departamento = new String[response.length()];
                            ncla_departamento = new String[response.length()];
                            for (int i = 0; i < response.length(); i++) {
                                id_departamentos[i] = response.getJSONObject(i).getString("id_departamento");
                                departamentos[i] = response.getJSONObject(i).getString("nom_departamento");
                                clave_departamento[i] = response.getJSONObject(i).getString("cla_departamento");
                                ncla_departamento[i] = response.getJSONObject(i).getString("ncla_departamento");
                            }
                            
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("TAG", "Error Volley: " + error.getCause());
                        new AlertDFont.Builder(MainActivity.this).setMessage(error.getCause().toString()).show();
                    }
                }
        
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("Accept", "application/json");
                return headers;
            }
            
        };
        Req.setRetryPolicy(new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        BeeDocsApplication.Companion.getInstance().getVolleyConnection().getImageLoader().addToRequestQueue(Req);
    }
    
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            Class FragmentClass = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if (GETSharedPreferences(SPNomenclaturaOficio, "").equals("")) {
                        FragmentClass = DepartamentosAsignados.class;
                    }else{
                        FragmentClass = Officios.class;
                    }if (FragmentClass != null) {
                        try {
                            fragment = (Fragment) FragmentClass.newInstance();
            
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        fragmentManager.beginTransaction().replace(R.id.MainFragmentContainer, fragment).commit();
                    }
                    return true;
                case R.id.navigation_dashboard:
                    if (GETSharedPreferences(SPNomenclaturaMemoran, "").equals("")) {
                        FragmentClass = DepartamentosAsignadosMemoran.class;
                    }else{
                        FragmentClass = Memoran.class;
                    }
                    if (FragmentClass != null) {
                        try {
                            fragment = (Fragment) FragmentClass.newInstance();
            
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        fragmentManager.beginTransaction().replace(R.id.MainFragmentContainer, fragment).commit();
                    }
                    return true;
                case R.id.navigation_notifications:
                    if (GETSharedPreferences(SPNomenclaturaCircular, "").equals("")) {
                        FragmentClass = DepartamentosAsignadosCircular.class;
                    }else{
                        FragmentClass = Circular.class;
    
                    }
                    if (FragmentClass != null) {
                        try {
                            fragment = (Fragment) FragmentClass.newInstance();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        fragmentManager.beginTransaction().replace(R.id.MainFragmentContainer, fragment).commit();
                    }
                    return true;
            }
            return false;
        }
    };
    
    @Override
    public void onFragmentInteraction(Uri uri) {
    
    }
}
