package com.BeeDocs.fragment;

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

import com.BeeDocs.utils.Constant;
import com.BeeDocs.model.DepartamentoAsignado;
import com.BeeDocs.activity.NuevoCircular;
import com.BeeDocs.activity.NuevoCircularTrabajador;
import com.BeeDocs.R;
import com.BeeDocs.activity.UpdateCircular;
import com.BeeDocs.BeeDocsApplication;
import com.BeeDocs.db.BaseOficios;
import com.BeeDocs.dialog.AlertDFont;
import com.BeeDocs.model.ModelCiruclar;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.BeeDocs.utils.Constant.SPRol;
import static com.BeeDocs.utils.Constant.WS_BorrarCircular;
import static com.BeeDocs.utils.Constant.WS_SelectCircular;
import static com.BeeDocs.utils.Constant.WS_SelectCircularAll;
import static com.BeeDocs.utils.Constant.WS_SelectDepartamento;
import static com.BeeDocs.utils.Constant.WS_SelectEstado;
import static com.BeeDocs.utils.SharedPreference.GETSharedPreferences;

public class Circular extends Fragment {
    
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static String[] departamentos;
    public static String[] id_departamentos;
    public static String[] clave_departamento;
    static String[] ncla_departamento;
    public static String[] estados;
    public static String[] id_estados;
    static List<ModelCiruclar> modelCircularList;
    private List<ModelCiruclar> lista_temporal;
    static BaseOficios baseOficios;
    private static CustomListAdapter Adapter;
    ImageView Officios_crear;
    ListView Officios_ListaOficios;
    Spinner Officios_Departamento, Officios_Estado;
    private String mParam1;
    private String mParam2;
    private EditText Officios_Search;
    private int RealorFake = 0;
    static DepartamentoAsignado departamentoAsignado;
    private Memoran.OnFragmentInteractionListener mListener;
    
    public Circular() {
    }
    
