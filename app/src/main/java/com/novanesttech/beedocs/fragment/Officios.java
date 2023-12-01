package com.novanesttech.beedocs.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.BeeDocs.R;
import com.novanesttech.beedocs.activity.UpdateOfficio;
import com.novanesttech.beedocs.BeeDocsApplication;
import com.novanesttech.beedocs.activity.NuevoOfficio;
import com.novanesttech.beedocs.activity.NuevoOficioTrabajador;
import com.novanesttech.beedocs.db.BaseOficios;
import com.novanesttech.beedocs.model.DepartamentoAsignado;
import com.novanesttech.beedocs.model.ModelOficio;
import com.novanesttech.beedocs.utils.Constant;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.novanesttech.beedocs.utils.SharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class Officios extends Fragment {
    
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static String[] departamentos;
    public static String[] id_departamentos;
    public static String[] clave_departamento;
    static String[] ncla_departamento;
    public static String[] estados;
    public static String[] id_estados;
    static List<ModelOficio> modelOficioList;
    public static BaseOficios baseOficios;
    private static CustomListAdapter Adapter;
    ImageView Officios_crear;
    ListView Officios_ListaOficios;
    Spinner Officios_Departamento, Officios_Estado;
    private String mParam1;
    private String mParam2;
    private EditText Officios_Search;
    private List<ModelOficio> lista_temporal;
    private int RealorFake = 0;
    static DepartamentoAsignado departamentoAsignado;
    private OnFragmentInteractionListener mListener;
    
    public Officios() {
    }
    
    public static Officios newInstance(String param1, String param2) {
        Officios fragment = new Officios();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_officios, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        
        baseOficios = new BaseOficios(getActivity());
        Officios_crear = (ImageView) view.findViewById(R.id.Officios_crear);
        Officios_Departamento = (Spinner) view.findViewById(R.id.Officios_Departamento);
        Officios_Estado = (Spinner) view.findViewById(R.id.Officios_Estado);
        Officios_Search = (EditText) view.findViewById(R.id.Officios_Search);
        Officios_ListaOficios = (ListView) view.findViewById(R.id.Officios_ListaOficios);
        ((ImageView)view.findViewById(R.id.Officio_switch)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.MainFragmentContainer,new DepartamentosAsignados()).commit();
            }
        });
        SelectDepartamento();
        
    }
    
    
    private void SelectDepartamento() {
        JsonArrayRequest Req = new JsonArrayRequest(
                Request.Method.POST,
                Constant.WS_SelectDepartamento,
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
                            ArrayAdapter<String> adapterdepartamentos = new ArrayAdapter<String>
                                    (getActivity(), android.R.layout.select_dialog_item, departamentos);
                            Officios_Departamento.setAdapter(adapterdepartamentos);
                            SelectEstados();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("TAG", "Error Volley: " + error.getCause());
                        new android.app.AlertDialog.Builder(getActivity()).setMessage(error.getCause().toString()).show();
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
        BeeDocsApplication.getInstance().addToRequestQueue(Req);
    }
    private void SelectEstados() {
        JsonArrayRequest Req = new JsonArrayRequest(
                Request.Method.POST,
                Constant.WS_SelectEstado,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("WS-Response", response.toString());
                        try {
                            id_estados = new String[response.length()];
                            estados = new String[response.length()];
                            for (int i = 0; i < response.length(); i++) {
                                id_estados[i] = response.getJSONObject(i).getString("id_estado");
                                estados[i] = response.getJSONObject(i).getString("nom_estado");
                            }
                            ArrayAdapter<String> adapterestados = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, estados);
                            Officios_Estado.setAdapter(adapterestados);
                            
                            
                            if (SharedPreference.GETSharedPreferences(Constant.SPRol, "").equals("1")) {
                                OneUser_ListaOficios(SharedPreference.GETSharedPreferences(Constant.SPId_persona, ""));
                            } else if (SharedPreference.GETSharedPreferences(Constant.SPRol, "").equals("2") || SharedPreference.GETSharedPreferences(Constant.SPRol, "").equals("3")) {
                                OneUser_ListaOficiosAll();
                            }
                            Officios_crear.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (SharedPreference.GETSharedPreferences(Constant.SPRol, "").equals("1")) {
                                        startActivity(new Intent(getActivity(), NuevoOficioTrabajador.class));
                                    } else if (SharedPreference.GETSharedPreferences(Constant.SPRol, "").equals("2") || SharedPreference.GETSharedPreferences(Constant.SPRol, "").equals("3")) {
                                        startActivity(new Intent(getActivity(), NuevoOfficio.class));
                                    }
                                }
                            });
                            
                            
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("TAG", "Error Volley: " + error.getCause());
                        new android.app.AlertDialog.Builder(getActivity()).setMessage(error.getCause().toString()).show();
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
        BeeDocsApplication.getInstance().addToRequestQueue(Req);
    }
    
    public void OneUser_ListaOficios(String id_persona) {
        HashMap<String, String> map = new HashMap<>();
        map.put("id_persona", id_persona);
        JSONObject object = new JSONObject(map);
        Log.e("WS-HashMap", object.toString());
        JsonArrayRequest Req = new JsonArrayRequest(
                Request.Method.POST,
                Constant.WS_SelectOficio,
                object,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("WS-Response", response.toString());
                        modelOficioList = new ArrayList<ModelOficio>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                ModelOficio modelOficio = new ModelOficio();
                                modelOficio.setId_oficio(response.getJSONObject(i).getString("id_oficio"));
                                modelOficio.setCla_oficio(response.getJSONObject(i).getString("cla_oficio"));
                                modelOficio.setNom_oficio(response.getJSONObject(i).getString("nom_oficio"));
                                modelOficio.setAsu_oficio(response.getJSONObject(i).getString("asu_oficio"));
                                modelOficio.setUbi_oficio(response.getJSONObject(i).getString("ubi_oficio"));
                                modelOficio.setNota_oficio(response.getJSONObject(i).getString("notas"));
                                modelOficio.setDepenviada(response.getJSONObject(i).getString("DepEnviada"));
                                modelOficio.setFkid_depepartamento(response.getJSONObject(i).getString("fk_id_departamento"));
                                modelOficio.setFkid_estado(response.getJSONObject(i).getString("fk_id_estado"));
                                modelOficio.setFkid_persona(response.getJSONObject(i).getString("fk_id_persona"));
                                modelOficio.setRutaOficioP(response.getJSONObject(i).getString("RutaOficioP"));
                                modelOficio.setRutaOficioR(response.getJSONObject(i).getString("RutaOficioR"));
                                modelOficioList.add(modelOficio);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Adapter = new CustomListAdapter(getActivity(), modelOficioList);
                        Officios_ListaOficios.setAdapter(Adapter);
                        Officios_ListaOficios.setTextFilterEnabled(true);
                        Officios_Search.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            
                            }
                            
                            @Override
                            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                                RealorFake = 1;
                                int textlength = charSequence.length();
                                lista_temporal = new ArrayList<ModelOficio>();
                                for (ModelOficio Officios : modelOficioList) {
                                    if (textlength <= Officios.getCla_oficio().length()) {
                                        if (Officios.getCla_oficio().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                                            lista_temporal.add(Officios);
                                        }
                                    }
                                }
                                Adapter = new CustomListAdapter(getActivity(), lista_temporal);
                                Officios_ListaOficios.setAdapter(Adapter);
                            }
                            
                            @Override
                            public void afterTextChanged(Editable s) {
                            
                            }
                        });
                        Officios_ListaOficios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if (RealorFake == 0) {
                                    startActivityForResult(new Intent(getActivity(), UpdateOfficio.class), Constant.UpdateOficio_CODE);
                                    UpdateOfficio.modelOficio = modelOficioList.get(position);
                                } else {
                                    startActivityForResult(new Intent(getActivity(), UpdateOfficio.class), Constant.UpdateOficio_CODE);
                                    UpdateOfficio.modelOficio = lista_temporal.get(position);
                                }
                            }
                        });
                        Officios_ListaOficios.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                                if (RealorFake == 0) {
                                    new android.app.AlertDialog.Builder(getActivity())
                                            .setMessage("Desea borrar este elemento?")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    BorrarOficio(modelOficioList.get(position).getId_oficio());
                                                }
                                            })
                                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }).show();
                                } else {
                                    new android.app.AlertDialog.Builder(getActivity())
                                            .setMessage("Desea borrar este elemento?")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    BorrarOficio(lista_temporal.get(position).getId_oficio());
                                                }
                                            })
                                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }).show();
                                }
                                return true;
                            }
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("TAG", "Error Volley: " + error.getCause());
                        new android.app.AlertDialog.Builder(getActivity())
                                .setMessage(error.getCause().toString()).show();
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
        BeeDocsApplication.getInstance().addToRequestQueue(Req);
    }
    public void OneUser_ListaOficiosAll() {
        HashMap<String, String> map = new HashMap<>();
        JSONObject object = new JSONObject(map);
        Log.e("WS-HashMap", object.toString());
        JsonArrayRequest Req = new JsonArrayRequest(
                Request.Method.POST,
                Constant.WS_SelectOficioAll,
                object,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("WS-Response", response.toString());
                        modelOficioList = new ArrayList<ModelOficio>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                ModelOficio modelOficio = new ModelOficio();
                                modelOficio.setId_oficio(response.getJSONObject(i).getString("id_oficio"));
                                modelOficio.setCla_oficio(response.getJSONObject(i).getString("cla_oficio"));
                                modelOficio.setNom_oficio(response.getJSONObject(i).getString("nom_oficio"));
                                modelOficio.setAsu_oficio(response.getJSONObject(i).getString("asu_oficio"));
                                modelOficio.setUbi_oficio(response.getJSONObject(i).getString("ubi_oficio"));
                                modelOficio.setNota_oficio(response.getJSONObject(i).getString("notas"));
                                modelOficio.setDepenviada(response.getJSONObject(i).getString("DepEnviada"));
                                modelOficio.setFkid_depepartamento(response.getJSONObject(i).getString("fk_id_departamento"));
                                modelOficio.setFkid_estado(response.getJSONObject(i).getString("fk_id_estado"));
                                modelOficio.setFkid_persona(response.getJSONObject(i).getString("fk_id_persona"));
                                modelOficio.setRutaOficioP(response.getJSONObject(i).getString("RutaOficioP"));
                                modelOficio.setRutaOficioR(response.getJSONObject(i).getString("RutaOficioR"));
                                modelOficio.setNom_personaVA(response.getJSONObject(i).getString("nom_persona"));
                                modelOficioList.add(modelOficio);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Adapter = new CustomListAdapter(getActivity(), modelOficioList);
                        Officios_ListaOficios.setAdapter(Adapter);
                        Officios_ListaOficios.setTextFilterEnabled(true);
                        Officios_Search.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            
                            }
                            
                            @Override
                            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                                RealorFake = 1;
                                int textlength = charSequence.length();
                                lista_temporal = new ArrayList<ModelOficio>();
                                for (ModelOficio Officios : modelOficioList) {
                                    if (textlength <= Officios.getCla_oficio().length()) {
                                        if (Officios.getCla_oficio().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                                            lista_temporal.add(Officios);
                                        }
                                    }
                                }
                                Adapter = new CustomListAdapter(getActivity(), lista_temporal);
                                Officios_ListaOficios.setAdapter(Adapter);
                            }
                            
                            @Override
                            public void afterTextChanged(Editable s) {
                            
                            }
                        });
                        Officios_ListaOficios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if (RealorFake == 0) {
                                    startActivityForResult(new Intent(getActivity(), UpdateOfficio.class), Constant.UpdateOficio_CODE);
                                    UpdateOfficio.modelOficio = modelOficioList.get(position);
                                } else {
                                    startActivityForResult(new Intent(getActivity(), UpdateOfficio.class), Constant.UpdateOficio_CODE);
                                    UpdateOfficio.modelOficio = lista_temporal.get(position);
                                }
                            }
                        });
                        Officios_ListaOficios.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                                if (modelOficioList.get(position).getFkid_persona().equals(SharedPreference.GETSharedPreferences(Constant.SPId_persona,""))){
                                    if (RealorFake==0){
                                        new android.app.AlertDialog.Builder(getActivity())
                                                .setMessage("Desea borrar este elemento?")
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        BorrarOficio(modelOficioList.get(position).getId_oficio());
                                                    }
                                                })
                                                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                }).show();
                                    }else{
                                        new android.app.AlertDialog.Builder(getActivity())
                                                .setMessage("Desea borrar este elemento?")
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        BorrarOficio(lista_temporal.get(position).getId_oficio());
                                                    }
                                                })
                                                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                }).show();
                                    }
                                }
                                return true;
                            }
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("TAG", "Error Volley: " + error.getCause());
                        new android.app.AlertDialog.Builder(getActivity())
                                .setMessage(error.getCause().toString()).show();
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
        BeeDocsApplication.getInstance().addToRequestQueue(Req);
    }
    
    public void BorrarOficio(String id_Oficio) {
        HashMap<String, String> map = new HashMap<>();
        map.put("Id_Oficio", id_Oficio);
        JSONObject object = new JSONObject(map);
        Log.e("WS-HashMap", object.toString());
        JsonObjectRequest Req = new JsonObjectRequest(
                Request.Method.POST,
                Constant.WS_BorrarOficio,
                object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("WS-Response", response.toString());
                        try {
                            if (response.getString("Response").equals("Success")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage("Oficio borrado con exito")
                                        .setCancelable(false)
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Fragment fragment = (Fragment) Officios.this;
                                        getFragmentManager().findFragmentById(R.id.MainFragmentContainer);
                                        getFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage("Operación no esta disponible, intente mas tarde");
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(error.getCause().toString());
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
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
        BeeDocsApplication.getInstance().addToRequestQueue(Req);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.UpdateOficio_CODE && resultCode == RESULT_OK) {
            if (SharedPreference.GETSharedPreferences(Constant.SPRol,"").equals("1")){
                OneUser_ListaOficios(SharedPreference.GETSharedPreferences(Constant.SPId_persona, ""));
            }else{
                OneUser_ListaOficiosAll();
            }
        }
    }
    
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    
    public class CustomListAdapter extends BaseAdapter {
        ImageLoader imageLoader = BeeDocsApplication.getInstance().getImageLoader();
        private LayoutInflater inflater;
        private Activity activity;
        private List<ModelOficio> lista;
        
        public CustomListAdapter(Activity activity, List<ModelOficio> lista) {
            this.activity = activity;
            this.lista = lista;
        }
        
        @Override
        public int getCount() {
            return lista.size();
        }
        
        @Override
        public Object getItem(int position) {
            return lista.get(position);
        }
        
        @Override
        public long getItemId(int position) {
            return position;
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (inflater == null) inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            
            if (convertView == null) convertView = inflater.inflate(R.layout.itemoficios, null);
            
            ImageView itemOficio_Estado = convertView.findViewById(R.id.itemOficio_Estado);
            TextView itemOficio_Clave = convertView.findViewById(R.id.itemOficio_Clave);
            TextView itemOficio_Nombre = convertView.findViewById(R.id.itemOficio_Nombre);
            TextView itemOficio_DepSol = convertView.findViewById(R.id.itemOficio_DepSol);
            TextView itemOficio_DepEnv = convertView.findViewById(R.id.itemOficio_DepEnv);
            
            ModelOficio m = lista.get(position);
            Bitmap icon = null;
    
            switch (m.getFkid_estado()) {
                case "1":
                    Glide.with(convertView).load(R.drawable.amarillo).into(itemOficio_Estado);
                    break;
                case "2":
                    Glide.with(convertView).load(R.drawable.verde).into(itemOficio_Estado);
                    break;
                case "3":
                    Glide.with(convertView).load(R.drawable.rojo).into(itemOficio_Estado);
                    break;
            }
            
            itemOficio_Clave.setText(m.getCla_oficio());
            if (SharedPreference.GETSharedPreferences(Constant.SPRol,"").equals("2")|| SharedPreference.GETSharedPreferences(Constant.SPRol,"").equals("3")) {
                if (!modelOficioList.get(position).getFkid_persona().equals(SharedPreference.GETSharedPreferences(Constant.SPId_persona,""))){
                    itemOficio_Nombre.setSingleLine(false);
                    itemOficio_Nombre.setText(m.getNom_oficio() + " \nCreador:" + m.getNom_personaVA());
                }else{
                    itemOficio_Nombre.setText(m.getNom_oficio());
                }
            }else {
                itemOficio_Nombre.setText(m.getNom_oficio());
            }
            int lugardedep=0;
            for (int i=0;i<id_departamentos.length;i++){
                if (id_departamentos[i].equals(m.getFkid_depepartamento())){
                    lugardedep=i;
                }
            }
            itemOficio_DepSol.setText("Dep.Sol:"+departamentos[lugardedep]);
            itemOficio_DepEnv.setText("Dep.Env:"+m.getDepenviada());
            
            
            return convertView;
        }
    }
}
