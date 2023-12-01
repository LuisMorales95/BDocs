package com.novanesttech.beedocs.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.novanesttech.beedocs.fragment.Officios;
import com.BeeDocs.R;
import com.novanesttech.beedocs.utils.SharedPreference;
import com.novanesttech.beedocs.utils.StringUtils;
import com.novanesttech.beedocs.BeeDocsApplication;
import com.novanesttech.beedocs.db.BaseOficios;
import com.novanesttech.beedocs.dialog.AlertDFont;
import com.novanesttech.beedocs.utils.Constant;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NuevoOfficio extends AppCompatActivity {
    
    AutoCompleteTextView NOfficio_Nombre, NOfficio_Dep_Envi, NOfficio_Asunto, NOfficio_Ubicacion;
    Button
            NOfficio_Pendiente, NOfficio_Entregada;
    EditText NOfficio_Notas;
    Spinner NOfficio_Dep_Sol, NOfficio_Estado;
    TextView NOfficio_Nomenclatura, NOfficio_Guardar,
            NOfficio_RutaPendiente, NOfficio_RutaEntregada;
    BaseOficios baseOficios;
    private String[] departamentos = Officios.departamentos, id_departamentos = Officios.id_departamentos, clave_departamento = Officios.clave_departamento;
    private String[] estados = Officios.estados, id_estados = Officios.id_estados;
    private String[] personas = {"id_persona", "nom_persona"};
    private String[] dep_enviada = {"id_dep_envi", "nom_dep_envi"};
    private String[] ubicacion = {"id_ubica", "nom_ubica"};
    private String mCurrentPhotoBase64Pendiente = "";
    private String mCurrentPhotoPathPendiente = "";
    private String mCurrentPhotoNamePendiente = "";
    private File imagePendiente = null;
    private OutputStream outputStreamPendiente = null;
    private String mCurrentPhotoBase64Entregada = "";
    private String mCurrentPhotoPathEntregada = "";
    private String mCurrentPhotoNameEntregada = "";
    private File imageEntregada = null;
    private OutputStream outputStreamEntregada = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_officio);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        baseOficios = new BaseOficios(this);
        NOfficio_RutaPendiente = (TextView) findViewById(R.id.NOfficio_RutaPendiente);
        NOfficio_RutaPendiente.setText("");
        NOfficio_RutaEntregada = (TextView) findViewById(R.id.NOfficio_RutaEntregada);
        NOfficio_RutaEntregada.setText("");
        NOfficio_Pendiente = (Button) findViewById(R.id.NOfficio_Pendiente);
        NOfficio_Entregada = (Button) findViewById(R.id.NOfficio_Entregada);
        
        NOfficio_Pendiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                if (mCurrentPhotoBase64Pendiente.equals("")) {
                    new AlertDFont.Builder(NuevoOfficio.this)
                            .setMessage("La imagen se tomara de: ")
                            .setPositiveButton("Camara", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) { getcamara(); }})
                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setNeutralButton("Galery", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getgallery();
                                    
                                }
                            }).show();
                } else {
                    final android.app.AlertDialog.Builder alerBuilder1 = new android.app.AlertDialog.Builder(NuevoOfficio.this);
                    alerBuilder1.setMessage("Desea recapturar la imagen?")
                            .setTitle("Aviso")
                            .setCancelable(false)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new AlertDFont.Builder(NuevoOfficio.this)
                                            .setMessage("La imagen se tomara de: ")
                                            .setPositiveButton("Camara", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    getcamara();
                                                }
                                            })
                                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .setNeutralButton("Galery", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    getgallery();
                    
                                                }
                                            }).show();
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, null).show();
                    alerBuilder1.create();
                }
            }
        });
        NOfficio_Pendiente.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!NOfficio_Pendiente.getText().toString().equals("")) {
                    LayoutInflater factory = LayoutInflater.from(NuevoOfficio.this);
                    final View view = factory.inflate(R.layout.imagevisor, null);
                    ImageView imagevisor_image = view.findViewById(R.id.imagevisor_image);
                    Glide
                            .with(NuevoOfficio.this)
                            .load(mCurrentPhotoPathPendiente)
                            .apply(RequestOptions.fitCenterTransform())
                            .into(imagevisor_image);
                    new AlertDFont.Builder(NuevoOfficio.this).setCancelable(false)
                            .setView(view)
                            .setNegativeButton("Cancelar",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                    return true;
                }
                return false;
            }
        });
        NOfficio_Entregada.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!NOfficio_RutaEntregada.getText().toString().equals("")) {
                    LayoutInflater factory = LayoutInflater.from(NuevoOfficio.this);
                    final View view = factory.inflate(R.layout.imagevisor, null);
                    ImageView imagevisor_image = view.findViewById(R.id.imagevisor_image);
                    Glide
                            .with(NuevoOfficio.this)
                            .load(mCurrentPhotoPathEntregada)
                            .apply(RequestOptions.fitCenterTransform())
                            .into(imagevisor_image);
                    new AlertDFont.Builder(NuevoOfficio.this).setCancelable(false)
                            .setView(view)
                            .setNegativeButton("Cancelar",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                    return true;
                }
                return false;
            }
        });
        NOfficio_Entregada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                if (mCurrentPhotoBase64Entregada.equals("")) {
                    new AlertDFont.Builder(NuevoOfficio.this)
                            .setMessage("La imagen se tomara de: ")
                            .setPositiveButton("Camara", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getcamaraEntregada();
                                }
                            })
                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setNeutralButton("Galery", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getgallery_entregado();
                                }
                            }).show();
                } else {
                    final android.app.AlertDialog.Builder alerBuilder1 = new android.app.AlertDialog.Builder(NuevoOfficio.this);
                    alerBuilder1.setMessage("Desea recapturar la imagen?")
                            .setTitle("Aviso")
                            .setCancelable(false)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new AlertDFont.Builder(NuevoOfficio.this)
                                            .setMessage("La imagen se tomara de: ")
                                            .setPositiveButton("Camara", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    getcamaraEntregada();
                                                }
                                            })
                                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .setNeutralButton("Galery", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    getgallery_entregado();
                                                }
                                            }).show();
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, null).show();
                }
            }
        });
        
        NOfficio_Nomenclatura = (TextView) findViewById(R.id.NOfficio_Nomenclatura);
        NOfficio_Nomenclatura.setText(SharedPreference.GETSharedPreferences(Constant.SPNomenclaturaOficio, ""));
        
        NOfficio_Nomenclatura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater factory = LayoutInflater.from(NuevoOfficio.this);
                
                //text_entry is an Layout XML file containing two text field to display in alert dialog
                final View View = factory.inflate(R.layout.numberpickerlayout, null);
                final NumberPicker np1 = (NumberPicker) View.findViewById(R.id.Numberpicker_1);
                np1.setMaxValue(9);
                np1.setMinValue(0);
                final NumberPicker np2 = (NumberPicker) View.findViewById(R.id.Numberpicker_2);
                np2.setMaxValue(9);
                np2.setMinValue(0);
                
                final NumberPicker np3 = (NumberPicker) View.findViewById(R.id.Numberpicker_3);
                np3.setMaxValue(9);
                np3.setMinValue(0);
                final NumberPicker np4 = (NumberPicker) View.findViewById(R.id.Numberpicker_4);
                np4.setMaxValue(9);
                np4.setMinValue(0);
                new AlertDFont.Builder(NuevoOfficio.this)
                        .setTitle("Numero de Offico: ").setView(View).setPositiveButton("Guardar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String constructstring = "";
                                String[] splitstring = NOfficio_Nomenclatura.getText().toString().split("-");
                                String[] numberstring = splitstring[splitstring.length - 1].split("/");
                                numberstring[0] =
                                        String.valueOf(np1.getValue())
                                                + String.valueOf(np2.getValue())
                                                + String.valueOf(np3.getValue())
                                                + String.valueOf(np4.getValue());
                                for (int i = 0; i < splitstring.length - 1; i++) {
                                    constructstring += splitstring[i] + "-";
                                }
                                constructstring += numberstring[0] + "/" + numberstring[1];
                                NOfficio_Nomenclatura.setText(constructstring);
                            }
                            
                        }).setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }).show();
                
            }
        });
        NOfficio_Nombre = (AutoCompleteTextView) findViewById(R.id.NOfficio_Nombre);
        ArrayAdapter<String> adapterpersonas = new ArrayAdapter<String>
                (this, R.layout.spinner_item, baseOficios.Return_AutoCompletado(personas, "personas"));
        NOfficio_Nombre.setThreshold(1);
        NOfficio_Nombre.setAdapter(adapterpersonas);
        NOfficio_Nombre.setTextColor(Color.GRAY);
        NOfficio_Nombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NOfficio_Nombre.showDropDown();
            }
        });
        
        NOfficio_Dep_Sol = (Spinner) findViewById(R.id.NOfficio_Dep_Sol);
        ArrayAdapter<String> adapterdepartamentos = new ArrayAdapter<String>
                (NuevoOfficio.this, R.layout.spinner_item, departamentos);
        NOfficio_Dep_Sol.setAdapter(adapterdepartamentos);
        int lugardedep = 0;
        for (int i = 0; i < id_departamentos.length; i++) {
            if (id_departamentos[i].equals(SharedPreference.GETSharedPreferences(Constant.SPID_Oficio, ""))) {
                lugardedep = i;
            }
        }
        NOfficio_Dep_Sol.setSelection(lugardedep);
        
        NOfficio_Dep_Envi = (AutoCompleteTextView) findViewById(R.id.NOfficio_Dep_Envi);
        ArrayAdapter<String> adapterdep_enviada = new ArrayAdapter<String>
                (this, R.layout.spinner_item, baseOficios.Return_AutoCompletado(dep_enviada, "dep_enviada"));
        NOfficio_Dep_Envi.setThreshold(1);
        NOfficio_Dep_Envi.setAdapter(adapterdep_enviada);
        NOfficio_Dep_Envi.setTextColor(Color.GRAY);
        NOfficio_Dep_Envi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NOfficio_Dep_Envi.showDropDown();
            }
        });
        
        NOfficio_Asunto = (AutoCompleteTextView) findViewById(R.id.NOfficio_Asunto);
        NOfficio_Asunto.setTextColor(Color.GRAY);
        
        NOfficio_Estado = (Spinner) findViewById(R.id.NOfficio_Estado);
        ArrayAdapter<String> adapterestados = new ArrayAdapter<String>
                (NuevoOfficio.this, R.layout.spinner_item, estados);
        NOfficio_Estado.setAdapter(adapterestados);
        
        NOfficio_Ubicacion = (AutoCompleteTextView) findViewById(R.id.NOfficio_Ubicacion);
        ArrayAdapter<String> adapterubicacion = new ArrayAdapter<String>
                (this, R.layout.spinner_item, baseOficios.Return_AutoCompletado(ubicacion, "ubicacion"));
        NOfficio_Ubicacion.setThreshold(1);
        NOfficio_Ubicacion.setAdapter(adapterubicacion);
        NOfficio_Ubicacion.setHint("Ubicación de Oficio");
        NOfficio_Ubicacion.setTextColor(Color.GRAY);
        NOfficio_Ubicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NOfficio_Ubicacion.showDropDown();
            }
        });
        
        NOfficio_Notas = (EditText) findViewById(R.id.NOfficio_Notas);
        NOfficio_Notas.setTextColor(Color.GRAY);
        
        NOfficio_Guardar = (TextView) findViewById(R.id.NOfficio_Guardar);
        NOfficio_Guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baseOficios.SearchAddIfExists(NOfficio_Nombre.getText().toString(), "personas", personas);
                baseOficios.SearchAddIfExists(NOfficio_Dep_Envi.getText().toString(), "dep_enviada", dep_enviada);
                baseOficios.SearchAddIfExists(NOfficio_Ubicacion.getText().toString(), "ubicacion", ubicacion);
                RegisterOficio(
                        NOfficio_Nomenclatura.getText().toString(),
                        NOfficio_Nombre.getText().toString(),
                        NOfficio_Asunto.getText().toString(),
                        NOfficio_Ubicacion.getText().toString(),
                        NOfficio_Notas.getText().toString(),
                        NOfficio_Dep_Envi.getText().toString(),
                        id_departamentos[NOfficio_Dep_Sol.getSelectedItemPosition()],
                        id_estados[NOfficio_Estado.getSelectedItemPosition()],
                        SharedPreference.GETSharedPreferences(Constant.SPId_persona, ""),
                        mCurrentPhotoNamePendiente, mCurrentPhotoBase64Pendiente,
                        mCurrentPhotoNameEntregada, mCurrentPhotoBase64Entregada);
            }
        });
    }
    
    private void RegisterOficio(String Clave_Oficio,
                                String Nombre_Oficio,
                                String Asunto_Oficio,
                                String Ubica_Oficio,
                                String Notas_Oficio,
                                String DepEnviada_Oficio,
                                String idDep_Oficio,
                                String idEstado_Oficio,
                                String idPersona_Oficio,
                                String OficioPName, String OficioP64,
                                String OficioRName, String OficioR64) {
        ProgressDialog.Builder builder = new ProgressDialog.Builder(NuevoOfficio.this);
        builder.setMessage("Subiendo Oficio...");
        builder.setCancelable(false);
        final android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
        
        HashMap<String, String> map = new HashMap<>();
        map.put("Clave_Oficio", StringUtils.unaccent(Clave_Oficio));
        map.put("Nombre_Oficio", StringUtils.unaccent(Nombre_Oficio));
        map.put("Asunto_Oficio", StringUtils.unaccent(Asunto_Oficio));
        map.put("Ubica_Oficio", StringUtils.unaccent(Ubica_Oficio));
        map.put("Notas_Oficio", StringUtils.unaccent(Notas_Oficio));
        map.put("idDep_Oficio", StringUtils.unaccent(idDep_Oficio));
        map.put("idEstado_Oficio", StringUtils.unaccent(idEstado_Oficio));
        map.put("idPersona_Oficio", StringUtils.unaccent(idPersona_Oficio));
        map.put("DepEnviada_Oficio", StringUtils.unaccent(DepEnviada_Oficio));
        map.put("OficioP", StringUtils.unaccent(OficioPName));
        map.put("OficioP64", OficioP64);
        map.put("OficioR", StringUtils.unaccent(OficioRName));
        map.put("OficioR64", OficioR64);
        JSONObject object = new JSONObject(map);
        Log.e("HashMap", object.toString());
        JsonObjectRequest Req = new JsonObjectRequest(
                Request.Method.POST,
                Constant.WS_SubirOficio,
                object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            alertDialog.dismiss();
                            Log.e("WS-Response", response.toString());
                            if (response.getString("Response").equals("Success")) {
                                new AlertDFont.Builder(NuevoOfficio.this)
                                        .setMessage("Informacion Guardada").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        NuevoOfficio.this.finish();
                                    }
                                }).show();
                            } else {
                                // TODO: Guardar localmente
                                new AlertDFont.Builder(NuevoOfficio.this)
                                        .setMessage("Su solicitud no se ha podido realizar con exito.").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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
                        new AlertDFont.Builder(NuevoOfficio.this).setMessage(error.getCause().toString()).show();
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
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.Camera_CODE && resultCode == RESULT_OK) {
            setPic();
        }
        if (requestCode == Constant.Camera_CODE_Entregado && resultCode == RESULT_OK) {
            setPicEntregada();
        }
        if (requestCode == Constant.getgallery && resultCode == RESULT_OK) {
            try {
                @SuppressLint("SimpleDateFormat") String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                InputStream inputStream = NuevoOfficio.this.getContentResolver().openInputStream(data.getData());
                String imageFileName = getResources().getString(R.string.app_name) + "_" + timestamp;
                Bitmap bitmap = BitmapFactory.decodeStream(new BufferedInputStream(inputStream));
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                mCurrentPhotoBase64Pendiente = Base64.encodeToString(os.toByteArray(), Base64.DEFAULT);
                mCurrentPhotoNamePendiente = imageFileName + ".jpg";
                NOfficio_RutaPendiente.setText(mCurrentPhotoNamePendiente);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == Constant.getgallery_entregado && resultCode == RESULT_OK) {
            try {
                @SuppressLint("SimpleDateFormat") String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                InputStream inputStream = NuevoOfficio.this.getContentResolver().openInputStream(data.getData());
                String imageFileName = getResources().getString(R.string.app_name) + "_" + timestamp;
                Bitmap bitmap = BitmapFactory.decodeStream(new BufferedInputStream(inputStream));
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                mCurrentPhotoBase64Entregada = Base64.encodeToString(os.toByteArray(), Base64.DEFAULT);
                mCurrentPhotoNameEntregada = imageFileName + ".jpg";
                NOfficio_RutaEntregada.setText(mCurrentPhotoNameEntregada);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ CAMARA PERMISSION
    private void getcamara() {
        if (ContextCompat.checkSelfPermission(NuevoOfficio.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(NuevoOfficio.this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, Constant.Camera_CODE);
        } else {
            dispatchTakePictureIntent();
        }
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (takePictureIntent.resolveActivity(Objects.requireNonNull(getApplicationContext()).getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), "com.BeeDocs.android.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        takePictureIntent.setClipData(ClipData.newRawUri("", photoURI));
                    }
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    
                    startActivityForResult(takePictureIntent, Constant.Camera_CODE);
                }
            }
        }
    }
    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat") String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = getResources().getString(R.string.app_name) + "_" + timestamp;
        File storageDir = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            storageDir = (Objects.requireNonNull(NuevoOfficio.this)).getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }
        mCurrentPhotoNamePendiente = imageFileName + ".jpg";
        imagePendiente = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPathPendiente = imagePendiente.getAbsolutePath();
        return imagePendiente;
    }
    private void setPic() {
        //TODO: Get the dimensions of the view;
        int targetWidth = 550;
        //                Main_Evidencia.getWidth();
        int targetHeight = 550;
        //                Main_Evidencia.getHeight();
        //TODO: Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPathPendiente, bmOptions);
        int photoWidth = bmOptions.outWidth;
        int photoheight = bmOptions.outHeight;
        
        //TODO: Determine how much to scale down the image
        int scaleFactor = Math.min(photoWidth / targetWidth, photoheight / targetHeight);
        
        //TODO: Decode the image file into a Bitmap sized to fill the view
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        
        try {
            int m_compress = 100;
            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPathPendiente, bmOptions);
            NOfficio_RutaPendiente.setText(mCurrentPhotoNamePendiente);
            outputStreamPendiente = new FileOutputStream(imagePendiente);
            bitmap.compress(Bitmap.CompressFormat.JPEG, m_compress, outputStreamPendiente);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            mCurrentPhotoBase64Pendiente = Base64.encodeToString(os.toByteArray(), Base64.DEFAULT);
            outputStreamPendiente.flush();
            outputStreamPendiente.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ CAMARA PERMISSION ENTREGADO
    private void getcamaraEntregada() {
        if (ContextCompat.checkSelfPermission(NuevoOfficio.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(NuevoOfficio.this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, Constant.Camera_CODE);
        } else {
            dispatchTakePictureIntentEntregada();
        }
    }
    private void dispatchTakePictureIntentEntregada() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (takePictureIntent.resolveActivity(Objects.requireNonNull(getApplicationContext()).getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFileEntregada();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile
                            (getApplicationContext(), "com.BeeDocs.android.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        takePictureIntent.setClipData(ClipData.newRawUri("", photoURI));
                    }
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    
                    startActivityForResult(takePictureIntent, Constant.Camera_CODE_Entregado);
                }
            }
        }
    }
    private File createImageFileEntregada() throws IOException {
        @SuppressLint("SimpleDateFormat") String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = getResources().getString(R.string.app_name) + "_" + timestamp;
        File storageDir = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            storageDir = (Objects.requireNonNull(NuevoOfficio.this)).getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }
        mCurrentPhotoNameEntregada = imageFileName + ".jpg";
        imageEntregada = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPathEntregada = imageEntregada.getAbsolutePath();
        return imageEntregada;
    }
    private void setPicEntregada() {
        //TODO: Get the dimensions of the view;
        int targetWidth = 550;
        //                Main_Evidencia.getWidth();
        int targetHeight = 550;
        //                Main_Evidencia.getHeight();
        //TODO: Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPathEntregada, bmOptions);
        int photoWidth = bmOptions.outWidth;
        int photoheight = bmOptions.outHeight;
        
        //TODO: Determine how much to scale down the image
        int scaleFactor = Math.min(photoWidth / targetWidth, photoheight / targetHeight);
        
        //TODO: Decode the image file into a Bitmap sized to fill the view
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        
        try {
            int m_compress = 100;
            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPathEntregada, bmOptions);
            NOfficio_RutaEntregada.setText(mCurrentPhotoNameEntregada);
            outputStreamEntregada = new FileOutputStream(imageEntregada);
            bitmap.compress(Bitmap.CompressFormat.JPEG, m_compress, outputStreamEntregada);
            //            imagen_camara.setImageBitmap(bitmap);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            mCurrentPhotoBase64Entregada = Base64.encodeToString(os.toByteArray(), Base64.DEFAULT);
            outputStreamEntregada.flush();
            outputStreamEntregada.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void getgallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccióna la imagen"), Constant.getgallery);
    }
    private void getgallery_entregado(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccióna la imagen"), Constant.getgallery_entregado);
    }
}