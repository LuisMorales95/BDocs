package com.BeeDocs;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.BeeDocs.GlideApp;
import com.BeeDocs.R;

import static com.BeeDocs.Constant.WS_UpdateMemoran;
import static com.BeeDocs.Officios.baseOficios;
import static com.BeeDocs.SharedPreference.GETSharedPreferences;

public class UpdateMemoran extends AppCompatActivity {
    
    static ModelMemoran modelMemoran;
    
    static String Nomenclatura, Nombre, Dep_Envi, Asunto, Ubicacion, Notas;
    static int Dep_Sol, Estado, idOficio;
    static int es_mio = 1;
    Button
            NOfficio_Pendiente, NOfficio_Entregada;
    EditText NOfficio_Notas;
    AutoCompleteTextView NOfficio_Nombre, NOfficio_Dep_Envi, NOfficio_Asunto, NOfficio_Ubicacion;
    Spinner NOfficio_Dep_Sol, NOfficio_Estado;
    TextView NOfficio_Nomenclatura, NOfficio_Guardar,
            NOfficio_RutaPendiente, NOfficio_RutaEntregada;
    private String[] personas = {"id_persona", "nom_persona"};
    private String[] dep_enviada = {"id_dep_envi", "nom_dep_envi"};
    private String[] ubicacion = {"id_ubica", "nom_ubica"};
    private String[] departamentos = Memoran.departamentos, id_departamentos = Memoran.id_departamentos, clave_departamento = Memoran.clave_departamento;
    private String[] estados = Memoran.estados, id_estados = Memoran.id_estados;
    
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_officio);
        baseOficios = new BaseOficios(this);
        ((TextView) findViewById(R.id.NOfficio_Titulo)).setText("Actualizar Memoran");
        NOfficio_RutaPendiente = (TextView) findViewById(R.id.NOfficio_RutaPendiente);
        NOfficio_RutaPendiente.setText(modelMemoran.getRutaMemoranP());
        NOfficio_RutaEntregada = (TextView) findViewById(R.id.NOfficio_RutaEntregada);
        NOfficio_RutaEntregada.setText(modelMemoran.getRutaMemoranR());
        NOfficio_Pendiente = (Button) findViewById(R.id.NOfficio_Pendiente);
        NOfficio_Pendiente.setText("Memoran");
        NOfficio_Entregada = (Button) findViewById(R.id.NOfficio_Entregada);
        
        NOfficio_Pendiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                if (modelMemoran.getRutaMemoranP().equals("")) {
                    getcamara();
                } else {
                    final android.app.AlertDialog.Builder alerBuilder1 = new android.app.AlertDialog.Builder(UpdateMemoran.this);
                    alerBuilder1.setMessage("Desea recapturar la imagen?")
                            .setTitle("Aviso")
                            .setCancelable(false)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getcamara();
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
                if (NOfficio_RutaPendiente.getText().toString().contains("MemoranYRecibos/")) {
                    LayoutInflater factory = LayoutInflater.from(UpdateMemoran.this);
                    final View view = factory.inflate(R.layout.imagevisor, null);
                    ImageView imagevisor_image = view.findViewById(R.id.imagevisor_image);
                    GlideApp
                            .with(UpdateMemoran.this)
                            .load(Constant.URL_Address + modelMemoran.getRutaMemoranP())
                            .fitCenter()
                            .into(imagevisor_image);
                    new AlertDFont.Builder(UpdateMemoran.this).setCancelable(false)
                            .setView(view)
                            .setNegativeButton("Cancelar",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                    return true;
                }else if (!NOfficio_Pendiente.getText().toString().equals("")){
                    LayoutInflater factory = LayoutInflater.from(UpdateMemoran.this);
                    final View view = factory.inflate(R.layout.imagevisor, null);
                    ImageView imagevisor_image = view.findViewById(R.id.imagevisor_image);
                    GlideApp
                            .with(UpdateMemoran.this)
                            .load(mCurrentPhotoPathPendiente)
                            .fitCenter()
                            .into(imagevisor_image);
                    new AlertDFont.Builder(UpdateMemoran.this).setCancelable(false)
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
                if (NOfficio_RutaEntregada.getText().toString().contains("MemoranYRecibos/")) {
                    LayoutInflater factory = LayoutInflater.from(UpdateMemoran.this);
                    final View view = factory.inflate(R.layout.imagevisor, null);
                    ImageView imagevisor_image = view.findViewById(R.id.imagevisor_image);
                    GlideApp
                            .with(UpdateMemoran.this)
                            .load(Constant.URL_Address + modelMemoran.getRutaMemoranR())
                            .fitCenter()
                            .into(imagevisor_image);
                    new AlertDFont.Builder(UpdateMemoran.this).setCancelable(false)
                            .setView(view)
                            .setNegativeButton("Cancelar",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                    return true;
                }else if (!NOfficio_RutaEntregada.getText().toString().equals("")){
                    LayoutInflater factory = LayoutInflater.from(UpdateMemoran.this);
                    final View view = factory.inflate(R.layout.imagevisor, null);
                    ImageView imagevisor_image = view.findViewById(R.id.imagevisor_image);
                    GlideApp
                            .with(UpdateMemoran.this)
                            .load(mCurrentPhotoPathEntregada)
                            .fitCenter()
                            .into(imagevisor_image);
                    new AlertDFont.Builder(UpdateMemoran.this).setCancelable(false)
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
                if (modelMemoran.getRutaMemoranR().equals("")) {
                    getcamaraEntregada();
                } else {
                    final android.app.AlertDialog.Builder alerBuilder1 = new android.app.AlertDialog.Builder(UpdateMemoran.this);
                    alerBuilder1.setMessage("Desea recapturar la imagen?")
                            .setTitle("Aviso")
                            .setCancelable(false)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getcamaraEntregada();
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, null).show();
                    alerBuilder1.create();
                }
            }
        });
        NOfficio_Nomenclatura = (TextView) findViewById(R.id.NOfficio_Nomenclatura);
        NOfficio_Nomenclatura.setText(modelMemoran.getCla_memoran());
        NOfficio_Nomenclatura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater factory = LayoutInflater.from(UpdateMemoran.this);
                
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
                new AlertDFont.Builder(UpdateMemoran.this)
                        .setTitle("Numero de Memoran: ").setView(View).setPositiveButton("Guardar",
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
        NOfficio_Nombre.setText(modelMemoran.getNom_memoran());
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
        ArrayAdapter<String> adapterdepartamentos = new ArrayAdapter<String>(UpdateMemoran.this, R.layout.spinner_item, departamentos);
        NOfficio_Dep_Sol.setAdapter(adapterdepartamentos);
        int lugardedep = 0;
        for (int i = 0; i < id_departamentos.length; i++) {
            if (id_departamentos[i].equals(modelMemoran.getFkid_depepartamento())) {
                lugardedep = i;
            }
        }
        NOfficio_Dep_Sol.setSelection(lugardedep);
        NOfficio_Dep_Sol.setEnabled(true);
    
        NOfficio_Dep_Envi = (AutoCompleteTextView) findViewById(R.id.NOfficio_Dep_Envi);
        NOfficio_Dep_Envi.setTextColor(Color.GRAY);
        NOfficio_Dep_Envi.setText(modelMemoran.getDepenviada());
        ArrayAdapter<String> adapterdep_enviada = new ArrayAdapter<String>(this, R.layout.spinner_item, baseOficios.Return_AutoCompletado(dep_enviada, "dep_enviada"));
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
        NOfficio_Asunto.setText(modelMemoran.getAsu_memoran());
        NOfficio_Asunto.setTextColor(Color.GRAY);
        
        NOfficio_Estado = (Spinner) findViewById(R.id.NOfficio_Estado);
        ArrayAdapter<String> adapterestados = new ArrayAdapter<String>
                (UpdateMemoran.this, R.layout.spinner_item, estados);
        
        NOfficio_Estado.setAdapter(adapterestados);
        NOfficio_Estado.setSelection(Integer.valueOf(modelMemoran.getFkid_estado()) - 1);
        
        NOfficio_Ubicacion = (AutoCompleteTextView) findViewById(R.id.NOfficio_Ubicacion);
        NOfficio_Ubicacion.setText(modelMemoran.getUbi_memoran());
        ArrayAdapter<String> adapterubicacion = new ArrayAdapter<String>
                (this, R.layout.spinner_item, baseOficios.Return_AutoCompletado(ubicacion, "ubicacion"));
        NOfficio_Ubicacion.setThreshold(1);
        NOfficio_Ubicacion.setAdapter(adapterubicacion);
        NOfficio_Ubicacion.setTextColor(Color.GRAY);
        NOfficio_Ubicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NOfficio_Ubicacion.showDropDown();
            }
        });
        NOfficio_Notas = (EditText) findViewById(R.id.NOfficio_Notas);
        NOfficio_Notas.setText(modelMemoran.getNota_memoran());
        NOfficio_Notas.setTextColor(Color.GRAY);
        NOfficio_Guardar = (TextView) findViewById(R.id.NOfficio_Guardar);
        NOfficio_Guardar.setText("Actualizar");
        
        NOfficio_Guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modelMemoran.getFkid_persona().equals(GETSharedPreferences(Constant.SPId_persona, ""))) {
                    if (NOfficio_RutaPendiente.getText().toString().contains("MemoranYRecibos/")) {
                        String[] split = NOfficio_RutaPendiente.getText().toString().split("/");
                        mCurrentPhotoNamePendiente = split[1];
                        mCurrentPhotoBase64Pendiente = "";
                    }
                    if (NOfficio_RutaEntregada.getText().toString().contains("MemoranYRecibos/")) {
                        String[] split = NOfficio_RutaEntregada.getText().toString().split("/");
                        mCurrentPhotoNameEntregada = split[1];
                        mCurrentPhotoBase64Entregada = "";
                    }
                    UpdateMemoran(
                            String.valueOf(modelMemoran.getId_memoran()),
                            NOfficio_Nomenclatura.getText().toString(),
                            NOfficio_Nombre.getText().toString(),
                            NOfficio_Asunto.getText().toString(),
                            NOfficio_Ubicacion.getText().toString(),
                            NOfficio_Notas.getText().toString(),
                            NOfficio_Dep_Envi.getText().toString(),
                            id_departamentos[NOfficio_Dep_Sol.getSelectedItemPosition()],
                            id_estados[NOfficio_Estado.getSelectedItemPosition()],
                            GETSharedPreferences(Constant.SPId_persona, ""),
                            mCurrentPhotoNamePendiente, mCurrentPhotoBase64Pendiente,
                            mCurrentPhotoNameEntregada, mCurrentPhotoBase64Entregada);
                    
                } else {
                    new AlertDFont.Builder(UpdateMemoran.this)
                            .setMessage("Permiso denegado: \n Informar cambios al creador")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                }
            }
        });
        
    }
    
    private void UpdateMemoran(String Id_Memoran,
                              String Clave_Memoran,
                              String Nombre_Memoran,
                              String Asunto_Memoran,
                              String Ubica_Memoran,
                              String Notas_Memoran,
                              String DepEnviada_Memoran,
                              String idDep_Memoran,
                              String idEstado_Memoran,
                              String idPersona_Memoran,
                              String MemoranPName, String MemoranP64,
                              String MemoranRName, String MemoranR64) {
        ProgressDialog.Builder builder = new ProgressDialog.Builder(UpdateMemoran.this);
        builder.setMessage("Actualizando Info Oficio...");
        builder.setCancelable(false);
        final android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
        
        HashMap<String, String> map = new HashMap<>();
        map.put("Id_Oficio", StringUtils.unaccent(Id_Memoran));
        map.put("Clave_Oficio", StringUtils.unaccent(Clave_Memoran));
        map.put("Nombre_Oficio", StringUtils.unaccent(Nombre_Memoran));
        map.put("Asunto_Oficio", StringUtils.unaccent(Asunto_Memoran));
        map.put("Ubica_Oficio", StringUtils.unaccent(Ubica_Memoran));
        map.put("Notas_Oficio", StringUtils.unaccent(Notas_Memoran));
        map.put("idDep_Oficio", StringUtils.unaccent(idDep_Memoran));
        map.put("idEstado_Oficio", StringUtils.unaccent(idEstado_Memoran));
        map.put("idPersona_Oficio", StringUtils.unaccent(idPersona_Memoran));
        map.put("DepEnviada_Oficio", StringUtils.unaccent(DepEnviada_Memoran));
        map.put("OficioP", StringUtils.unaccent(MemoranPName));
        map.put("OficioP64", MemoranP64);
        map.put("OficioR", StringUtils.unaccent(MemoranRName));
        map.put("OficioR64", MemoranR64);
        JSONObject object = new JSONObject(map);
        Log.e("HashMap", object.toString());
        JsonObjectRequest Req = new JsonObjectRequest(
                Request.Method.POST,
                WS_UpdateMemoran,
                object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            alertDialog.dismiss();
                            Log.e("WS-Response", response.toString());
                            if (response.getString("Response").equals("Success")) {
                                new AlertDFont.Builder(UpdateMemoran.this)
                                        .setMessage("Informacion Actualizada").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        Intent returnIntent = new Intent();
                                        setResult(Activity.RESULT_OK, returnIntent);
                                        UpdateMemoran.this.finish();
                                        
                                    }
                                }).show();
                            } else {
                                // TODO: Guardar localmente
                                new AlertDFont.Builder(UpdateMemoran.this)
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
                        new AlertDFont.Builder(UpdateMemoran.this).setMessage(error.getCause().toString()).show();
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
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constant.Camera_CODE && resultCode == RESULT_OK) {
            setPic();
        }
        if (requestCode == Constant.Camera_CODE_Entregado && resultCode == RESULT_OK) {
            setPicEntregada();
        }
    }
    
    
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++                              CAMARA PERMISSION
    
    private void getcamara() {
        if (ContextCompat.checkSelfPermission(UpdateMemoran.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UpdateMemoran.this, new String[]{
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
                    Uri photoURI = FileProvider.getUriForFile
                            (getApplicationContext(), "com.BeeDocs.android.fileprovider", photoFile);
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
            storageDir = (Objects.requireNonNull(UpdateMemoran.this)).getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
            int m_compress = 50;
            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPathPendiente, bmOptions);
            NOfficio_RutaPendiente.setText(mCurrentPhotoNamePendiente);
            outputStreamPendiente = new FileOutputStream(imagePendiente);
            bitmap.compress(Bitmap.CompressFormat.JPEG, m_compress, outputStreamPendiente);
            //            imagen_camara.setImageBitmap(bitmap);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            mCurrentPhotoBase64Pendiente = Base64.encodeToString(os.toByteArray(), Base64.DEFAULT);
            outputStreamPendiente.flush();
            outputStreamPendiente.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++                              CAMARA PERMISSION ENTREGADO
    
    
    private void getcamaraEntregada() {
        if (ContextCompat.checkSelfPermission(UpdateMemoran.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UpdateMemoran.this, new String[]{
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
            storageDir = (Objects.requireNonNull(UpdateMemoran.this)).getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
            int m_compress = 50;
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
    
    
}
