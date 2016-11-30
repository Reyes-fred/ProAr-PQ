package com.buap.Proarpq;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Alfredo on 04/11/2015.
 */
public class recordatorio extends Fragment implements WeekView.MonthChangeListener,
        WeekView.EventClickListener, WeekView.EventLongPressListener {
    View v;
    FragmentManager mFragmentManager;
    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;
    private WeekView mWeekView;
    SQLiteDatabase db2;


    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         v = inflater.inflate(R.layout.recordatorio,container,false);
        // Get a reference for the week view in the layout.
       // Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        MainActivity.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                setupDateTimeInterpreter(id == R.id.action_week_view);
                switch (id) {
                    case R.id.action_today:
                        mWeekView.goToToday();
                        return true;
                    case R.id.action_day_view:
                        if (mWeekViewType != TYPE_DAY_VIEW) {
                            item.setChecked(!item.isChecked());
                            mWeekViewType = TYPE_DAY_VIEW;
                            mWeekView.setNumberOfVisibleDays(1);

                            // Lets change some dimensions to best fit the view.
                            mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                            mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                            mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                        }
                        return true;
                    case R.id.action_three_day_view:
                        if (mWeekViewType != TYPE_THREE_DAY_VIEW) {
                            item.setChecked(!item.isChecked());
                            mWeekViewType = TYPE_THREE_DAY_VIEW;
                            mWeekView.setNumberOfVisibleDays(3);

                            // Lets change some dimensions to best fit the view.
                            mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                            mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                            mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                        }
                        return true;
                    case R.id.action_week_view:
                        if (mWeekViewType != TYPE_WEEK_VIEW) {
                            item.setChecked(!item.isChecked());
                            mWeekViewType = TYPE_WEEK_VIEW;
                            mWeekView.setNumberOfVisibleDays(7);

                            // Lets change some dimensions to best fit the view.
                            mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                            mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                            mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                        }
                        return true;
                }
                return true;
            }
        });
        MainActivity.toolbar.getMenu().clear();
        MainActivity.toolbar.inflateMenu(R.menu.menu_recordatorio);
        MainActivity.toolbar.setTitle("ProAR-PQ");


        mWeekView = (WeekView) v.findViewById(R.id.weekView);

        // Show a toast message about the touched event.
        mWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(this);

        // Set up a date time interpreter to interpret how the date and time will be formatted in
        // the week view. This is optional.
        setupDateTimeInterpreter(false);


        v.findViewById(R.id.nuevorecodatorio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),nuevorecordatorio.class);
                startActivity(i);

            }
        });



        return v;
    }





    /**
     * Set up a date time interpreter which will show short date values when in week view and long
     * date values otherwise.
     * @param shortDate True if the date values should be short.
     */
    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.getDefault());

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
            }
        });
    }



    @Override
    public synchronized List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        Calendar startTime = Calendar.getInstance();
        Calendar endTime = (Calendar) startTime.clone();

        // Populate the week view with some events.
        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
        WeekViewEvent event;
        events.clear();
        db2 = v.getContext().openOrCreateDatabase("MyDB", Context.MODE_PRIVATE, null);
        Cursor c = db2.rawQuery("SELECT id,fechaInicio,fechaFinal,horaInicio,horaFin,mensaje FROM recordatorio", null);
        Log.d("Mes", "" + newMonth);
        int i=1;
        if(c.moveToFirst()) {
                while(c.moveToNext())
                {
                    String[] fechainicio = c.getString(1).split("-");
                    String[] fechafinal = c.getString(2).split("-");
                    String[] horainicio = c.getString(3).split(":");
                    String[] horafinal = c.getString(4).split(":");

/*                    Log.d("fechainicio", fechainicio[2]+"/"+fechainicio[1]+"/"+fechainicio[0]);
                    Log.d("fechafin", fechafinal[2]+"/"+fechafinal[1]+"/"+fechafinal[0]);
                    Log.d("horainicio", horainicio[1]+":"+horainicio[0]);
                    Log.d("horainicio", horafinal[1]+":"+horafinal[0]);
*/
                    startTime = Calendar.getInstance();
                    startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(horainicio[0]));
                    startTime.set(Calendar.MINUTE, Integer.parseInt(horainicio[1]));
                    startTime.set(Calendar.DAY_OF_MONTH,Integer.parseInt(fechainicio[2]));
                    startTime.set(Calendar.MONTH, Integer.parseInt(fechainicio[1]) - 1);
                    startTime.set(Calendar.YEAR, Integer.parseInt(fechainicio[0]));
                    Log.d("Inicio:", "" + startTime.getTime());

                    endTime = (Calendar) startTime.clone();
                    endTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(horafinal[0]));
                    endTime.set(Calendar.MINUTE, Integer.parseInt(horafinal[1]));
                    endTime.set(Calendar.DAY_OF_MONTH,Integer.parseInt(fechafinal[2]));
                    endTime.set(Calendar.MONTH, Integer.parseInt(fechafinal[1]) - 1);
                    endTime.set(Calendar.YEAR, Integer.parseInt(fechafinal[0]));
                    Log.d("Fin:", "" + endTime.getTime());


                    event = new WeekViewEvent(i, getEventTitle(startTime), startTime, endTime);
                    //event.setName(c.getString(5));
                    Log.d("Evento ", ""+c.getInt(0));
                    event.setColor(getResources().getColor(R.color.event_color_01));
                    events.add(event);
                    i++;
                }


        }
        db2.close();
        Log.d("total ", "" + events.size());
        return events;
    }

    private String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(v.getContext(),  event.getName(), Toast.LENGTH_SHORT).show();
        //detalles
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(v.getContext(),  event.getName(), Toast.LENGTH_SHORT).show();

        //eliminar evento
    }

    // Called at the start of the visible lifetime.
    @Override
    public void onStart(){
        super.onStart();
        // Apply any required UI change now that the Fragment is visible.
    }

    // Called at the start of the active lifetime.
    @Override
    public void onResume(){
        super.onResume();
        // Resume any paused UI updates, threads, or processes required
        // by the Fragment but suspended when it became inactive.
    }
    // Called at the end of the active lifetime.
    @Override
    public void onPause(){
        // Suspend UI updates, threads, or CPU intensive processes
        // that don't need to be updated when the Activity isn't
        // the active foreground activity.
        // Persist all edits or state changes
        // as after this call the process is likely to be killed.
        super.onPause();
    }
}
