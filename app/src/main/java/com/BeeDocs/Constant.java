package com.BeeDocs;

public class Constant {
    //TODO: GENERIAL INFOMATION
    public static String SPId_persona="Id_persona";
    public static String SPNombre_persona="Nombre_persona";
    public static String SPApellidoP_persona="ApellidoP_persona";
    public static String SPApellidoM_persona="ApellidoM_persona";
    public static String SPTelefono_persona="Telefono_persona";
    public static String SPCorreo_persona="Correo_persona";
    public static String SPUsuario_persona="Usuario_persona";
    public static String SPContraseña_persona="Contraseña_persona";
    public static String SPToken="Token";
    public static String SPRol="Rol";
    
    public static String SPID_Oficio="IdentifyerOficio";
    public static String SPNomenclaturaOficio="NomenclaturaOficio";
    public static String SPID_Memoran="IdentifyerMemoran";
    public static String SPNomenclaturaMemoran="NomenclaturaMemoran";
    public static String SPID_Circular="IdentifyerCircular";
    public static String SPNomenclaturaCircular="NomenclaturaCircular";
    
    public static String TempDepartamentoNomenclatura="";
    public static String TempDepartamentoId="";
    
    
    //TODO: RETRIEVE INICIAL INFORMATION
//    public static String IP_Address = "192.168.1.66";
    public static String IP_Address = "www.gsirem.com:8888";
    public static String URL_Address = "http://"+IP_Address+"/OficioAssist/";
    public static String Url_Add = "www.gsirem.com:8888/OficioAssist/";
    public static String WS_Register="http://"+IP_Address+"/OficioAssist/RegisterUser.php";
    public static String WS_SignIn="http://"+IP_Address+"/OficioAssist/SignIn.php";
    public static String WS_SelectDepartamento="http://"+IP_Address+"/OficioAssist/SelectDepartamentos.php";
    public static String WS_SelectEstado="http://"+IP_Address+"/OficioAssist/SelectEstado.php";
    public static String WS_SelectAllDepartamentos="http://"+IP_Address+"/OficioAssist/SelectAllDepartamentos.php";
    public static String WS_SubirDepartamento="http://"+IP_Address+"/OficioAssist/SubirDepartamento.php";
    public static String WS_SendEmail="http://"+IP_Address+"/OficioAssist/SendEmail.php";
    
    //TODO: OFICIOS
    public static String WS_SubirOficio="http://"+IP_Address+"/OficioAssist/SubirOficio.php";
    public static String WS_SelectOficio="http://"+IP_Address+"/OficioAssist/SelectOficio.php";
    public static String WS_SelectOficioAll="http://"+IP_Address+"/OficioAssist/SelectOficioAll.php";
    public static String WS_UpdateOficio="http://"+IP_Address+"/OficioAssist/UpdateOficio.php";
    public static String WS_BorrarOficio="http://"+IP_Address+"/OficioAssist/BorrarOficio.php";
    
    //TODO: MEMORAN
    public static String WS_SubirMemoran="http://"+IP_Address+"/OficioAssist/SubirMemoran.php";
    public static String WS_SelectMemoran="http://"+IP_Address+"/OficioAssist/SelectMemoran.php";
    public static String WS_SelectMemoranAll="http://"+IP_Address+"/OficioAssist/SelectMemoranAll.php";
    public static String WS_UpdateMemoran="http://"+IP_Address+"/OficioAssist/UpdateMemoran.php";
    public static String WS_BorrarMemoran="http://"+IP_Address+"/OficioAssist/BorrarMemoran.php";
    
    //TODO: CIRCULAR
    public static String WS_SubirCircular="http://"+IP_Address+"/OficioAssist/SubirCircular.php";
    public static String WS_SelectCircular="http://"+IP_Address+"/OficioAssist/SelectCircular.php";
    public static String WS_SelectCircularAll="http://"+IP_Address+"/OficioAssist/SelectCircularAll.php";
    public static String WS_UpdateCircular="http://"+IP_Address+"/OficioAssist/UpdateCircular.php";
    public static String WS_BorrarCircular="http://"+IP_Address+"/OficioAssist/BorrarCircular.php";
    
    //TODO: ACCOUNT INFO
    public static String WS_SelectInfoPersona="http://"+IP_Address+"/OficioAssist/SelectInfoPersona.php";
    public static String WS_UpdateInfoPersona="http://"+IP_Address+"/OficioAssist/UpdateInfoPersona.php";
    public static String WS_SelectTieneDep="http://"+IP_Address+"/OficioAssist/SelectTieneDep.php";
    public static String WS_GETAccount="http://"+IP_Address+"/OficioAssist/GETAccount.php";
    public static String WS_UpdateAccount="http://"+IP_Address+"/OficioAssist/UpdateAccount.php";
    
    //TODO: ASIGN DEPARTMENTS
    public static String WS_SubirAsignarDepartamento="http://"+IP_Address+"/OficioAssist/SubirAsignarDepartamento.php";
    public static String WS_BorrarAsignarDepartamento="http://"+IP_Address+"/OficioAssist/BorrarAsignarDepartamento.php";
    public static String WS_SelectDepsAsignado="http://"+IP_Address+"/OficioAssist/SelectDepsAsignado.php";
    
    //TODO: ACTIVITY RESULT CODES
    public static Integer UpdateOficio_CODE=001;
    public static Integer AddUser_CODE=002;
    public static Integer Camera_CODE=003;
    public static Integer Camera_CODE_Entregado=004;
    public static Integer UserUpdated_CODE=005;
    public static Integer NewDepartment=006;
    
    //TODO: GENERAL VARIABLES
    public static String Active = "activo",Rol = "fk_id_rol";
    public static Integer NO=0 , YES=1, Worker=1,Supervisor=2,Administrator=3;
    
    
    
}
