package com.novanesttech.beedocs.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.novanesttech.beedocs.R;
import com.novanesttech.beedocs.BeeDocsApplication;
import com.novanesttech.beedocs.dialog.ProgressDFont;
import com.novanesttech.beedocs.model.Info_Personas;
import com.novanesttech.beedocs.utils.Constant;
import com.novanesttech.beedocs.dialog.AlertDFont;
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

public class UsersActivation extends AppCompatActivity {
    ListView Users_Activation_Lista;
    List<Info_Personas> Info_Personas;
    CustomListAdapter adapter;
    TextView Users_Activation_Title, Users_Activation_Notice;
    ImageButton User_Activation_back, User_Activation_adduser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_activation);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        User_Activation_back = (ImageButton) findViewById(R.id.User_Activation_back);
        User_Activation_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        User_Activation_adduser = (ImageButton) findViewById(R.id.User_Activation_adduser);
        User_Activation_adduser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(UsersActivation.this, Register.class), Constant.AddUser_CODE);
            }
        });
        Users_Activation_Title = (TextView) findViewById(R.id.Users_Activation_Title);
        Users_Activation_Notice = (TextView) findViewById(R.id.Users_Activation_Notice);
        Users_Activation_Notice.setText("Cambios tomaran efecto la proxima ves que el usuario entre a su cuenta");
        Users_Activation_Lista = (ListView) findViewById(R.id.Users_Activation_Lista);
        List_Activation_Users();
    }

    public void List_Activation_Users() {
        HashMap<String, String> map = new HashMap<>();
        JsonArrayRequest Req = new JsonArrayRequest(
                Request.Method.POST,
                Constant.WS_SelectInfoPersona,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("WS-Response", response.toString());
                        Info_Personas = new ArrayList<Info_Personas>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                Info_Personas info_personas = new Info_Personas();
                                info_personas.setId_persona(response.getJSONObject(i).getString("id_usuario"));
                                info_personas.setNom_persona(response.getJSONObject(i).getString("nom_persona"));
                                info_personas.setUsu_usuario(response.getJSONObject(i).getString("usu_usuario"));
                                info_personas.setFk_id_rol(response.getJSONObject(i).getString("fk_id_rol"));
                                info_personas.setActivo(response.getJSONObject(i).getString("activo"));
                                Info_Personas.add(info_personas);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        adapter = new CustomListAdapter(UsersActivation.this, Info_Personas);
                        Users_Activation_Lista.setAdapter(adapter);
                        Users_Activation_Lista.setTextFilterEnabled(true);
                        Users_Activation_Lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if (!Info_Personas.get(position).getFk_id_rol().equals("3")) {
                                    AssignDepartment.Persona = Info_Personas.get(position);
                                    startActivity(new Intent(UsersActivation.this, AssignDepartment.class));
                                }
                            }
                        });
                        Users_Activation_Lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                AccountInfo(Info_Personas.get(position).getId_persona());
                                return true;
                            }
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("TAG", "Error Volley: " + error.getCause());
                        new AlertDFont.Builder(UsersActivation.this).setMessage(error.getCause().toString()).show();
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

    public void AccountInfo(String Id_persona) {
        final ProgressDFont builder = new ProgressDFont(UsersActivation.this);
        builder.setMessage("Buscando Cuenta...");
        builder.setCancelable(false);
        builder.show();

        HashMap<String, String> map = new HashMap<>();
        map.put("id_persona", Id_persona);
        JSONObject object = new JSONObject(map);
        Log.e("HashMap", object.toString());
        JsonObjectRequest Req = new JsonObjectRequest(
                Request.Method.POST,
                Constant.WS_GETAccount,
                object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("WS-Response", response.toString());
                        builder.dismiss();
                        try {
                            Account account = new Account();
                            account.setIdpersona(response.getString("id_persona"));
                            account.setNompersona(response.getString("nom_persona"));
                            account.setApppersona(response.getString("app_persona"));
                            account.setApmpersona(response.getString("apm_persona"));
                            account.setTelpersona(response.getString("tel_persona"));
                            account.setCorpersona(response.getString("cor_persona"));
                            account.setUsupersona(response.getString("usu_usuario"));
                            account.setConpersona(response.getString("con_usuario"));
                            UpdateUser.account = account;
                            startActivityForResult(new Intent(UsersActivation.this, UpdateUser.class), Constant.UserUpdated_CODE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        builder.dismiss();
                        Log.d("TAG", "Error Volley: " + error.getCause());
                        new AlertDFont.Builder(UsersActivation.this).setMessage(error.getCause().toString()).show();
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

    public class CustomListAdapter extends BaseAdapter {
        ImageLoader imageLoader = BeeDocsApplication.Companion.getInstance().getVolleyConnection().getImageLoader();
        private LayoutInflater inflater;
        private Activity activity;
        private List<Info_Personas> lista;

        public CustomListAdapter(Activity activity, List<Info_Personas> lista) {
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
            if (inflater == null)
                inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) convertView = inflater.inflate(R.layout.itemactivacion, null);

            ImageView UsersAct_Activacion = convertView.findViewById(R.id.UsersAct_Activacion);
            TextView UsersAct_Nombre = convertView.findViewById(R.id.UsersAct_Nombre);
            TextView UsersAct_Correo = convertView.findViewById(R.id.UsersAct_Correo);
            ImageView UsersAct_Rol = convertView.findViewById(R.id.UsersAct_Rol);

            final Info_Personas info_personas = lista.get(position);
            Bitmap icon = null;
            switch (info_personas.getActivo()) {
                case "0":
                    Glide.with(convertView).load(R.drawable.inactive_waiting).into(UsersAct_Activacion);
                    break;
                case "1":
                    Glide.with(convertView).load(R.drawable.active).into(UsersAct_Activacion);
                    break;
            }
            UsersAct_Activacion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (info_personas.getActivo().equals("0")) {
                        Update_Info_Personas(info_personas.getId_persona(), Constant.Active, String.valueOf(Constant.YES));
                    } else {
                        Update_Info_Personas(info_personas.getId_persona(), Constant.Active, String.valueOf(Constant.NO));
                    }
                }
            });
            switch (info_personas.getFk_id_rol()) {
                case "1":
                    Glide.with(convertView).load(R.drawable.trabajadores).into(UsersAct_Rol);
                    break;
                case "2":
                    Glide.with(convertView).load(R.drawable.supervisor).into(UsersAct_Rol);
                    break;
                case "3":
                    Glide.with(convertView).load(R.drawable.administrator).into(UsersAct_Rol);
                    break;
            }
            UsersAct_Rol.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (info_personas.getFk_id_rol().equals("1")) {
                        Update_Info_Personas(info_personas.getId_persona(), Constant.Rol, String.valueOf(Constant.Supervisor));
                    } else if (info_personas.getFk_id_rol().equals("2")) {
                        Update_Info_Personas(info_personas.getId_persona(), Constant.Rol, String.valueOf(Constant.Worker));
                    }
                }
            });
            UsersAct_Rol.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (info_personas.getFk_id_rol() != "3") {
                        new AlertDFont.Builder(UsersActivation.this)
                                .setTitle("Importante")
                                .setMessage("Usuario sera administrador: \n ¿Desea Continuar?")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Update_Info_Personas(info_personas.getId_persona(), Constant.Rol, String.valueOf(Constant.Administrator));
                                        dialog.dismiss();
                                    }
                                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).setCancelable(false).show();
                        return true;
                    }
                    return false;
                }
            });
            UsersAct_Nombre.setText(info_personas.getNom_persona());
            UsersAct_Correo.setText(info_personas.getUsu_usuario());


            return convertView;
        }
    }

    private void Update_Info_Personas(String Id_persona, String Campo, String valor) {
        ProgressDialog.Builder builder = new ProgressDialog.Builder(UsersActivation.this);
        builder.setMessage("Actualizando Info Persona...");
        builder.setCancelable(false);
        final android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();

        HashMap<String, String> map = new HashMap<>();
        map.put("Id_Usuario", Id_persona);
        map.put("Campo_Usuario", Campo);
        map.put("valor_Usuario", valor);
        JSONObject object = new JSONObject(map);
        Log.e("HashMap", object.toString());
        JsonObjectRequest Req = new JsonObjectRequest(
                Request.Method.POST,
                Constant.WS_UpdateInfoPersona,
                object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            alertDialog.dismiss();
                            Log.e("WS-Response", response.toString());
                            if (!response.getString("Response").equals("Success")) {
                                new AlertDFont.Builder(UsersActivation.this).setMessage("Intente nuevamente o mas tarde").show();
                            } else {
                                List_Activation_Users();
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
                        new AlertDFont.Builder(UsersActivation.this).setMessage(error.getCause().toString()).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.AddUser_CODE && resultCode == RESULT_OK || requestCode == Constant.UserUpdated_CODE && resultCode == RESULT_OK) {
            List_Activation_Users();
        }
    }

    public class Account {
        String idpersona;
        String nompersona;
        String apppersona;
        String apmpersona;
        String telpersona;
        String corpersona;
        String usupersona;
        String conpersona;

        public Account() {
        }

        public Account(String idpersona, String nompersona, String apppersona, String apmpersona, String telpersona, String corpersona, String usupersona, String conpersona) {
            this.idpersona = idpersona;
            this.nompersona = nompersona;
            this.apppersona = apppersona;
            this.apmpersona = apmpersona;
            this.telpersona = telpersona;
            this.corpersona = corpersona;
            this.usupersona = usupersona;
            this.conpersona = conpersona;
        }

        public String getIdpersona() {
            return idpersona;
        }

        public void setIdpersona(String idpersona) {
            this.idpersona = idpersona;
        }

        public String getNompersona() {
            return nompersona;
        }

        public void setNompersona(String nompersona) {
            this.nompersona = nompersona;
        }

        public String getApppersona() {
            return apppersona;
        }

        public void setApppersona(String apppersona) {
            this.apppersona = apppersona;
        }

        public String getApmpersona() {
            return apmpersona;
        }

        public void setApmpersona(String apmpersona) {
            this.apmpersona = apmpersona;
        }

        public String getTelpersona() {
            return telpersona;
        }

        public void setTelpersona(String telpersona) {
            this.telpersona = telpersona;
        }

        public String getCorpersona() {
            return corpersona;
        }

        public void setCorpersona(String corpersona) {
            this.corpersona = corpersona;
        }

        public String getUsupersona() {
            return usupersona;
        }

        public void setUsupersona(String usupersona) {
            this.usupersona = usupersona;
        }

        public String getConpersona() {
            return conpersona;
        }

        public void setConpersona(String conpersona) {
            this.conpersona = conpersona;
        }
    }

}