    public static Circular newInstance(String param1, String param2) {
        Circular fragment = new Circular();
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
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.MainFragmentContainer,new DepartamentosAsignadosCircular()).commit();
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
                        new AlertDFont.Builder(getActivity()).setMessage(error.getCause().toString()).show();
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
                WS_SelectEstado,
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
    
    
                            if (GETSharedPreferences(SPRol, "").equals("1")) {
                                OneUser_ListaOficios(GETSharedPreferences(Constant.SPId_persona, ""));
                            } else if (GETSharedPreferences(SPRol, "").equals("2") || GETSharedPreferences(SPRol, "").equals("3")) {
                                OneUser_ListaOficiosAll();
                            }
                            Officios_crear.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (GETSharedPreferences(SPRol, "").equals("1")) {
                                        startActivity(new Intent(getActivity(), NuevoCircularTrabajador.class));
                                    } else if (GETSharedPreferences(SPRol, "").equals("2") || GETSharedPreferences(SPRol, "").equals("3")) {
                                        startActivity(new Intent(getActivity(), NuevoCircular.class));
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
                        new AlertDFont.Builder(getActivity()).setMessage(error.getCause().toString()).show();
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
                WS_SelectCircular,
                object,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("WS-Response", response.toString());
                        modelCircularList = new ArrayList<ModelCiruclar>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                ModelCiruclar modelCiruclar = new ModelCiruclar();
                                modelCiruclar.setId_circular(response.getJSONObject(i).getString("id_circular"));
                                modelCiruclar.setCla_circular(response.getJSONObject(i).getString("cla_circular"));
                                modelCiruclar.setNom_circular(response.getJSONObject(i).getString("nom_circular"));
                                modelCiruclar.setAsu_circular(response.getJSONObject(i).getString("asu_circular"));
                                modelCiruclar.setUbi_circular(response.getJSONObject(i).getString("ubi_circular"));
                                modelCiruclar.setNota_circular(response.getJSONObject(i).getString("notas"));
                                modelCiruclar.setDepenviada(response.getJSONObject(i).getString("DepEnviada"));
                                modelCiruclar.setFkid_depepartamento(response.getJSONObject(i).getString("fk_id_departamento"));
                                modelCiruclar.setFkid_estado(response.getJSONObject(i).getString("fk_id_estado"));
                                modelCiruclar.setFkid_persona(response.getJSONObject(i).getString("fk_id_persona"));
                                modelCiruclar.setRutaCircularP(response.getJSONObject(i).getString("RutaCircularP"));
                                modelCiruclar.setRutaCircularR(response.getJSONObject(i).getString("RutaCircularR"));
                                modelCircularList.add(modelCiruclar);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Adapter = new CustomListAdapter(getActivity(), modelCircularList);
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
                                lista_temporal = new ArrayList<ModelCiruclar>();
                                for (ModelCiruclar modelCiruclar : modelCircularList) {
                                    if (textlength <= modelCiruclar.getCla_circular().length()) {
                                        if (modelCiruclar.getCla_circular().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                                            lista_temporal.add(modelCiruclar);
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
                                    startActivityForResult(new Intent(getActivity(), UpdateCircular.class), Constant.UpdateOficio_CODE);
                                    // TODO: Modificar aqui }}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}
                                    UpdateCircular.modelCiruclar = modelCircularList.get(position);
                                } else {
                                    startActivityForResult(new Intent(getActivity(), UpdateCircular.class), Constant.UpdateOficio_CODE);
                                    // TODO: Modificar aqui }}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}
                                    UpdateCircular.modelCiruclar = lista_temporal.get(position);
                                }
                            }
                        });
                        Officios_ListaOficios.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                                if (RealorFake == 0) {
                                    new AlertDFont.Builder(getActivity())
                                            .setMessage("Desea borrar este elemento?")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    BorrarCircular(modelCircularList.get(position).getId_circular());
                                                }
                                            })
                                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }).show();
                                } else {
                                    new AlertDFont.Builder(getActivity())
                                            .setMessage("Desea borrar este elemento?")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    BorrarCircular(lista_temporal.get(position).getId_circular());
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
                        new AlertDFont.Builder(getActivity())
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
                WS_SelectCircularAll,
                object,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("WS-Response", response.toString());
                        modelCircularList = new ArrayList<ModelCiruclar>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                ModelCiruclar modelCiruclar = new ModelCiruclar();
                                modelCiruclar.setId_circular(response.getJSONObject(i).getString("id_circular"));
                                modelCiruclar.setCla_circular(response.getJSONObject(i).getString("cla_circular"));
                                modelCiruclar.setNom_circular(response.getJSONObject(i).getString("nom_circular"));
                                modelCiruclar.setAsu_circular(response.getJSONObject(i).getString("asu_circular"));
                                modelCiruclar.setUbi_circular(response.getJSONObject(i).getString("ubi_circular"));
                                modelCiruclar.setNota_circular(response.getJSONObject(i).getString("notas"));
                                modelCiruclar.setDepenviada(response.getJSONObject(i).getString("DepEnviada"));
                                modelCiruclar.setFkid_depepartamento(response.getJSONObject(i).getString("fk_id_departamento"));
                                modelCiruclar.setFkid_estado(response.getJSONObject(i).getString("fk_id_estado"));
                                modelCiruclar.setFkid_persona(response.getJSONObject(i).getString("fk_id_persona"));
                                modelCiruclar.setRutaCircularP(response.getJSONObject(i).getString("RutaCircularP"));
                                modelCiruclar.setRutaCircularR(response.getJSONObject(i).getString("RutaCircularR"));
                                modelCiruclar.setNom_personaVA(response.getJSONObject(i).getString("nom_persona"));
                                modelCircularList.add(modelCiruclar);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Adapter = new CustomListAdapter(getActivity(), modelCircularList);
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
                                lista_temporal = new ArrayList<ModelCiruclar>();
                                for (ModelCiruclar modelCiruclar : modelCircularList) {
                                    if (textlength <= modelCiruclar.getCla_circular().length()) {
                                        if (modelCiruclar.getCla_circular().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                                            lista_temporal.add(modelCiruclar);
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
                                    startActivityForResult(new Intent(getActivity(), UpdateCircular.class), Constant.UpdateOficio_CODE);
                                    UpdateCircular.modelCiruclar = modelCircularList.get(position);
                                } else {
                                    startActivityForResult(new Intent(getActivity(), UpdateCircular.class), Constant.UpdateOficio_CODE);
                                    UpdateCircular.modelCiruclar = lista_temporal.get(position);
                                }
                            }
                        });
                        Officios_ListaOficios.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                                if (modelCircularList.get(position).getFkid_persona().equals(GETSharedPreferences(Constant.SPId_persona,""))){
                                    if (RealorFake==0){
                                        new AlertDFont.Builder(getActivity())
                                                .setMessage("Desea borrar este elemento?")
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        BorrarCircular(modelCircularList.get(position).getId_circular());
                                                    }
                                                })
                                                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                }).show();
                                    }else{
                                        new AlertDFont.Builder(getActivity())
                                                .setMessage("Desea borrar este elemento?")
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        BorrarCircular(lista_temporal.get(position).getId_circular());
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
                        new AlertDFont.Builder(getActivity())
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
    
    public void BorrarCircular(String id_circular) {
        HashMap<String, String> map = new HashMap<>();
        map.put("Id_circular", id_circular);
        JSONObject object = new JSONObject(map);
        Log.e("WS-HashMap", object.toString());
        JsonObjectRequest Req = new JsonObjectRequest(
                Request.Method.POST,
                WS_BorrarCircular,
                object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("WS-Response", response.toString());
                        try {
                            if (response.getString("Response").equals("Success")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setMessage("Circular borrado con exito").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Fragment fragment = (Fragment) Circular.this;
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
            if (GETSharedPreferences(SPRol,"").equals("1")){
                OneUser_ListaOficios(GETSharedPreferences(Constant.SPId_persona, ""));
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
        if (context instanceof Officios.OnFragmentInteractionListener) {
            mListener = (Memoran.OnFragmentInteractionListener) context;
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
        private List<ModelCiruclar> lista;
        
        public CustomListAdapter(Activity activity, List<ModelCiruclar> lista) {
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
    
            ModelCiruclar m = lista.get(position);
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
            
            itemOficio_Clave.setText(m.getCla_circular());
            if (GETSharedPreferences(SPRol,"").equals("2")||GETSharedPreferences(SPRol,"").equals("3")) {
                if (!modelCircularList.get(position).getFkid_persona().equals(GETSharedPreferences(Constant.SPId_persona,""))){
                    itemOficio_Nombre.setSingleLine(false);
                    itemOficio_Nombre.setText(m.getNom_circular() + " \nCreador:" + m.getNom_personaVA());
                }else{
                    itemOficio_Nombre.setText(m.getNom_circular());
                }
            }else {
                itemOficio_Nombre.setText(m.getNom_circular());
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