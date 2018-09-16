package com.BeeDocs;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.BeeDocs.R;

import static com.BeeDocs.Constant.WS_SelectAllDepartamentos;
import static com.BeeDocs.Constant.WS_SelectDepsAsignado;

public class DepartamentosAsignadosCircular extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ListView DepAsignada_Lista;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private DepartamentosAsignados.OnFragmentInteractionListener mListener;
    public DepartamentosAsignadosCircular() {}
    public static DepartamentosAsignadosCircular newInstance(String param1, String param2) {
        DepartamentosAsignadosCircular fragment = new DepartamentosAsignadosCircular();
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
        return inflater.inflate(R.layout.fragment_departamentos_asignados, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        DepAsignada_Lista = (ListView) view.findViewById(R.id.DepAsignada_Lista);
        if (SharedPreference.GETSharedPreferences(Constant.SPRol,"").equals("1")) {
            Departamento(SharedPreference.GETSharedPreferences(Constant.SPId_persona,""));
        }else{
            DepartamentoAll();
        }
    }
    
    public void Departamento(String id_personas) {
        ProgressDialog.Builder builder = new ProgressDialog.Builder(getActivity());
        builder.setMessage("Buscando Departamentos...");
        builder.setCancelable(false);
        final android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
        
        HashMap<String, String> map = new HashMap<>();
        map.put("id_persona", id_personas);
        JSONObject object = new JSONObject(map);
        Log.e("HashMap", object.toString());
        JsonArrayRequest Req = new JsonArrayRequest(
                Request.Method.POST,
                WS_SelectDepsAsignado,
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
                                departamentoAsignado.setNcla_departamento(response.getJSONObject(i).getString("ncla_departamento").replace("OFICIO","CIRCULAR"));
    
                                departamentoAsignadosList.add(departamentoAsignado);
                            }
                            CustomAdapter customAdapter = new CustomAdapter(getActivity(),departamentoAsignadosList);
                            DepAsignada_Lista.setAdapter(customAdapter);
                            DepAsignada_Lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    SharedPreference.SETSharedPreferences(Constant.SPID_Oficio, departamentoAsignadosList.get(position).getId_departamento());
                                    SharedPreference.SETSharedPreferences(Constant.SPNomenclaturaOficio, departamentoAsignadosList.get(position).getNcla_departamento().replace("CIRCULAR","OFICIO"));
                                    SharedPreference.SETSharedPreferences(Constant.SPID_Memoran, departamentoAsignadosList.get(position).getId_departamento());
                                    SharedPreference.SETSharedPreferences(Constant.SPNomenclaturaMemoran, departamentoAsignadosList.get(position).getNcla_departamento().replace("CIRCULAR","MEMORAN"));
                                    SharedPreference.SETSharedPreferences(Constant.SPID_Circular, departamentoAsignadosList.get(position).getId_departamento());
                                    SharedPreference.SETSharedPreferences(Constant.SPNomenclaturaCircular, departamentoAsignadosList.get(position).getNcla_departamento());
                                    Circular nextFrag= new Circular();
                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.MainFragmentContainer, nextFrag)
                                            .addToBackStack(null).commit();                                }
                            });
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
        VolleySingleton.getInstance().addToRequestQueue(Req);
    }
    
    public void DepartamentoAll() {
        ProgressDialog.Builder builder = new ProgressDialog.Builder(getActivity());
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
                                departamentoAsignado.setNcla_departamento(response.getJSONObject(i).getString("ncla_departamento").replace("OFICIO", "CIRCULAR"));
                                departamentoAsignadosList.add(departamentoAsignado);
                            }
                            CustomAdapter customAdapter = new CustomAdapter(getActivity(),departamentoAsignadosList);
                            DepAsignada_Lista.setAdapter(customAdapter);
                            DepAsignada_Lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    SharedPreference.SETSharedPreferences(Constant.SPID_Oficio, departamentoAsignadosList.get(position).getId_departamento());
                                    SharedPreference.SETSharedPreferences(Constant.SPNomenclaturaOficio, departamentoAsignadosList.get(position).getNcla_departamento().replace("CIRCULAR","OFICIO"));
                                    SharedPreference.SETSharedPreferences(Constant.SPID_Memoran, departamentoAsignadosList.get(position).getId_departamento());
                                    SharedPreference.SETSharedPreferences(Constant.SPNomenclaturaMemoran, departamentoAsignadosList.get(position).getNcla_departamento().replace("CIRCULAR","MEMORAN"));
                                    SharedPreference.SETSharedPreferences(Constant.SPID_Circular, departamentoAsignadosList.get(position).getId_departamento());
                                    SharedPreference.SETSharedPreferences(Constant.SPNomenclaturaCircular, departamentoAsignadosList.get(position).getNcla_departamento());
                                    Circular nextFrag= new Circular();
                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.MainFragmentContainer, nextFrag)
                                            .addToBackStack(null).commit();
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
                        alertDialog.dismiss();
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
        VolleySingleton.getInstance().addToRequestQueue(Req);
    }
    
    
    
    
    public class CustomAdapter extends BaseAdapter {
        ImageLoader imageLoader = VolleySingleton.getInstance().getImageLoader();
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
    
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DepartamentosAsignados.OnFragmentInteractionListener) {
            mListener = (DepartamentosAsignados.OnFragmentInteractionListener) context;
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
}


