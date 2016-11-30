package com.buap.Proarpq;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.os.PowerManager;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PodometroFragment extends Fragment implements SensorEventListener {
    FragmentManager mFragmentManager;
    SQLiteDatabase db2;
    float d ;
    private float X = 0, Y = 0, Z = 0  ;
    int pasos=0;
    public int p;
    CheckBox _3g;
    boolean red=true;
    PowerManager.WakeLock wakeLock;
    float km=0;
    float vel=0;
    Intent i;
    ProgressBar pbpasos,pbdistancia,pbvelocidad;
    View v;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       v = inflater.inflate(R.layout.activity_podometro,null);
        MainActivity.toolbar.getMenu().clear();
       /* try {
            setMobileDataEnabled(v.getContext(), true);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }*/
        pbpasos = (ProgressBar)v.findViewById (R.id.pbpasos);
        pbdistancia = (ProgressBar)v.findViewById (R.id.pbdistancia);
        pbvelocidad = (ProgressBar)v.findViewById (R.id.pbvelocidad);

        Date now = new Date();
        DateFormat df3 = DateFormat.getDateInstance(DateFormat.LONG);
        String s3 = df3.format(now);
        ((TextView) v.findViewById(R.id.fecha)).setText(s3);

        i = new Intent(v.getContext(), graficas.class);

        pbpasos.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                i.putExtra("bandera", "0");
                startActivity(i);

            }
        });

        pbdistancia.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                i.putExtra("bandera", "1");
                startActivity(i);

            }
        });


        /**evitar que la pantalla se bloque */

        PowerManager pm = (PowerManager) v.getContext().getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
                "My wakelook");
        wakeLock.acquire();
	    /*Toast acquire = Toast.makeText(getApplicationContext(), "Wake Lock ON",
	      Toast.LENGTH_SHORT);
	    acquire.show();*/


        //Log.d("entro","1");
        //CreaBase();
        if(verifica()){
            Intent a = new Intent(v.getContext(), calibrar.class);
            Log.d("no se ha calibrado", "");
            startActivity(a);
            //	this.finish();
        }else{
            d = recuperads();


            if(verificapasos()){
                Log.d("sin registros en el dia", "2");
                inserta(0);
            }else{
                Log.d("ya existen pasos", "3");
                pasos=recuperapasos();
                if (pasos>0) {
                    calculadis();
                }
            }
            //d = getIntent().getFloatExtra("ds",dsx);
            //Log.d("umbral", ""+d);
            recuperas();
        }
        _3g = (CheckBox)v.findViewById(R.id._3g);
       // _3g.setVisibility(View.INVISIBLE);
        _3g.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.d("activa 3g","");
                    red=true;
                }
                else {
                    Log.d("desactiva 3g","");
                    red=false;

                }
            }
        });
         mFragmentManager = getActivity().getSupportFragmentManager();
        v.findViewById(R.id.mensaje).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                xfragmentTransaction.replace(R.id.containerView,new mensaje()).commit();


            }
        });

        return v;
    }

    public void onResume(){
        super.onResume();
        SensorManager sm = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ACCELEROMETER);

        if (sensors.size() > 0) {
            sm.registerListener(this, sensors.get(0), SensorManager.SENSOR_DELAY_GAME);
        }

    }


    public void onStop() {
        SensorManager sm = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        //sm.unregisterListener(this);
        super.onStop();
    }



    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub

    }
    public  boolean boll  = false;

    final float alpha = (float) 0.8;
    float[] gravity = new float[3];
    float k = (float) 0.35;
    float tx = 0,ty = 0,tz = 0;


    public float mediaMovilx(float V){

        return  tx=(float) (k * tx + (1.0 - k) * V);

    }


    public float mediaMovily(float V){

        return  ty=(float) (k * ty + (1.0 - k) * V);

    }

    public float mediaMovilz(float V){

        return  tz=(float) (k * tz + (1.0 - k) * V);

    }

    //Sensor sensor = mSensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

    int w= 0,u=0;
    float[] fx = new float[2401];
    float[] fy = new float[2401];
    //float[] fz = new float[2401];


    long now = 0;
    long time = 0;
    int temp = 0;

    float mediax=0;
    float mediay=0;

    float dsx=0,dsy=0;
    float t=0;
    public float[] suma(float []A ,float []B,int in ,int f){
        float[] Aux = new float[1200];
        for(int i=in; i<f; i++){
            if(in>1199){
                Aux[i-1200]= A[i]+B[i];
            }else{
                Aux[i]= A[i]+B[i];
            }
        }

        return Aux;
    }

    public float media(float []A ,float []B,int in ,int f){
        float media=0;
        for(int i=in; i<f; i++){
            media+= A[i];
            media+= B[i];
        }

        return media/1200;
    }

    public float DS(float []A ,float []B,int in ,int f,float media){
        float ds=0;
        for(int i=in; i<f; i++){
            ds += Math.pow(((A[i]+B[i])-media),2);

        }

        ds= ds/1199;

        return (float) Math.sqrt(ds);
    }

    public int flatp(float []A,float []B, int in, int f, float ds){
        boolean ban= false;


        int cont=0;
        float aux =0;
        if(in<1199){
            for(int i=in; i<f; i++){
                aux=A[i]+B[i];
                if(aux>= ds){

                    ban=true;
                }
                if(ban == true && aux<= -ds ){

                    cont++;
                    ban=false;
                }
            }
        }else{
            for(int i=in; i<f; i++){
                aux=A[i-1199]+B[i-1199];
                if(aux>= ds){

                    ban=true;
                }
                if(ban == true && aux<= -ds ){

                    cont++;
                    ban=false;
                }
            }
        }

        if(cont > 3){

            t += (time_end - time_start)/1000;

        }

        return cont;
    }

    /*private boolean CreaBase(){
		db2 = openOrCreateDatabase("MyDB",MODE_PRIVATE,null);
		db2.execSQL("CREATE  TABLE IF NOT EXISTS user (iduser INT NOT NULL, pass VARCAHR(45) ,ds FLOAT NOT NULL , PRIMARY KEY (iduser) )");
		db2.execSQL("CREATE  TABLE IF NOT EXISTS pasos (iduser INT NOT NULL , fecha DATE NOT NULL ,  pasos INT NOT NULL DEFAULT 0 , PRIMARY KEY (iduser, fecha)) ");
		db2.close();
	return true;
	}/*/

    envio n = new envio();
    int suma = 10;
    public synchronized void  actualiza(int pas){
        String pasos,fecha,user;
        db2 = v.getContext().openOrCreateDatabase("MyDB", Context.MODE_PRIVATE,null);
        db2.execSQL("UPDATE pasos SET pasos="+pas+",sync=0 WHERE fecha='"+getD()+"';");

        if(suma>=0){
            Log.d("verificando sync", "");
            if(estadoRedes()){
                suma=10;

                Cursor c = db2.rawQuery("SELECT * FROM pasos where sync=0", null);
                c.moveToFirst();

                do{
                    pasos = c.getString(c.getColumnIndex("pasos"));
                    fecha = c.getString(c.getColumnIndex("fecha"));
                    user  = c.getString(c.getColumnIndex("iduser"));
                    Log.d("Subiendo al Server",pasos+"	--- "+fecha);

                    Log.e("El valor es: ",""+checarhora());

                    n.actualizar(pasos, fecha, user);

                    db2.execSQL("UPDATE pasos SET sync=1 WHERE fecha='"+getD()+"';");
                }while(c.moveToNext());
                //n.r();
                //n.actualizar(getD());
            }

        }else{
            suma+=24;
        }
        db2.close();
    }

    boolean checarhora(){
        int from = 2200;
        int to = 2359;
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int t = c.get(Calendar.HOUR_OF_DAY) * 100 + c.get(Calendar.MINUTE);
        boolean isBetween = to > from && t >= from && t <= to || to < from && (t >= from || t <= to);
        return isBetween;
    }

    String fech = getD();
    public void inserta(int pas){
        String x="";

        db2 = getActivity().openOrCreateDatabase("MyDB", android.content.Context.MODE_PRIVATE, null);
        Cursor c = db2.rawQuery("SELECT * FROM user", null);
        c.moveToFirst();

        x=c.getString(c.getColumnIndex("iduser"));

        db2.execSQL("INSERT INTO pasos VALUES('"+x+"','"+fech+"',0,0);");
        db2.close();

        Log.d("insertó", fech+"  "+x);
    }



    private String getD()
    {
        // SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fecha = dateFormat.format(Calendar.getInstance().getTime());
        Log.d("fecha", fecha);

        return fecha;
    }



    public boolean verifica(){

        db2 = getActivity().openOrCreateDatabase("MyDB", android.content.Context.MODE_PRIVATE, null);
        Cursor c = db2.rawQuery("SELECT * FROM user", null);
        c.moveToFirst();
        db2.close();
        if(c.getCount()==0)
            return true;
        return false;
    }


    public boolean verificapasos(){
        Log.d("verifica", "");
        db2 = getActivity().openOrCreateDatabase("MyDB", android.content.Context.MODE_PRIVATE, null);
        Cursor c = db2.rawQuery("SELECT * FROM pasos WHERE fecha='"+getD()+"';", null);
        c.moveToFirst();
        db2.close();
        if(c.getCount()==0)
            return true;
        return false;
    }

    public void recuperas(){
        String x="",y="";
        db2 = getActivity().openOrCreateDatabase("MyDB", android.content.Context.MODE_PRIVATE, null);
        Cursor c = db2.rawQuery("SELECT * FROM pasos", null);
        c.moveToFirst();
        db2.close();
        do{
            x=c.getString(c.getColumnIndex("pasos"));
            y=c.getString(c.getColumnIndex("fecha"));
            Log.d("datos de el dia", x+"---"+y);
        }while(c.moveToNext());


    }



    public float recuperads(){
        String x="";
        db2 = getActivity().openOrCreateDatabase("MyDB", android.content.Context.MODE_PRIVATE, null);
        Cursor c = db2.rawQuery("SELECT * FROM user", null);
        c.moveToFirst();
        db2.close();
        do{
            x=c.getString(c.getColumnIndex("ds"));
        }while(c.moveToNext());

        return Float.parseFloat(x);
    }

    public int recuperapasos(){
        String x="";
        db2 = getActivity().openOrCreateDatabase("MyDB",android.content.Context.MODE_PRIVATE,null);
        Cursor c = db2.rawQuery("SELECT * FROM pasos WHERE fecha='"+getD()+"';", null);
        c.moveToFirst();
        db2.close();
        do{
            x=c.getString(c.getColumnIndex("pasos"));
        }while(c.moveToNext());

        return Integer.parseInt(x);
    }

    public void calculadis(){
        km = (float) (pasos*10/500);
        Log.d("km",""+km);
        km = (float) (km/40);
        Log.d("km",""+km);
    }

    public void calculavel(float times){
        Log.d("time",""+times);
        vel=(km*100)/(times);
        Log.d("vel",""+vel);
        vel=(float) (vel*3.6) ;
        Log.d("vel",""+vel);
        vel = (float) Math.rint(vel*100)/100;
        Log.d("vel",""+vel);
    }


    float []xy = new float[1200];
    long time_start, time_end;


    @Override
    public void onSensorChanged(SensorEvent event) {

        synchronized (this) {
            //long current_time = event.timestamp;



            ///	((TextView) this.findViewById(R.id.axex)).setText("X : " + x);
            //((TextView) this.findViewById(R.id.axey)).setText("Y : " + y);
            //((TextView) this.findViewById(R.id.axez)).setText("Z : " + z);

            // Get the mean frequency for "nbElements" (=30) elements
			/*if (now != 0) {
				temp++;
				if (temp == nbElements) {

		        		*/
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            X = event.values[0] - gravity[0];
            Y = event.values[1] - gravity[1];
            Z = event.values[2] - gravity[2];

            X = mediaMovilx(X);
            Y = mediaMovily(Y);
            Z = mediaMovilz(Z);

            fx[u]= X;
            fy[u]= Y;
            if (u==0){
                time_start = System.currentTimeMillis();
            }
            u++;
            //Log.d("u"+u, "");
            //n.actualizar2(""+(X+Y), ""+d);

            if(u==1200 ){
                time_end = System.currentTimeMillis();
                pasos+= flatp(fx,fy,0,1199,d);
                actualiza(pasos);
                calculadis();
                calculavel(t);
                time_start = System.currentTimeMillis();
            }
            if(u == 2400){
                //procesa 1200 a 2399
                time_end = System.currentTimeMillis();
                u=0;
                pasos+= flatp(fx,fy,1200,2399,d);
                actualiza(pasos);
                calculadis();
                calculavel(t);
            }

				/*	time = tS - now;
					temp = 0;

					fx[u] = X;
					fy[u] = Y;
					//fz[u] = Z;

					u++;
					if(u > 24 && u<26){
						//prosesa 0 - 24
					}
					if(u > 48){
							u=0;
							//procesa 25 - 48

					}
				}
			}
			// To set up now on the first event and do not change it while we do not have "nbElements" events
			if (temp == 0) {
				now = tS;
			}
			*/

            if(aux!=pasos){
                Log.d("pasos", ""+pasos);
                aux=pasos;
                ((TextView) v.findViewById(R.id.valor2)).setText("" + pasos);

                int porcentaje=(100*pasos)/10000;
                pbpasos.setProgress(0);
                pbpasos.setProgress(porcentaje);

            }

            if(aux2!=vel){
                Log.d("vel", ""+vel);
                aux2=vel;
                ((TextView) v.findViewById(R.id.vel0)).setText("" + vel);

                float porcentaje=(100*vel)/20;
                pbvelocidad.setProgress(0);
                pbvelocidad.setProgress(Math.round(porcentaje));

            }

            if(aux3!=km){
                Log.d("km", ""+km);
                aux3=km;
                ((TextView) v.findViewById(R.id.dis0)).setText("" + km);

                float porcentaje=(100*km)/8;
                pbdistancia.setProgress(0);
                pbdistancia.setProgress(Math.round(porcentaje));

            }
        }

    }
    int aux=0;
    float aux2=0,aux3=0;

    public synchronized boolean estadoRedes() {
        ConnectivityManager connMgr = (ConnectivityManager) v.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifi.isAvailable()) {
            return true;
        }else{
            if( mobile.isAvailable() && red){
                return true;
            }
            return false;
        }
    }

    private void setMobileDataEnabled(Context context, boolean enabled) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        final ConnectivityManager conman = (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final Class conmanClass = Class.forName(conman.getClass().getName());
        final Field connectivityManagerField = conmanClass.getDeclaredField("mService");
        connectivityManagerField.setAccessible(true);
        final Object connectivityManager = connectivityManagerField.get(conman);
        final Class connectivityManagerClass =  Class.forName(connectivityManager.getClass().getName());
        final Method setMobileDataEnabledMethod = connectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
        setMobileDataEnabledMethod.setAccessible(true);

        setMobileDataEnabledMethod.invoke(connectivityManager, enabled);
    }

}
