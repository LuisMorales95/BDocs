package com.beedocs.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.BeeDocs.R;
import com.beedocs.model.Info_Personas;
import com.beedocs.BeeDocsApplication;
import com.beedocs.dialog.AlertDFont;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.beedocs.utils.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Asign_Departamentos extends AppCompatActivity {
    ArrayAdapter<String> adapterdepartamentos;
    
    static Info_Personas Persona;
    static String[] Id_departamento = MainActivity.id_departamentos, Departamentos = MainActivity.departamentos;
    private ImageButton AsignDep_Agregar;
    private Spinner AsignDep_Departamentos;
    private ListView AsignDep_Listas;
    List<departamentosBD> ListDepsBD;
    CustomListDepartamentos customListDepartamentos;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asign_departamentos);
        AsignDep_Departamentos = (Spinner) findViewById(R.id.AsignDep_Departamentos);
        AsignDep_Agregar = (ImageButton) findViewById(R.id.AsignDep_Agregar);
        AsignDep_Listas = (ListView) findViewById(R.id.AsignDep_Listas);
        Desps_Asignados(Persona.getId_persona(),Id_departamento,Departamentos);
    }
    
    public void Desps_Asignados(final String id_Persona, final String[] id_departamentosTodos, final String[] departamentosTodos) {
        ProgressDialog.Builder builder = new ProgressDialog.Builder(Asign_Departamentos.this);
        builder.setMessage("Analizando Informacion...");
        builder.setCancelable(false);
        final android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
        
        HashMap<String, String> map = new HashMap<>();
        map.put("id_persona", id_Persona);
        JSONObject object = new JSONObject(map);
        Log.e("HashMap", object.toString());
        JsonArrayRequest Req = new JsonArrayRequest(
                Request.Method.POST,
                Constant.WS_SelectTieneDep,
                object,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        alertDialog.dismiss();
                        Log.e("WS-Response", response.toString());
                        ListDepsBD = new ArrayList<departamentosBD>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                departamentosBD depBD = new departamentosBD();
                                depBD.setId_TieneDep(response.getJSONObject(i).getString("id_TieneDep"));
                                depBD.setFk_id_departamento(response.getJSONObject(i).getString("fk_id_departamento"));
                                depBD.setFk_id_usuario(response.getJSONObject(i).getString("fk_id_usuario"));
                                ListDepsBD.add(depBD);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        List<departam> deps = new ArrayList<departam>();
                        final List<departam> depsasignados = new ArrayList<departam>();
                        for (int y=0;y<id_departamentosTodos.length;y++){
                            int se_encuentra=0;
                            for (int z=0;z<ListDepsBD.size();z++){
                                if (id_departamentosTodos[y].equals(ListDepsBD.get(z).getFk_id_departamento())){
                                    se_encuentra=1;
                                }
                            }
                            if (se_encuentra==0){
                                departam departam = new departam();
                                departam.setIddeps(id_departamentosTodos[y]);
                                departam.setNomdeps(departamentosTodos[y]);
                                deps.add(departam);
                            }else{
                                departam departam = new departam();
                                departam.setIddeps(id_departamentosTodos[y]);
                                departam.setNomdeps(departamentosTodos[y]);
                                depsasignados.add(departam);
                            }
                        }
                        final String [] Iddeps = new String[deps.size()];
                        final String [] Nomdeps = new String[deps.size()];
                        for (int i=0;i<deps.size();i++){
                            Iddeps[i]=deps.get(i).getIddeps();
                            Nomdeps[i]=deps.get(i).getNomdeps();
                        }
                        adapterdepartamentos = new ArrayAdapter<String>(Asign_Departamentos.this, R.layout.spinner_item, Nomdeps);
                        AsignDep_Departamentos.setAdapter(adapterdepartamentos);
                        AsignDep_Departamentos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                                AsignDep_Agregar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        insertDepartamento(Persona.getId_persona(),Iddeps[position]);
                                    }
                                });
                            }
                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        customListDepartamentos = new CustomListDepartamentos(Asign_Departamentos.this,depsasignados);
                        AsignDep_Listas.setAdapter(customListDepartamentos);
                        AsignDep_Listas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                delete_AsignaDepartamento(Persona.getId_persona(),depsasignados.get(position).getIddeps());
                                
                            }
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        alertDialog.dismiss();
                        Log.d("TAG", "Error Volley: " + error.getCause());
                        new AlertDFont.Builder(Asign_Departamentos.this).setMessage(error.getCause().toString()).show();
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
    public class departam{
        String iddeps,nomdeps;
    
        public departam() {
        }
    
        public departam(String iddeps, String nomdeps) {
            this.iddeps = iddeps;
            this.nomdeps = nomdeps;
        }
    
        public String getIddeps() {
            return iddeps;
        }
    
        public void setIddeps(String iddeps) {
            this.iddeps = iddeps;
        }
    
        public String getNomdeps() {
            return nomdeps;
        }
    
        public void setNomdeps(String nomdeps) {
            this.nomdeps = nomdeps;
        }
    }
    public class departamentosBD{
        String id_TieneDep, fk_id_departamento, fk_id_usuario;
    
        public departamentosBD() {
        }
    
        public departamentosBD(String id_TieneDep, String fk_id_departamento, String fk_id_usuario) {
            this.id_TieneDep = id_TieneDep;
            this.fk_id_departamento = fk_id_departamento;
            this.fk_id_usuario = fk_id_usuario;
        }
    
        public String getId_TieneDep() {
            return id_TieneDep;
        }
    
        public void setId_TieneDep(String id_TieneDep) {
            this.id_TieneDep = id_TieneDep;
        }
    
        public String getFk_id_departamento() {
            return fk_id_departamento;
        }
    
        public void setFk_id_departamento(String fk_id_departamento) {
            this.fk_id_departamento = fk_id_departamento;
        }
    
        public String getFk_id_usuario() {
            return fk_id_usuario;
        }
    
        public void setFk_id_usuario(String fk_id_usuario) {
            this.fk_id_usuario = fk_id_usuario;
        }
    }
    
    public void insertDepartamento(String id_persona, String id_departamento){
        ProgressDialog.Builder builder = new ProgressDialog.Builder(Asign_Departamentos.this);
        builder.setMessage("Asignando Departamento...");
        builder.setCancelable(false);
        final android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
        
        HashMap<String, String> map = new HashMap<>();
        map.put("id_persona", id_persona);
        map.put("id_departamento", id_departamento);
        JSONObject object = new JSONObject(map);
        Log.e("HashMap", object.toString());
        JsonObjectRequest Req = new JsonObjectRequest(
                Request.Method.POST,
                Constant.WS_SubirAsignarDepartamento,
                object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            alertDialog.dismiss();
                            Log.e("WS-Response",response.toString());
                            if (response.getString("Response").equals("Success")){
                                new AlertDFont.Builder(Asign_Departamentos.this)
                                        .setMessage("Informacion Guardada")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        reload();
                                    }
                                }).show();
                            }else{
                                // TODO: Guardar localmente
                                new AlertDFont.Builder(Asign_Departamentos.this)
                                        .setMessage("Su solicitud no se ha podido realizar con exito.")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        alertDialog.dismiss();
                        Log.d("TAG", "Error Volley: " + error.getCause());
                        new AlertDFont.Builder(Asign_Departamentos.this).setMessage(error.getCause().toString()).show();
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
    public void reload(){
        if (Build.VERSION.SDK_INT >= 11) {
            recreate();
        } else {
            Intent intent = getIntent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            overridePendingTransition(0, 0);
        
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
    }
    
    
    public class CustomListDepartamentos extends BaseAdapter{
    
        ImageLoader imageLoader = BeeDocsApplication.Companion.getInstance().getVolleyConnection().getImageLoader().getImageLoader();
        private LayoutInflater inflater;
        private Activity activity;
        private List<departam> departamentoAsignados;
        
        public CustomListDepartamentos(Activity activity, List<departam> DepartamentoAsignados) {
            this.activity = activity;
            this.departamentoAsignados = DepartamentoAsignados;
        }
    
        @Override
        public int getCount() {
            return departamentoAsignados.size();
        }
    
        @Override
        public Object getItem(int position) {
            return departamentoAsignados.get(position);
        }
    
        @Override
        public long getItemId(int position) {
            return position;
        }
    
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (inflater == null) inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    
            if (convertView == null) convertView = inflater.inflate(android.R.layout.simple_list_item_1, null);
            
            TextView textView = convertView.findViewById(android.R.id.text1);
            
            departam depa = departamentoAsignados.get(position);
            textView.setText(depa.getNomdeps());
            
            return convertView;
        }
    }
    
    public void delete_AsignaDepartamento(String id_persona,String id_departamento){
        ProgressDialog.Builder builder = new ProgressDialog.Builder(Asign_Departamentos.this);
        builder.setMessage("Borrando Departamento Asignado...");
        builder.setCancelable(false);
        final android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    
        HashMap<String, String> map = new HashMap<>();
        map.put("id_persona", id_persona);
        map.put("id_departamento", id_departamento);
        JSONObject object = new JSONObject(map);
        Log.e("HashMap", object.toString());
        JsonObjectRequest Req = new JsonObjectRequest(
                Request.Method.POST,
                Constant.WS_BorrarAsignarDepartamento,
                object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            alertDialog.dismiss();
                            Log.e("WS-Response",response.toString());
                            if (response.getString("Response").equals("Success")){
                                new AlertDFont.Builder(Asign_Departamentos.this)
                                        .setMessage("Informacion Guardada")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                reload();
                                            }
                                        }).show();
                            }else{
                                // TODO: Guardar localmente
                                new AlertDFont.Builder(Asign_Departamentos.this)
                                        .setMessage("Su solicitud no se ha podido realizar con exito.")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        alertDialog.dismiss();
                        Log.d("TAG", "Error Volley: " + error.getCause());
                        new AlertDFont.Builder(Asign_Departamentos.this).setMessage(error.getCause().toString()).show();
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
    
}
