package com.buap.Proarpq;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wdullaer.materialdatetimepicker.time.Timepoint;

import java.util.Calendar;

public class nuevorecordatorio extends AppCompatActivity {


    private int fechaini[]={-8,-8,-8},fechafin[]={-8,-8,-8},horaini[]={-8,-8,-8},horafin[]={-8,-8,-8};

    private Button bt;
    private EditText texto;
    SQLiteDatabase db2;
    Button timeButton,dateButton,timeButton2,dateButton2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevorecordatorio);


        timeButton = (Button)findViewById(R.id.time_button);
        dateButton = (Button)findViewById(R.id.date_button);
        timeButton2 = (Button)findViewById(R.id.time_button2);
        dateButton2 = (Button)findViewById(R.id.date_button2);

        bt = (Button)findViewById(R.id.guardar);
        texto = (EditText)findViewById(R.id.mensaje);


        // Show a timepicker when the timeButton is clicked
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar now = Calendar.getInstance();
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
                                String hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
                                String minuteString = minute < 10 ? "0"+minute : ""+minute;
                                String secondString = second < 10 ? "0"+second : ""+second;
                                String time = hourString+"h"+minuteString+"m"+secondString+"s";
                               // now.get(Calendar.HOUR_OF_DAY);
                                horaini[0]=hourOfDay;
                                horaini[1]=minute;
                                horaini[2]=second;
                                timeButton.setText(time);

                            }
                        },
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        false
                );
                tpd.setAccentColor(Color.parseColor("#12B4D8"));
                tpd.setTitle("Hora Inicio");

                tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        Log.d("TimePicker", "Dialog was cancelled");
                    }
                });
                tpd.show(getFragmentManager(), "Timepickerdialoginicio");
            }
        });

        // Show a datepicker when the dateButton is clicked
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                String date = dayOfMonth+"/"+(++monthOfYear)+"/"+year;
                                fechaini[0]=year;
                                fechaini[1]=monthOfYear;
                                fechaini[2]=dayOfMonth;

                                dateButton.setText(date);


                            }
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setAccentColor(Color.parseColor("#12B4D8"));
                dpd.setTitle("Dia Inicio");

                dpd.show(getFragmentManager(), "Datepickerdialoginicio");
            }
        });

        // Show a timepicker when the timeButton is clicked
        timeButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
                                String hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
                                String minuteString = minute < 10 ? "0"+minute : ""+minute;
                                String secondString = second < 10 ? "0"+second : ""+second;
                                String time = hourString+"h"+minuteString+"m"+secondString+"s";
                                horafin[0]=hourOfDay;
                                horafin[1]=minute;
                                horafin[2]=second;

                                timeButton2.setText(time);

                            }
                        },
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        false
                );
                    tpd.setAccentColor(Color.parseColor("#12B4D8"));
                    tpd.setTitle("Hora Final");

                tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        Log.d("TimePicker", "Dialog was cancelled");
                    }
                });
                tpd.show(getFragmentManager(), "Timepickerdialogfin");
            }
        });

        // Show a datepicker when the dateButton is clicked
        dateButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                String date = dayOfMonth+"/"+(++monthOfYear)+"/"+year;
                                fechafin[0]=year;
                                fechafin[1]=monthOfYear;
                                fechafin[2]=dayOfMonth;

                                dateButton2.setText(date);


                            }
                        },
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setAccentColor(Color.parseColor("#12B4D8"));
                dpd.setTitle("Dia Final");
                dpd.show(getFragmentManager(), "Datepickerdialogfin");
            }
        });


        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fechainicio,fechaFin,horaInicio,horaFin,mensaje;
                fechainicio=""+fechaini[0]+"-"+fechaini[1]+"-"+fechaini[2];
                fechaFin=""+fechafin[0]+"-"+fechafin[1]+"-"+fechafin[2];
                horaInicio=""+horaini[0]+":"+horaini[1]+":"+horaini[2];
                horaFin=""+horafin[0]+":"+horafin[1]+":"+horafin[2];
                mensaje=texto.getText().toString();

                if(fechafin[0]!=-8&&fechaini[0]!=-8&&horafin[0]!=-8&&horaini[0]!=-8){

                    db2 = openOrCreateDatabase("MyDB",android.content.Context.MODE_PRIVATE,null);
                    db2.execSQL("INSERT INTO recordatorio(fechaInicio,fechaFinal,horaInicio,horaFin,mensaje) VALUES ('" + fechainicio + "','" + fechaFin + "','" + horaInicio + "','" + horaFin + "','" + mensaje + "');");
                    db2.close();
                    finish();
                }

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        TimePickerDialog tpdinicio = (TimePickerDialog) getFragmentManager().findFragmentByTag("Timepickerdialoginicio");
        DatePickerDialog dpdinicio = (DatePickerDialog) getFragmentManager().findFragmentByTag("Datepickerdialoginicio");
        TimePickerDialog tpdfin = (TimePickerDialog) getFragmentManager().findFragmentByTag("Timepickerdialogfin");
        DatePickerDialog dpdfin = (DatePickerDialog) getFragmentManager().findFragmentByTag("Datepickerdialogfin");

        if(tpdinicio != null) tpdinicio.setOnTimeSetListener(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
                String hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
                String minuteString = minute < 10 ? "0"+minute : ""+minute;
                String secondString = second < 10 ? "0"+second : ""+second;
                String time = hourString+"h"+minuteString+"m"+secondString+"s";


            }
        });
        if(dpdinicio != null) dpdinicio.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                String date = dayOfMonth+"/"+(++monthOfYear)+"/"+year;


            }
        });

        if(tpdfin != null) tpdfin.setOnTimeSetListener(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
                String hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
                String minuteString = minute < 10 ? "0"+minute : ""+minute;
                String secondString = second < 10 ? "0"+second : ""+second;
                String time = hourString+"h"+minuteString+"m"+secondString+"s";


            }
        });
        if(dpdfin != null) dpdfin.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                String date = dayOfMonth+"/"+(++monthOfYear)+"/"+year;


            }
        });
    }//http://www.programcreek.com/java-api-examples/index.php?api=com.wdullaer.materialdatetimepicker.date.DatePickerDialog




}
