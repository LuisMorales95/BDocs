package com.novanesttech.beedocs.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.novanesttech.beedocs.R;
import com.novanesttech.beedocs.BeeDocsApplication;
import com.novanesttech.beedocs.dialog.ProgressDFont;
import com.novanesttech.beedocs.utils.Constant;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.novanesttech.beedocs.utils.SharedPreference.GETSharedPreferences;
import static com.novanesttech.beedocs.utils.SharedPreference.SETSharedPreferences;

public class SignIn extends AppCompatActivity {
    private ConstraintLayout background;
    private String tag_json_obj = "jobj_req";
    private static EditText SignIn_User,SignIn_Contra;
    private ImageView SignIn_LogIn,PersonalizedImageLogo,cardback;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        background = (ConstraintLayout) findViewById(R.id.background);
        background.setBackgroundDrawable(new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.background)));
        PersonalizedImageLogo = (ImageView) findViewById(R.id.PersonalizedImageLogo);
        PersonalizedImageLogo.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.logotipoabeja));
        cardback = (ImageView) findViewById(R.id.cardback);
        cardback.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.logincuadro));
        SignIn_User = (EditText) findViewById(R.id.SignIn_User);
        SignIn_Contra = (EditText) findViewById(R.id.SignIn_Contra);
        SignIn_LogIn = (ImageView) findViewById(R.id.SignIn_LogIn);
        if (!GETSharedPreferences(Constant.SPCorreo_persona,"").equals("")&&!GETSharedPreferences(Constant.SPContraseña_persona,"").equals("")){
            SignIn_User.setText(GETSharedPreferences(Constant.SPUsuario_persona,""));
            SignIn_Contra.setText(GETSharedPreferences(Constant.SPContraseña_persona,""));
            SignInRequest(SignIn_User.getText().toString(),SignIn_Contra.getText().toString());
        }else{
            SignIn_LogIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SignInRequest(SignIn_User.getText().toString(),SignIn_Contra.getText().toString());
                }
            });
        }
       
    }
    private void SignInRequest(final String FUsuCorreo, final String FContraseña) {
        final ProgressDFont builder = new ProgressDFont(SignIn.this);
        builder.setMessage("Iniciando Sesión...");
        builder.setCancelable(false);
        builder.show();
        HashMap<String, String> map = new HashMap<>();
        map.put("UsuCorreo", FUsuCorreo);
        map.put("Contrasena", FContraseña);
        JSONObject object = new JSONObject(map);
        Log.e("HashMap", object.toString());
        JsonObjectRequest Req = new JsonObjectRequest(
                Request.Method.POST,
                Constant.WS_SignIn,
                object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("WS-Response",response.toString());
                        try {
                            builder.dismiss();
                            if (response.getString("id_persona").equals("")){
                                new android.app.AlertDialog.Builder(SignIn.this).setMessage("Usuario/Contraseña Incorrecto")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        SignIn_LogIn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                SignInRequest(SignIn_User.getText().toString(),SignIn_Contra.getText().toString());
                                            }
                                        });
                                    }
                                }).show();
                            }else if (response.getString("activo").equals("0")){
                                new android.app.AlertDialog.Builder(SignIn.this).setMessage("Su cuenta no esta activa aun")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        SignIn_LogIn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                SignInRequest(SignIn_User.getText().toString(),SignIn_Contra.getText().toString());
                                            }
                                        });
                                    }
                                }).show();
                            }else if (response.getString("activo").equals("1")){
                                SETSharedPreferences(Constant.SPId_persona,response.getString("id_persona"));
                                SETSharedPreferences(Constant.SPNombre_persona,response.getString("nom_persona"));
                                SETSharedPreferences(Constant.SPApellidoP_persona,response.getString("app_persona"));
                                SETSharedPreferences(Constant.SPApellidoM_persona,response.getString("apm_persona"));
                                SETSharedPreferences(Constant.SPTelefono_persona,response.getString("tel_persona"));
                                SETSharedPreferences(Constant.SPCorreo_persona,response.getString("cor_persona"));
                                SETSharedPreferences(Constant.SPUsuario_persona,FUsuCorreo);
                                SETSharedPreferences(Constant.SPContraseña_persona,FContraseña);
                                SETSharedPreferences(Constant.SPToken,response.getString("token"));
                                SETSharedPreferences(Constant.SPRol,response.getString("rol"));
                                SignIn.this.finish();
                                startActivity(new Intent(SignIn.this, MainActivity.class));
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
                        new AlertDialog.Builder(SignIn.this).setMessage(error.getCause().toString()).show();
                        SignIn_LogIn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SignInRequest(SignIn_User.getText().toString(),SignIn_Contra.getText().toString());
                            }
                        });
    
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
        Objects.requireNonNull(BeeDocsApplication.Companion.getInstance()).getVolleyConnection().addToRequestQueue(Req, tag_json_obj);
    }
    
    
}
