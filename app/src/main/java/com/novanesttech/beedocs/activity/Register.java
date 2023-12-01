package com.novanesttech.beedocs.activity;

import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.BeeDocs.R;
import com.novanesttech.beedocs.utils.StringUtils;
import com.novanesttech.beedocs.BeeDocsApplication;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.novanesttech.beedocs.utils.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    
    private String tag_json_obj = "jobj_req";
    private EditText Register_Nombre,Register_ApellidoP,Register_ApellidoM,Register_Telefono,Register_Correo,Register_Usuario,Register_Contra;
    private Button Register_Registrar;
    private ImageButton Register_Regresar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    
        Register_Nombre = (EditText) findViewById(R.id.Register_Nombre);
        Register_ApellidoP = (EditText) findViewById(R.id.Register_ApellidoP);
        Register_ApellidoM = (EditText) findViewById(R.id.Register_ApellidoM);
        Register_Telefono = (EditText) findViewById(R.id.Register_Telefono);
    
        Register_Correo = (EditText) findViewById(R.id.Register_Correo);
        Register_Usuario = (EditText) findViewById(R.id.Register_Usuario);
        Register_Contra = (EditText) findViewById(R.id.Register_Contra);
        Register_Registrar = (Button) findViewById(R.id.Register_Registrar);
        Register_Regresar = (ImageButton) findViewById(R.id.Register_Regresar);
        Register_Registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Register_Correo.getText().toString().isEmpty()){
                    Register_Correo.setError("Requerido!");
                }else {
                    if (Register_Usuario.getText().toString().equals("") || Register_Contra.getText().toString().equals("")) {
                        new android.app.AlertDialog.Builder(Register.this)
                                .setMessage("Usuario o Contraseña Invalidos")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                    } else {
                        Register(
                                Register_Nombre.getText().toString(),
                                Register_ApellidoP.getText().toString(),
                                Register_ApellidoM.getText().toString(),
                                Register_Telefono.getText().toString(),
                                Register_Correo.getText().toString(),
                                Register_Usuario.getText().toString(),
                                Register_Contra.getText().toString());
                    }
                }
            }
        });
        Register_Regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register.this.finish();
            }
        });
    }
        private void Register(
                String FNombre,
                String FApellidoP,
                String FApellidoM,
                String FTelefono,
                String FCorreo,
                final String FUsuCorreo,
                final String FContraseña) {
        HashMap<String, String> map = new HashMap<>();
            map.put("Nombre", StringUtils.unaccent(FNombre));
            map.put("ApellidoP", StringUtils.unaccent(FApellidoP));
            map.put("ApellidoM", StringUtils.unaccent(FApellidoM));
            map.put("Telefono", FTelefono);
            map.put("Correo", FCorreo);
            map.put("UsuCorreo", StringUtils.unaccent(FUsuCorreo));
            map.put("Contrasena", FContraseña);
        JSONObject object = new JSONObject(map);
        Log.e("HashMap", object.toString());
        JsonObjectRequest Req = new JsonObjectRequest(
                Request.Method.POST,
                Constant.WS_Register,
                object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("WSError",response.toString());
                        try {
                            if (response.getString("Response").equals("Existe")){
                                new android.app.AlertDialog.Builder(Register.this)
                                        .setMessage("El Correo y/o Usuario ingresado ya se encuentran ocupados")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();
                            }else if (response.getString("Response").equals("Success")){
                                new android.app.AlertDialog.Builder(Register.this)
                                        .setMessage("La cuenta fue creada")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent returnintent = new Intent();
                                        setResult(RESULT_OK,returnintent);
                                        Register.this.finish();
                                    }
                                }).show();
                            }else{
                                new android.app.AlertDialog.Builder(Register.this)
                                        .setMessage("Su cuenta no ha podido ser creada, Intente mas tarde.")
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
                        //idlc = "";
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("TAG", "Error Volley: " + error.getCause());
                        new AlertDialog.Builder(Register.this).setMessage(error.getCause().toString()).show();
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
        BeeDocsApplication.Companion.getInstance().getVolleyConnection().getImageLoader().addToRequestQueue(Req, tag_json_obj);
    }
    
}
