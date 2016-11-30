package com.buap.Proarpq;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TabHost;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class graficas extends Activity {
    TextView text,text2;
    int ban=0;
    int aux;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_graficas);
        String datas="";

        Bundle extras = getIntent().getExtras();
        text = (TextView) findViewById(R.id.titulo);
        text2 = (TextView) findViewById(R.id.titulo2);
        if (extras != null) {
            datas= extras.getString("bandera");

            if (datas!= null) {
                if(datas.equals("0")){
                text.setText("Pasos");
                    text2.setText("Pasos");
                }
                else{
                    text.setText("Distancia (km)");
                    text2.setText("Distancia (km)");
                    ban=1;
                }
            }
        }



        BarChart chart = (BarChart) findViewById(R.id.chart);

        BarData data = new BarData(getXAxisValues(), getDataSet());
        chart.setData(data);
        chart.setDescription("");
        chart.animateXY(2000, 2000);
        chart.invalidate();

        BarChart chart2 = (BarChart) findViewById(R.id.chart2);

        BarData data2 = new BarData(getXAxisValues2(), getDataSet2());
        chart2.setData(data2);
        chart2.setDescription("");
        chart2.animateXY(2000, 2000);
        chart2.zoomIn();
        chart2.invalidate();

        Resources res = getResources();

        TabHost tabs=(TabHost)findViewById(android.R.id.tabhost);
        tabs.setup();

        TabHost.TabSpec spec=tabs.newTabSpec("mitab1");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Dia",
                res.getDrawable(android.R.drawable.ic_btn_speak_now));
        tabs.addTab(spec);

        spec=tabs.newTabSpec("mitab2");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Mes",
                res.getDrawable(android.R.drawable.ic_dialog_map));
        tabs.addTab(spec);

        tabs.setCurrentTab(0);

    }

    @SuppressLint("SimpleDateFormat")
    private String getDnormal()
    {
        //SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        String fecha = dateFormat.format(cal.getTime());

        Log.d("fecha :", fecha);

        return fecha;

    }
    @SuppressLint("SimpleDateFormat")
    private String getDmenos()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE,-7);
        String fecha = dateFormat.format(cal.getTime());

        Log.d("fecha menos 7 dias:", fecha);

        return fecha;

    }


    private ArrayList<BarDataSet> getDataSet() {
        ArrayList<BarDataSet> dataSets = null;
        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        String x="";
        SQLiteDatabase db2 = openOrCreateDatabase("MyDB",MODE_PRIVATE,null);
        Cursor c = db2.rawQuery("SELECT * FROM pasos WHERE fecha BETWEEN '" + getDmenos() + "' AND '" + getDnormal() + "';", null);
        //Cursor c2 = db2.rawQuery("SELECT * FROM pasos WHERE fecha = '"+getDnormal()+"';", null);
        c.moveToFirst();
        //c2.moveToFirst();
        Log.d("Numero de registros", String.valueOf(c.getCount()));
        //Log.d("Numero de registros hoy", String.valueOf(c2.getCount()));
        db2.close();
        int i=0;
        Double total;
        if (c!= null && c.getCount() > 0){
            do{
                if(ban==0){
                x=c.getString(c.getColumnIndex("pasos"));
                BarEntry v1e1 = new BarEntry(Float.parseFloat(x), i); // Jan
                valueSet1.add(v1e1);}
                else{
                x=c.getString(c.getColumnIndex("pasos"));
                    total=(double)Integer.parseInt(x);
                    total=total/2000;
                    BarEntry v1e1 = new BarEntry(Float.parseFloat(String.valueOf(total)), i); // Jan
                    valueSet1.add(v1e1);
                }

                i++;
            }while(c.moveToNext());}
        Log.e("Total:",String.valueOf(valueSet1.size()));
        Log.e("Total:",String.valueOf(7-valueSet1.size()));
        aux=valueSet1.size();

        for(int y=aux;y<7;y++){
            BarEntry v1e1 = new BarEntry(0, aux); // Jan
            valueSet1.add(v1e1);
            aux++;
        }
        aux=valueSet1.size();

        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Pasos realizados por día");
        barDataSet1.setColor(Color.rgb(18,180,216));


        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);

        return dataSets;
    }


    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();
        String x="";
        SQLiteDatabase db2 = openOrCreateDatabase("MyDB",MODE_PRIVATE,null);
        Cursor c = db2.rawQuery("SELECT * FROM pasos WHERE fecha BETWEEN '"+getDmenos()+"' AND '"+getDnormal()+"';", null);
        //Cursor c = db2.rawQuery("SELECT * FROM pasos WHERE fecha = '"+getD()+"';", null);
        c.moveToFirst();

        Log.d("Numero de registros 2", String.valueOf(c.getCount()));
        db2.close();
        int i=0;
        if (c!= null && c.getCount() > 0){
            do{

                x=c.getString(c.getColumnIndex("fecha"));
                xAxis.add(x);
                i++;
                }while(c.moveToNext());}

        for(int y=aux;y<7;y++){
           xAxis.add("");
        }

        return xAxis;
    }



    private ArrayList<BarDataSet> getDataSet2() {
        ArrayList<BarDataSet> dataSets = null;
        ArrayList<BarEntry> valueSet1 = new ArrayList<>();
        String x = "";
        int i = 0;
        for(int j=1;j<=12;j++)
        {
        SQLiteDatabase db2 = openOrCreateDatabase("MyDB", MODE_PRIVATE, null);
        Cursor c = db2.rawQuery("SELECT * FROM pasos WHERE strftime('%m', `fecha`) = '"+String.format("%02d", j)+"'", null);
        //Cursor c2 = db2.rawQuery("SELECT * FROM pasos WHERE strftime('%m', `fecha`) = '08'", null);
        c.moveToFirst();
        //c2.moveToFirst();
        Log.d("Numero de registros", String.valueOf(c.getCount()));
        //Log.d("Numero de registros hoy", String.valueOf(c2.getCount()));
        db2.close();
        float aux = 0;
        if (c != null && c.getCount() > 0) {
            do {
                x = c.getString(c.getColumnIndex("pasos"));
                aux += Float.parseFloat(x);

            } while (c.moveToNext());
        }
        BarEntry v1e1 = new BarEntry(aux, i);
        valueSet1.add(v1e1);
            i++;

    }


        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Pasos realizados por día");
        barDataSet1.setColor(Color.rgb(18,180,216));


        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);

        return dataSets;
    }



    private ArrayList<String> getXAxisValues2(){
        ArrayList<String> xAxis = new ArrayList<>();
        xAxis.add("Enero");
        xAxis.add("Febrero");
        xAxis.add("Marzo");
        xAxis.add("Abril");
        xAxis.add("Mayo");
        xAxis.add("Junio");
        xAxis.add("Julio");
        xAxis.add("Agosto");
        xAxis.add("Septiembre");
        xAxis.add("Octubre");
        xAxis.add("Noviembre");
        xAxis.add("Diciembre");

        return xAxis;
    }
}


