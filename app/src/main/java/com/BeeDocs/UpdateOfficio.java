package com.BeeDocs;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
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
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.BeeDocs.Constant.SPApellidoM_persona;
import static com.BeeDocs.Constant.SPApellidoP_persona;
import static com.BeeDocs.Constant.SPNombre_persona;
import static com.BeeDocs.Constant.WS_SendEmail;
import static com.BeeDocs.Constant.WS_UpdateOficio;
import static com.BeeDocs.Officios.baseOficios;
import static com.BeeDocs.SharedPreference.GETSharedPreferences;

public class UpdateOfficio extends AppCompatActivity {
    
    static ModelOficio modelOficio;
    private static String Carpeta_App = "com.BeeDocs";
    private static String Carpeta_PDF = "PDFs";
    Button NOfficio_Pendiente, NOfficio_Entregada;
    EditText NOfficio_Notas;
    AutoCompleteTextView NOfficio_Nombre, NOfficio_Dep_Envi, NOfficio_Asunto, NOfficio_Ubicacion;
    Spinner NOfficio_Dep_Sol, NOfficio_Estado;
    TextView NOfficio_Nomenclatura, NOfficio_Guardar,
            NOfficio_RutaPendiente, NOfficio_RutaEntregada;
    private ImageView NOfficio_ShareDoc;
    private String[] personas = {"id_persona", "nom_persona"};
    private String[] dep_enviada = {"id_dep_envi", "nom_dep_envi"};
    private String[] ubicacion = {"id_ubica", "nom_ubica"};
    private String[] departamentos = Officios.departamentos, id_departamentos = Officios.id_departamentos, clave_departamento = Officios.clave_departamento;
    private String[] estados = Officios.estados, id_estados = Officios.id_estados;
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
    ProgressDialog progressDFont;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_officio);
        progressDFont = new ProgressDialog(this);
        baseOficios = new BaseOficios(this);
        NOfficio_ShareDoc = (ImageView) findViewById(R.id.NOfficio_ShareDoc);
        ((TextView) findViewById(R.id.NOfficio_Titulo)).setText("Actualizar Oficio");
        NOfficio_RutaPendiente = (TextView) findViewById(R.id.NOfficio_RutaPendiente);
        NOfficio_RutaPendiente.setText(modelOficio.getRutaOficioP());
        NOfficio_RutaEntregada = (TextView) findViewById(R.id.NOfficio_RutaEntregada);
        NOfficio_RutaEntregada.setText(modelOficio.getRutaOficioR());
        NOfficio_Pendiente = (Button) findViewById(R.id.NOfficio_Pendiente);
        NOfficio_Entregada = (Button) findViewById(R.id.NOfficio_Entregada);
        
        NOfficio_Pendiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                if (modelOficio.getRutaOficioP().equals("")) {
                    getcamara();
                } else {
                    final android.app.AlertDialog.Builder alerBuilder1 = new android.app.AlertDialog.Builder(UpdateOfficio.this);
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
                if (NOfficio_RutaPendiente.getText().toString().contains("OficiosYRecibos/")) {
                    LayoutInflater factory = LayoutInflater.from(UpdateOfficio.this);
                    final View view = factory.inflate(R.layout.imagevisor, null);
                    ImageView imagevisor_image = view.findViewById(R.id.imagevisor_image);
                    GlideApp
                            .with(UpdateOfficio.this)
                            .load(Constant.URL_Address + modelOficio.getRutaOficioP())
                            .fitCenter()
                            .into(imagevisor_image);
                    new AlertDFont.Builder(UpdateOfficio.this).setCancelable(false)
                            .setView(view)
                            .setNegativeButton("Cancelar",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                    return true;
                } else if (!NOfficio_Pendiente.getText().toString().equals("")) {
                    LayoutInflater factory = LayoutInflater.from(UpdateOfficio.this);
                    final View view = factory.inflate(R.layout.imagevisor, null);
                    ImageView imagevisor_image = view.findViewById(R.id.imagevisor_image);
                    GlideApp
                            .with(UpdateOfficio.this)
                            .load(mCurrentPhotoPathPendiente)
                            .fitCenter()
                            .into(imagevisor_image);
                    new AlertDFont.Builder(UpdateOfficio.this).setCancelable(false)
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
                if (NOfficio_RutaEntregada.getText().toString().contains("OficiosYRecibos/")) {
                    LayoutInflater factory = LayoutInflater.from(UpdateOfficio.this);
                    final View view = factory.inflate(R.layout.imagevisor, null);
                    ImageView imagevisor_image = view.findViewById(R.id.imagevisor_image);
                    GlideApp
                            .with(UpdateOfficio.this)
                            .load(Constant.URL_Address + modelOficio.getRutaOficioR())
                            .fitCenter()
                            .into(imagevisor_image);
                    new AlertDFont.Builder(UpdateOfficio.this).setCancelable(false)
                            .setView(view)
                            .setNegativeButton("Cancelar",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                    return true;
                } else if (!NOfficio_RutaEntregada.getText().toString().equals("")) {
                    LayoutInflater factory = LayoutInflater.from(UpdateOfficio.this);
                    final View view = factory.inflate(R.layout.imagevisor, null);
                    ImageView imagevisor_image = view.findViewById(R.id.imagevisor_image);
                    GlideApp
                            .with(UpdateOfficio.this)
                            .load(mCurrentPhotoPathEntregada)
                            .fitCenter()
                            .into(imagevisor_image);
                    new AlertDFont.Builder(UpdateOfficio.this).setCancelable(false)
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
                if (modelOficio.getRutaOficioR().equals("")) {
                    getcamaraEntregada();
                } else {
                    final android.app.AlertDialog.Builder alerBuilder1 = new android.app.AlertDialog.Builder(UpdateOfficio.this);
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
        NOfficio_Nomenclatura.setText(modelOficio.getCla_oficio());
        NOfficio_Nomenclatura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater factory = LayoutInflater.from(UpdateOfficio.this);
                
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
                new AlertDFont.Builder(UpdateOfficio.this)
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
        NOfficio_Nombre.setText(modelOficio.getNom_oficio());
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
        ArrayAdapter<String> adapterdepartamentos = new ArrayAdapter<String>(UpdateOfficio.this, R.layout.spinner_item, departamentos);
        NOfficio_Dep_Sol.setAdapter(adapterdepartamentos);
        int lugardedep = 0;
        for (int i = 0; i < id_departamentos.length; i++) {
            if (id_departamentos[i].equals(modelOficio.getFkid_depepartamento())) {
                lugardedep = i;
            }
        }
        NOfficio_Dep_Sol.setSelection(lugardedep);
        NOfficio_Dep_Sol.setEnabled(true);
        
        NOfficio_Dep_Envi = (AutoCompleteTextView) findViewById(R.id.NOfficio_Dep_Envi);
        NOfficio_Dep_Envi.setTextColor(Color.GRAY);
        NOfficio_Dep_Envi.setText(modelOficio.getDepenviada());
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
        NOfficio_Asunto.setText(modelOficio.getAsu_oficio());
        NOfficio_Asunto.setTextColor(Color.GRAY);
        
        NOfficio_Estado = (Spinner) findViewById(R.id.NOfficio_Estado);
        ArrayAdapter<String> adapterestados = new ArrayAdapter<String>
                (UpdateOfficio.this, R.layout.spinner_item, estados);
        
        NOfficio_Estado.setAdapter(adapterestados);
        NOfficio_Estado.setSelection(Integer.valueOf(modelOficio.getFkid_estado()) - 1);
        
        NOfficio_Ubicacion = (AutoCompleteTextView) findViewById(R.id.NOfficio_Ubicacion);
        NOfficio_Ubicacion.setText(modelOficio.getUbi_oficio());
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
        NOfficio_Notas.setText(modelOficio.getNota_oficio());
        NOfficio_Notas.setTextColor(Color.GRAY);
        NOfficio_Guardar = (TextView) findViewById(R.id.NOfficio_Guardar);
        NOfficio_Guardar.setText("Actualizar");
        
        NOfficio_Guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modelOficio.getFkid_persona().equals(GETSharedPreferences(Constant.SPId_persona, ""))) {
                    if (NOfficio_RutaPendiente.getText().toString().contains("OficiosYRecibos/")) {
                        String[] split = NOfficio_RutaPendiente.getText().toString().split("/");
                        mCurrentPhotoNamePendiente = split[1];
                        mCurrentPhotoBase64Pendiente = "";
                    }
                    if (NOfficio_RutaEntregada.getText().toString().contains("OficiosYRecibos/")) {
                        String[] split = NOfficio_RutaEntregada.getText().toString().split("/");
                        mCurrentPhotoNameEntregada = split[1];
                        mCurrentPhotoBase64Entregada = "";
                    }
                    UpdateOficio(
                            String.valueOf(modelOficio.getId_oficio()),
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
                    new AlertDFont.Builder(UpdateOfficio.this)
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
        NOfficio_ShareDoc.setVisibility(View.VISIBLE);
        NOfficio_ShareDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email="";
                AlertDFont.Builder dialogBuilder = new AlertDFont.Builder(UpdateOfficio.this);
                LayoutInflater inflater = UpdateOfficio.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.singleedittext, null);
                dialogBuilder.setView(dialogView);
                final EditText email = (EditText) dialogView.findViewById(R.id.SingleEmail);
                email.setHint("Escriba el Correo");
                dialogBuilder.setTitle("Correo Electronico");
                dialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!email.getText().toString().isEmpty()&&isValidEmail(email.getText().toString())){
                            Toast.makeText(UpdateOfficio.this, "Correo valido", Toast.LENGTH_SHORT).show();
                            progressDFont.setMessage("Comenzando Proceso PDF");
                            progressDFont.setCancelable(false);
                            progressDFont.show();
                            new Share_PDF(UpdateOfficio.this,email.getText().toString()).execute();
                        }else{
                            Toast.makeText(UpdateOfficio.this, "Correo invalido o vacio", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
            }
        });
    
    }
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    
    public class Share_PDF extends AsyncTask<Void,Boolean,Boolean>{
        private Context context;
        private String Email;
        public Share_PDF(Context context, String email) {
            this.context = context;
            Email = email;
        }
        private File filehome=null;
        private String NomenclaturaDate="";
        private String nombre_completo;
    
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                @SuppressLint("SimpleDateFormat") String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                NomenclaturaDate = "ArchivoPDF" + timestamp + ".pdf";
                Document document = new Document(PageSize.LETTER);
                String StorageInterno = Environment.getExternalStorageDirectory().toString();
                File file = new File(StorageInterno + File.separator + "Android" + File.separator + "data" + File.separator + Carpeta_App);
                if (!file.exists()) { file.mkdir(); }
                filehome = new File(file.getPath() + File.separator + Carpeta_PDF);
                if (!filehome.exists()) { filehome.mkdir(); }
                nombre_completo = Environment.getExternalStorageDirectory() + File.separator + "Android" + File.separator +
                        "data" + File.separator + Carpeta_App + File.separator + Carpeta_PDF + File.separator + NomenclaturaDate;
                File outputfile = new File(nombre_completo);
                if (outputfile.exists()) {
                    outputfile.delete();
                }
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(nombre_completo));
                document.open();
                document.addAuthor(GETSharedPreferences(SPNombre_persona, "") + " " +
                        GETSharedPreferences(SPApellidoP_persona, "") +
                        " " + GETSharedPreferences(SPApellidoM_persona, ""));
                document.addCreator(getResources().getString(R.string.app_name));
                document.addCreationDate();
                document.addTitle(NomenclaturaDate);
                
                if (!modelOficio.getRutaOficioP().isEmpty()) {
                    int SDK_INT = android.os.Build.VERSION.SDK_INT;
                    if (SDK_INT > 8) {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                    }
                    String URL = Constant.URL_Address + modelOficio.getRutaOficioP();
                    Image image = Image.getInstance(URL.replace(" ", "%20"));
                    float scaler = ((document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin() - 0) / image.getWidth()) * 100;
                    image.scalePercent(scaler);
                    image.setAlignment(Image.ALIGN_CENTER | Image.BOTTOM);
                    document.add(image);
                }
                if (!modelOficio.getRutaOficioR().isEmpty()) {
                    int SDK_INT = android.os.Build.VERSION.SDK_INT;
                    if (SDK_INT > 8) {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                    }
                    String URL2 = Constant.URL_Address + modelOficio.getRutaOficioR();
                    Image image2 = Image.getInstance(URL2.replace(" ", "%20"));
                    float scaler2 = ((document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin() - 0) / image2.getWidth()) * 100;
                    image2.scalePercent(scaler2);
                    image2.setAlignment(Image.ALIGN_CENTER | Image.BOTTOM);
                    document.add(image2);
                }
                document.close();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(UpdateOfficio.this, "PDF Creado", Toast.LENGTH_LONG).show();
                    }
                });
                return true;
            } catch (DocumentException e) {
                e.printStackTrace();
                return false;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            progressDFont.dismiss();
            if (aBoolean) {
                String PDF_base64 = "";
                File PDF = new File(nombre_completo);
                byte[] bytes = new byte[(int) PDF.length()];
                try {
                    FileInputStream fileInputStream = new FileInputStream(PDF);
                    fileInputStream.read(bytes);
                    for (int j = 0; j < bytes.length; j++) {
                        System.out.print((char) bytes[j]);
                    }
                    byte[] bytefileArray = FileUtils.readFileToByteArray(PDF);
                    if (bytefileArray.length>0){
                        PDF_base64 = android.util.Base64.encodeToString(bytefileArray, Base64.NO_WRAP);
                    }
                } catch (FileNotFoundException e) {
                    System.out.println("File Not Found.");
                    e.printStackTrace();
                } catch (IOException e1) {
                    System.out.println("Error Reading The File.");
                    e1.printStackTrace();
                }
                EnviarCorreo(Email,NomenclaturaDate,PDF_base64);
            }else{
                new AlertDFont.Builder(context).setMessage("PDF no Generado").show();
            }
        }
    }
    
    private void EnviarCorreo(String ws_PDFEmail,String ws_PDFName,String ws_PDFBase64){
        final ProgressDialog builder = new ProgressDialog(UpdateOfficio.this);
        builder.setMessage("Verificando envio de Correo...");
        builder.setCancelable(false);
        builder.show();
        
        HashMap<String, String>map = new HashMap<>();
        map.put("ws_PDFEmail", ws_PDFEmail);
        map.put("ws_PDFName", ws_PDFName);
        map.put("ws_PDFBase64", ws_PDFBase64);
        JSONObject object = new JSONObject(map);
        JsonObjectRequest Req = new JsonObjectRequest(
                Request.Method.POST,
                WS_SendEmail,
                object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            builder.dismiss();
                            Log.e("WS-Response", response.toString());
                            if (response.getString("Response").equals("Success")) {
                                new AlertDFont.Builder(UpdateOfficio.this)
                                        .setMessage("Correo Enviado")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                Intent returnIntent = new Intent();
                                                setResult(Activity.RESULT_OK, returnIntent);
                                                UpdateOfficio.this.finish();
                                            
                                            }
                                        })
                                        .show();
                            } else {
                                // TODO: Guardar localmente
                                new AlertDFont.Builder(UpdateOfficio.this)
                                        .setMessage("Su solicitud no se ha podido realizar con exito.")
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .show();
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
                        Log.e("TAG", "Error Volley: " + error.getCause());
                        new AlertDFont.Builder(UpdateOfficio.this).setMessage(error.getCause().toString()).show();
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
    
    private void UpdateOficio(String Id_Oficio,
                              String Clave_Oficio,
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
        ProgressDialog.Builder builder = new ProgressDialog.Builder(UpdateOfficio.this);
        builder.setMessage("Actualizando Info Oficio...");
        builder.setCancelable(false);
        final android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
        
        HashMap<String, String> map = new HashMap<>();
        map.put("Id_Oficio", StringUtils.unaccent(Id_Oficio));
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
                WS_UpdateOficio,
                object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            alertDialog.dismiss();
                            Log.e("WS-Response", response.toString());
                            if (response.getString("Response").equals("Success")) {
                                new AlertDFont.Builder(UpdateOfficio.this)
                                        .setMessage("Informacion Actualizada").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        Intent returnIntent = new Intent();
                                        setResult(Activity.RESULT_OK, returnIntent);
                                        UpdateOfficio.this.finish();
                                        
                                    }
                                }).show();
                            } else {
                                // TODO: Guardar localmente
                                new AlertDFont.Builder(UpdateOfficio.this)
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
                        new AlertDFont.Builder(UpdateOfficio.this).setMessage(error.getCause().toString()).show();
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
    
    
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++  CAMARA PERMISSION
    private void getcamara() {
        if (ContextCompat.checkSelfPermission(UpdateOfficio.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UpdateOfficio.this, new String[]{
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
            storageDir = (Objects.requireNonNull(UpdateOfficio.this)).getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
    
    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++  CAMARA PERMISSION ENTREGADO
    private void getcamaraEntregada() {
        if (ContextCompat.checkSelfPermission(UpdateOfficio.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UpdateOfficio.this, new String[]{
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
            storageDir = (Objects.requireNonNull(UpdateOfficio.this)).getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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