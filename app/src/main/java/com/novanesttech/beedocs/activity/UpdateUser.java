package com.novanesttech.beedocs.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.BeeDocs.R;
import com.novanesttech.beedocs.BeeDocsApplication;
import com.novanesttech.beedocs.dialog.ProgressDFont;
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

public class UpdateUser extends AppCompatActivity {
    static UsersActivation.Account account;
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
        Register_Nombre.setText(account.getNompersona());
        Register_ApellidoP = (EditText) findViewById(R.id.Register_ApellidoP);
        Register_ApellidoP.setText(account.getApppersona());
        Register_ApellidoM = (EditText) findViewById(R.id.Register_ApellidoM);
        Register_ApellidoM.setText(account.getApmpersona());
        Register_Telefono = (EditText) findViewById(R.id.Register_Telefono);
        Register_Telefono.setText(account.getTelpersona());
        Register_Correo = (EditText) findViewById(R.id.Register_Correo);
        Register_Correo.setText(account.getCorpersona());
        Register_Usuario = (EditText) findViewById(R.id.Register_Usuario);
        Register_Usuario.setText(account.getUsupersona());
        Register_Contra = (EditText) findViewById(R.id.Register_Contra);
        Register_Contra.setText(account.getConpersona());
        Register_Registrar = (Button) findViewById(R.id.Register_Registrar);
        Register_Registrar.setText("Actualizar");
        Register_Regresar = (ImageButton) findViewById(R.id.Register_Regresar);
        
        Register_Registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Register_Usuario.getText().toString().equals("")||Register_Contra.getText().toString().equals("")){
                    new android.app.AlertDialog.Builder(UpdateUser.this)
                            .setMessage("Usuario o Contraseña Invalidos")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }else {
                    ActualizarRequest(account.getIdpersona(),Register_Nombre.getText().toString(),Register_ApellidoM.getText().toString(),Register_ApellidoP.getText().toString(),Register_Telefono.getText().toString(),Register_Correo.getText().toString(),Register_Usuario.getText().toString(),Register_Contra.getText().toString());
                }
            }
        });
        Register_Regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateUser.this.finish();
            }
        });
    }
    
    
    private void ActualizarRequest(String id_persona,String Nombre,String ApellidoM, String ApellidoP,String Telefono, String correo,String usuario,String pass) {
        final ProgressDFont builder = new ProgressDFont(UpdateUser.this);
        builder.setMessage("Actualizando Info...");
        builder.setCancelable(false);
        builder.show();
        HashMap<String, String> map = new HashMap<>();
        map.put("Id_persona", id_persona);
        map.put("Nombre", Nombre);
        map.put("ApellidoP", ApellidoP);
        map.put("ApellidoM", ApellidoM);
        map.put("Telefono", Telefono);
        map.put("Correo", correo);
        map.put("UsuCorreo", usuario);
        map.put("Contrasena", pass);
        JSONObject object = new JSONObject(map);
        Log.e("HashMap", object.toString());
        JsonObjectRequest Req = new JsonObjectRequest(
                Request.Method.POST,
                Constant.WS_UpdateAccount,
                object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("WS-Response",response.toString());
                        try {
                            builder.dismiss();
                            if (response.getString("Response").equals("Success")){
                                new android.app.AlertDialog.Builder(UpdateUser.this).setMessage("Actualizacion exitosa")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                Intent returnintent = new Intent();
                                                setResult(RESULT_OK,returnintent);
                                                UpdateUser.this.finish();
                                            }
                                        }).show();
                            }else{
                                new android.app.AlertDialog.Builder(UpdateUser.this).setMessage("Actualizacion Fallid, intente mas tarde")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                Intent returnintent = new Intent();
                                                setResult(RESULT_OK,returnintent);
                                                UpdateUser.this.finish();
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
                        builder.dismiss();
                        Log.d("TAG", "Error Volley: " + error.getCause());
                        if (error.getCause()!=null){
                            new AlertDialog.Builder(UpdateUser.this).setMessage(error.getCause().toString()).show();
                        }
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
        BeeDocsApplication.Companion.getInstance().getVolleyConnection().addToRequestQueue(Req, tag_json_obj);
    }
    
    
    
    
}

