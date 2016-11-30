package com.buap.Proarpq;

/**
 * Created by Alfredo on 11/05/2015.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

@SuppressLint("ShowToast")
public class calibrar extends Activity implements OnClickListener, SensorEventListener {
   boolean inicio = false;
    Intent pods;
    String user;
    PowerManager.WakeLock mWakeLock;
    MediaPlayer mensajemp = null, iniciomp = null, errormp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_inicio);


        mensajemp = MediaPlayer.create(this, R.raw.mensaje);
        iniciomp = MediaPlayer.create(this, R.raw.inicio);


        final View Button = findViewById(R.id.boton1);
        final View Button2 = findViewById(R.id.detener);
        ((TextView) findViewById(R.id.te)).setText("");

        Button2.setEnabled(false);
        Button2.setVisibility(View.INVISIBLE);
        pods = new Intent(this, MainActivity.class);

        user = getIntent().getStringExtra("user");

        Log.d("" + user, "");

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "on");
        mWakeLock.acquire();



        Button.setOnClickListener(new View.OnClickListener() {


            public void onClick(View arg0) {


                Button.setEnabled(false);
                Button.setVisibility(View.INVISIBLE);

                mensajemp.start();


                @SuppressWarnings("unused")
                CountDownTimer timer = new CountDownTimer(10000, 1000) {
                    public void onTick(long millisUntilFinished) {

                        ((TextView) findViewById(R.id.te)).setText("Iniciara en: " + millisUntilFinished / 1000 + " seg");
                    }

                    @Override
                    public void onFinish() {

                        iniciomp.start();

                        @SuppressWarnings("static-access")
                        Vibrator v = (Vibrator) getSystemService(getApplicationContext().VIBRATOR_SERVICE);
                        v.vibrate(300);
                        Button2.setEnabled(true);
                        Button2.setVisibility(View.VISIBLE);
                        Button.setEnabled(false);
                        Button.setVisibility(View.INVISIBLE);
                        inicia = true;
                        inicio = true;

                    }
                }.start();

            }
        });

        Button2.setOnClickListener(new View.OnClickListener() {


            public void onClick(View arg0) {
                Button2.setEnabled(false);
                Button2.setVisibility(View.INVISIBLE);
                inicia = false;

            }

        });


    }

    int seg=10;
    boolean calibrado = false;

    protected void onResume() {
        super.onResume();
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensors.size() > 0) {
            sm.registerListener(this, sensors.get(0), SensorManager.SENSOR_DELAY_FASTEST);
        }

    }

    protected void onStop() {
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        sm.unregisterListener(this);
        if (iniciomp != null) {
            iniciomp.release();
            iniciomp = null;
        }
        if (mensajemp != null) {
            mensajemp.release();
            mensajemp = null;
        }

        if (errormp != null) {
            errormp.release();
            errormp = null;
        }
        super.onStop();
    }

    boolean inicia = false;

    public void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    final float alpha = (float) 0.8;
    float[] gravity = new float[3];
    float k = (float) 0.35;
    float tx = 0,ty = 0,tz = 0;

    public float mediaMovilx(float V) {

        return tx = (float) (k * tx + (1.0 - k) * V);

    }

    public float mediaMovily(float V) {

        return ty = (float) (k * ty + (1.0 - k) * V);

    }

    public float mediaMovilz(float V) {

        return tz = (float) (k * tz + (1.0 - k) * V);

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

    public float[] suma(float[] A, float[] B, int in, int f) {
        float[] Aux = new float[1200];
        for (int i = in; i < f; i++) {
            if (in > 1199) {
                Aux[i - 1200] = A[i] + B[i];
            } else {
                Aux[i] = A[i] + B[i];
            }
        }

        return Aux;
    }

    public float media(float[] A, float[] B, int in, int f) {
        float media = 0;
        for (int i = in; i < f; i++) {
            media += A[i];
            media += B[i];
        }

        return media / 1200;
    }

    public float DS(float[] A, float[] B, int in, int f, float media) {
        float ds = 0;
        for (int i = in; i < f; i++) {
            ds += Math.pow(((A[i] + B[i]) - media), 2);

        }

        ds = ds / 1199;

        return (float) Math.sqrt(ds);
    }

    public int flatp(float[] A, float[] B, int in, int f, float ds) {
        boolean ban = false;


        int cont = 0;
        float aux = 0;
        if (in < 1199) {
            for (int i = in; i < f; i++) {
                aux = A[i] + B[i];
                if (aux >= ds) {
                    ban = true;
                }
                if (ban == true && aux <= -ds) {
                    cont++;
                    ban = false;
                }
            }
        } else {
            for (int i = in; i < f; i++) {
                aux = A[i - 1199] + B[i - 1199];
                if (aux >= ds) {
                    ban = true;
                }
                if (ban == true && aux <= -ds) {
                    cont++;
                    ban = false;
                }
            }
        }

        return cont;
    }

    private float X = 0, Y = 0, Z = 0;
    float []xy = new float[1200];
    int pasos =0;
    boolean b2=false;
    float dsG;
    Bundle bundle = new Bundle();

    @Override
    public void onSensorChanged(SensorEvent event) {

        synchronized (this) {
            if (inicia == true) {
                gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
                gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
                gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

                X = event.values[0] - gravity[0];
                Y = event.values[1] - gravity[1];
                Z = event.values[2] - gravity[2];

                X = mediaMovilx(X);
                Y = mediaMovily(Y);
                Z = mediaMovilz(Z);

                fx[u] = X;
                fy[u] = Y;
                u++;
            } else {
                if (inicio == true) {

                    mediax = Math.round(media(fx, fy, 0, u - 115));

                    dsG = Math.round(DS(fx, fy, 0, u - 115, mediax));

                    dsx = dsG / 10;

                    pasos = flatp(fx, fy, 0, u - 115, dsx);
                    ((TextView) findViewById(R.id.te)).setText("Pasos:" + pasos);
                    inicio = false;
                    if (pasos >= 8 && pasos <= 10) {
                        actualizar(dsx);
                        Toast.makeText(getApplicationContext(), "Calibración Exitosa", Toast.LENGTH_LONG).show();
                        startActivity(pods);
                        this.finish();
                    } else {

                        b2 = true;
                    }

                } else {
                    if (b2 == true) {
                        if (p < 100) {
                            p++;
                            dsx += dsG / 10;
                            pasos = flatp(fx, fy, 0, u - 115, dsx);
                            ((TextView) findViewById(R.id.te)).setText("Pasos:" + pasos);

                            if (pasos > 10) {

                            } else {
                                //Toast.makeText(this, ""+pasos+"--"+dsx, 1);
                                //actualizar(dsx);
                                //pods.putExtra("ds", dsx);
                                actualizar(dsx);
                                b2 = false;
                                startActivity(pods);
                                this.finish();
                            }
                        } else {
                            b2 = false;
                            errormp = MediaPlayer.create(this, R.raw.error);
                            errormp.start();
                            Intent intent= new Intent(this,calibrar.class);
                            startActivity(intent);
                            this.finish();

                        }

                    }

                }

            }


        }
    }
int p = 0;
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

    }
    SQLiteDatabase db2;
    public boolean verifica() {

        db2 = openOrCreateDatabase("MyDB", MODE_PRIVATE, null);
        Cursor c = db2.rawQuery("SELECT * FROM pasos", null);
        c.moveToFirst();
        db2.close();
        if (c.getCount() == 0)
            return true;
        return false;
    }

    public boolean verifica2() {

        db2 = openOrCreateDatabase("MyDB", MODE_PRIVATE, null);
        Cursor c = db2.rawQuery("SELECT * FROM user", null);
        c.moveToFirst();
        db2.close();
        if (c.getCount() == 0)
            return true;
        return false;
    }


    public void actualizar(float x) {
        Log.d("entro", "2");
        db2 = openOrCreateDatabase("MyDB", MODE_PRIVATE, null);
        Log.d("creo", "" + x);
        //if(verifica2()){

        db2.execSQL("INSERT INTO user VALUES ('" + user + "','pass'," + x + ");");


			/*}else{

			db2.execSQL("UPDATE user SET ds="+x+" WHERE iduser=1;");

			}*/
        Log.d("inserto", "");
    }


}
