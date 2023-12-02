package com.novanesttech.beedocs.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.novanesttech.beedocs.R;
import com.novanesttech.beedocs.BeeDocsApplication;
import com.novanesttech.beedocs.dialog.AlertDFont;
import com.novanesttech.beedocs.model.DepartamentoAsignado;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.novanesttech.beedocs.utils.Constant.WS_SelectAllDepartamentos;
import static com.novanesttech.beedocs.utils.Constant.WS_SubirDepartamento;
import static com.novanesttech.beedocs.utils.StringUtils.unaccent;

public class NuevoDepartamento extends AppCompatActivity {
    EditText NuevoDep_Nombre, NuevoDep_Clave;
    ImageButton NuevoDep_Agregar;
    ListView NuevoDep_Lista;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_departamento);
        NuevoDep_Nombre = (EditText) findViewById(R.id.NuevoDep_Nombre);
        NuevoDep_Clave = (EditText) findViewById(R.id.NuevoDep_Clave);
        NuevoDep_Clave.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        NuevoDep_Agregar = (ImageButton) findViewById(R.id.NuevoDep_Agregar);
        NuevoDep_Lista = (ListView) findViewById(R.id.NuevoDep_Lista);
        NuevoDep_Agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NuevoDep_Nombre.getText().toString().equals("")||NuevoDep_Clave.getText().toString().equals("")){
                    NuevoDep_Nombre.setError("Valor Invalido");
                    NuevoDep_Clave.setError("Valor Invalido");
                }else{
                    RegisterOficio(NuevoDep_Clave.getText().toString(),NuevoDep_Nombre.getText().toString(),NuevoDep_Clave.getText().toString()+"-HAADV-OFICIO-0000/2018");
                }
            }
        });
        DepartamentoAll();
    }
    public void DepartamentoAll() {
        ProgressDialog.Builder builder = new ProgressDialog.Builder(NuevoDepartamento.this);
        builder.setMessage("Buscando Departamentos...");
        builder.setCancelable(false);
        final android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
        
        HashMap<String, String> map = new HashMap<>();
        JSONObject object = new JSONObject(map);
        Log.e("HashMap", object.toString());
        JsonArrayRequest Req = new JsonArrayRequest(
                Request.Method.POST,
                WS_SelectAllDepartamentos,
                object,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        alertDialog.dismiss();
                        Log.e("WS-Response", response.toString());
                        try {
                            final List<DepartamentoAsignado> departamentoAsignadosList = new ArrayList<DepartamentoAsignado>();
                            for (int i = 0; i < response.length(); i++) {
                                DepartamentoAsignado departamentoAsignado = new DepartamentoAsignado();
                                departamentoAsignado.setId_departamento(response.getJSONObject(i).getString("id_departamento"));
                                departamentoAsignado.setNom_departamento(response.getJSONObject(i).getString("nom_departamento"));
                                departamentoAsignado.setCla_departamento(response.getJSONObject(i).getString("cla_departamento"));
                                departamentoAsignado.setNcla_departamento(response.getJSONObject(i).getString("ncla_departamento"));
                                departamentoAsignadosList.add(departamentoAsignado);
                            }
                            CustomAdapter customAdapter = new CustomAdapter(NuevoDepartamento.this,departamentoAsignadosList);
                            NuevoDep_Lista.setAdapter(customAdapter);
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
                        new AlertDFont.Builder(NuevoDepartamento.this).setMessage(error.getCause().toString()).show();
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
        BeeDocsApplication.Companion.getInstance().getVolleyConnection().addToRequestQueue(Req);
    }
    private void RegisterOficio(String Clave_departamento,
                                String Nombre_departamento,
                                String Ncla_departamento) {
        ProgressDialog.Builder builder = new ProgressDialog.Builder(NuevoDepartamento.this);
        builder.setMessage("Subiendo Departamento...");
        builder.setCancelable(false);
        final android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
        
        HashMap<String, String> map = new HashMap<>();
        map.put("Clave_departamento", unaccent(Clave_departamento));
        map.put("Nombre_departamento", unaccent(Nombre_departamento));
        map.put("Ncla_departamento",Ncla_departamento);
        JSONObject object = new JSONObject(map);
        Log.e("HashMap", object.toString());
        JsonObjectRequest Req = new JsonObjectRequest(
                Request.Method.POST,
                WS_SubirDepartamento,
                object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            alertDialog.dismiss();
                            Log.e("WS-Response",response.toString());
                            if (response.getString("Response").equals("Existe")) {
                                new AlertDFont.Builder(NuevoDepartamento.this)
                                        .setMessage("El Nombre y/o Clave ya estan registrados")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();
                            }else if (response.getString("Response").equals("Success")){
                                new AlertDFont.Builder(NuevoDepartamento.this)
                                        .setMessage("Informacion Guardada")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                NuevoDep_Nombre.setText("");
                                                NuevoDep_Clave.setText("");
                                                DepartamentoAll();
                                            }
                                        }).show();
                            }else{
                                // TODO: Guardar localmente
                                new AlertDFont.Builder(NuevoDepartamento.this)
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
                        new AlertDFont.Builder(NuevoDepartamento.this).setMessage(error.getCause().toString()).show();
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
        BeeDocsApplication.Companion.getInstance().getVolleyConnection().addToRequestQueue(Req);
    }
    
    
    
    
    public class CustomAdapter extends BaseAdapter {
        ImageLoader imageLoader = BeeDocsApplication.Companion.getInstance().getVolleyConnection().getImageLoader();
        private LayoutInflater inflater;
        private Activity activity;
        private List<DepartamentoAsignado> departamentoAsignadoList;
        
        public CustomAdapter(Activity activity, List<DepartamentoAsignado> departamentoAsignadoList) {
            this.activity = activity;
            this.departamentoAsignadoList = departamentoAsignadoList;
        }
        
        @Override
        public int getCount() {
            return departamentoAsignadoList.size();
        }
        
        @Override
        public Object getItem(int position) {
            return departamentoAsignadoList.get(position);
        }
        
        @Override
        public long getItemId(int position) {
            return position;
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (inflater == null) inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) convertView = inflater.inflate(android.R.layout.simple_list_item_2, null);
            TextView textView1 = convertView.findViewById(android.R.id.text1);
            TextView textView2 = convertView.findViewById(android.R.id.text2);
            
            DepartamentoAsignado departamentoAsignado = departamentoAsignadoList.get(position);
            textView1.setText(departamentoAsignado.getNom_departamento());
            textView1.setTextColor(Color.GRAY);
            textView2.setText(departamentoAsignado.getCla_departamento()+"  "+departamentoAsignado.getNcla_departamento());
            textView2.setTextColor(Color.GRAY);
            return convertView;
        }
    }
    
    
}
