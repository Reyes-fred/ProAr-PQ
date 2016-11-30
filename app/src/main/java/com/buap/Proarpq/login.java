package com.buap.Proarpq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class login extends Activity {
    private Button bt;
    private EditText edit;
    private EditText pass;
    Intent pods;
    String idpaciente;
    SQLiteDatabase db2;
    //Creamos el handler puente para mostrar
    //el mensaje recibido del servidor
    private Handler puente = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //Mostramos el mensage recibido del servido en pantalla
            // if(msg.toString().equals("Bienvenido"))
            if(msg.obj.toString().length() > 1){
                pods.putExtra("user",idpaciente);
                startActivity(pods);
                //Log.e("dato", (String)msg.obj);
                Toast.makeText(getApplicationContext(), "Bienvenido "+edit.getText(),
                        Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(getApplicationContext(),"Acceso incorrecto",
                        Toast.LENGTH_LONG).show();
                edit.setText("");
                pass.setText("");
            }

        }
    };

/*    public void registro(View v){
        Intent i = new Intent(this,RegistroActivity.class);
        startActivity(i);
    }*/
    private boolean CreaBase(){
        db2 = openOrCreateDatabase("MyDB",MODE_PRIVATE,null);
        db2.execSQL("CREATE  TABLE IF NOT EXISTS user (iduser VARCHAR(50) NOT NULL, pass VARCHAR(45) ,ds FLOAT NOT NULL , PRIMARY KEY (iduser) )");

        db2.execSQL("CREATE  TABLE IF NOT EXISTS pasos (iduser VARCHAR(50) NOT NULL , fecha DATE NOT NULL ,  pasos INT NOT NULL DEFAULT 0, sync INT NOT NULL DEFAULT 0, PRIMARY KEY (iduser, fecha)) ");

        db2.execSQL("CREATE  TABLE IF NOT EXISTS recordatorio (id INTEGER PRIMARY KEY AUTOINCREMENT, fechaInicio DATE NOT NULL ," +
                "fechaFinal DATE NOT NULL , horaInicio VARCHAR(45) NOT NULL, horaFin VARCHAR(45) NOT NULL, mensaje VARCHAR(200))");

        db2.execSQL("CREATE  TABLE IF NOT EXISTS usuario (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre VARCHAR(100) NOT NULL ,apellido VARCHAR(100) NOT NULL ," +
                "sexo VARCHAR(20) NOT NULL ,fechaNac VARCHAR(50) NOT NULL ,email VARCHAR(45) NOT NULL , usuario VARCHAR(45) NOT NULL )");
        db2.close();
        return true;
    }

    public boolean verifica(){

        db2 = openOrCreateDatabase("MyDB",MODE_PRIVATE,null);
        Cursor c = db2.rawQuery("SELECT * FROM user", null);
        c.moveToFirst();
        db2.close();
        if(c.getCount()==0)
            return true;
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);



        //verificamos si ya se ha logeado antes para q no lo repita y mandar directamente a la pantalla de pasos
        CreaBase();
        if(verifica()){
            pods  = new Intent(this, calibrar.class);

            //this.finish();
        }else{
            Intent Main =  new Intent(this, MainActivity.class);
            startActivity(Main);
        }


        //Relacionamos con XML como ya sabemos
        bt = (Button)findViewById(R.id.Entrar);
        edit = (EditText)findViewById(R.id.user);
        pass = (EditText)findViewById(R.id.pass);
        //Añadimos el Listener
        bt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                //Hay que hacerlo dentro del Thread
                //No me dejaba tocar la Clase de Network
                //directamente en el hilo principal
                Thread thr = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //Enviamos el texto escrito a la funcion
                        validar();
                        //  Log.d("entro", edit.getText().toString()+"-----"+pass.getText().toString());
                    }
                });
                //Arrancamos el Hilo
                thr.start();
            }
        });
    }

    public void validar() {
        Message sms = new Message();

        if (!edit.getText().toString().equals("") && !pass.getText().toString().equals("")) {
            if (getServerData(edit.getText().toString(), pass.getText().toString())) {
                sms.obj = "entro";
                puente.sendMessage(sms);
            } else {
                sms.obj = "";
                puente.sendMessage(sms);
            }
        }


    }
    public void actualizar(String nombre,String apaterno,String fechanac,String sexo,String correo){
        db2 = openOrCreateDatabase("MyDB",MODE_PRIVATE,null);
        db2.execSQL("INSERT INTO usuario (nombre,apellido,sexo,fechaNac,email,usuario)" +
                " VALUES ('"+nombre+"','"+apaterno+"','"+sexo+"','"+fechanac+"','"+correo+"','"+edit.getText()+"');");
        db2.close();
        Log.d("inserto", "");
    }
    protected void onPause(){
        super.onPause();
        finish();
    }

    private boolean getServerData(String user, String pass) {

        InputStream is = null;

        String result = "";

        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("username", user));
        nameValuePairs.add(new BasicNameValuePair("password", pass));

        //http post
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://mets.itisol.com.mx/login/loginMovil");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();


        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
        }


        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }
        //parse json data
        try {
            org.json.JSONObject jsonO = new org.json.JSONObject(result);

            Log.e("log_tag", "Resultado " + jsonO.toString());
            if (("0").equals(jsonO.get("respuesta").toString())) {
                return false;
            } else {
                actualizar(jsonO.get("nombre").toString(), jsonO.get("apellido").toString(),jsonO.get("fechaNac").toString(),
                      jsonO.get("sexo").toString(),jsonO.get("correo").toString());
                idpaciente = jsonO.get("idpaciente").toString();
                return true;
            }

        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
        return false;
    }


}