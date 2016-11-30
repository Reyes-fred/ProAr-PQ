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

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

public class envio extends Activity {
    private Button bt;
    private EditText edit;

    SQLiteDatabase db2; //= openOrCreateDatabase("MyDB",MODE_PRIVATE,null);;

    //Creamos el handler puente para mostrar
//el mensaje recibido del servidor
    private Handler puente = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //Mostramos el mensage recibido del servido en pantalla
            Log.d("Guardado en el servidor",""+msg.obj.toString());
            if(msg.obj.toString().length() > 1){

                Log.d("VALOR DEL SERVER",""+(String)msg.obj);

            }else{

            }

        }
    };

    String pasos="",fecha2="",user2="";



    public String cambiaFecha(String fecha){
        String aux="";
        String[] a = fecha.split("-");

        aux=""+a[2]+"-"+a[1]+"-"+a[0];

        return aux;
    }



    public void actualizar(final String pasos,final String fecha1,final String user){

        user2 = user;
        fecha2=fecha1;
        //Hay que hacerlo dentro del Thread
        //No me dejaba tocar la Clase de Network
        //directamente en el hilo principal
        Thread thr = new Thread(new Runnable() {
            @Override
            public void run() {

                //Enviamos el texto escrito a la funcion
                EnviarDatos(user,fecha1,pasos);
            }
        });
        //Arrancamos el Hilo
        thr.start();

    }



    private void EnviarDatos(String user, String fecha, String pasos){
        //Utilizamos la clase Httpclient para conectar

        HttpClient httpclient = new DefaultHttpClient();
        //Utilizamos la HttpPost para enviar lso datos
        //A la url donde se encuentre nuestro archivo receptor




        HttpPost httppost2 = new HttpPost("http://mets.itisol.com.mx/paciente/insertaActividad");
        try {
            //Añadimos los datos a enviar en este caso solo uno
            //que le llamamos de nombre 'a'
            //La segunda linea podría repetirse tantas veces como queramos
            //siempre cambiando el nombre ('a')
            List<NameValuePair> postValues = new ArrayList<NameValuePair>(2);
            postValues.add(new BasicNameValuePair("usuario", user));

            postValues.add(new BasicNameValuePair("fecha", fecha));
            Log.d("la fecha es", "" + cambiaFecha(fecha));
            postValues.add(new BasicNameValuePair("pasos", pasos));

           // postValues.add(new BasicNameValuePair("tipo", "1"));

            //Encapsulamos
            httppost2.setEntity(new UrlEncodedFormEntity(postValues));
            //Lanzamos la petición
            HttpResponse respuesta2 = httpclient.execute(httppost2);
            //Conectamos para recibir datos de respuesta
            HttpEntity entity2 = respuesta2.getEntity();
            //Creamos el InputStream como su propio nombre indica
            InputStream is2 = entity2.getContent();
            //Limpiamos el codigo obtenido atraves de la funcion
            //StreamToString explicada más abajo
            String resultado2= StreamToString(is2);

            //Enviamos el resultado LIMPIO al Handler para mostrarlo
            Message sms2 = new Message();
            sms2.obj = resultado2;

            puente.sendMessage(sms2);
        }catch (IOException e) {
            //TODO Auto-generated catch block
        }

    }






    //Funcion para 'limpiar' el codigo recibido
    public String StreamToString(InputStream is) {
        //Creamos el Buffer
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            //Bucle para leer todas las líneas
            //En este ejemplo al ser solo 1 la respuesta
            //Pues no haría falta
            while((line = reader.readLine())!=null){
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //retornamos el codigo límpio
        return sb.toString();
    }
}