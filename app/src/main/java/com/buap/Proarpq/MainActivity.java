package com.buap.Proarpq;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    Intent intent;
    Intent salir;
    AlertDialog.Builder alertDialog;
    SQLiteDatabase db2;

    TextView nombre,correo;

    public static Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nombre = (TextView) findViewById(R.id.username);
        correo = (TextView) findViewById(R.id.email);

        getinfo();
        /**
         *Setup the DrawerLayout and NavigationView
         */

             mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
             mNavigationView = (NavigationView) findViewById(R.id.shitstuff) ;

        /**
         * Lets inflate the very first fragment
         * Here , we are inflating the TabFragment as the first Fragment
         */

             mFragmentManager = getSupportFragmentManager();
             mFragmentTransaction = mFragmentManager.beginTransaction();
             mFragmentTransaction.replace(R.id.containerView,new TabFragment()).commit();
        /**
         * Setup click events on the Navigation View Items.
         */
         intent = new Intent(this,NoteList.class);
        salir = new Intent(this,login.class);
             mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

             @Override
             public boolean onNavigationItemSelected(MenuItem menuItem) {
                mDrawerLayout.closeDrawers();

                 if (menuItem.getItemId() == R.id.nav_item_inicio) {
                     FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                     xfragmentTransaction.replace(R.id.containerView,new TabFragment()).commit();
                 }

                 if (menuItem.getItemId() == R.id.nav_item_notas) {
                     startActivity(intent);
                 }

                 if (menuItem.getItemId() == R.id.nav_item_recordatorio) {
                     FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                     xfragmentTransaction.replace(R.id.containerView,new recordatorio()).commit();
                 }

                 if (menuItem.getItemId() == R.id.nav_item_actividad) {
                     FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                     xfragmentTransaction.replace(R.id.containerView,new actividad()).commit();
                 }

                 if (menuItem.getItemId() == R.id.nav_item_cuestionario) {
                     FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                     xfragmentTransaction.replace(R.id.containerView,new cuestionario()).commit();
                 }

                 if (menuItem.getItemId() == R.id.nav_item_perfil) {
                     FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                     xfragmentTransaction.replace(R.id.containerView,new profile()).commit();
                 }

                 if (menuItem.getItemId() == R.id.nav_item_salida) {
                     db2 = openOrCreateDatabase("MyDB",android.content.Context.MODE_PRIVATE,null);
                     db2.execSQL("DELETE FROM user;");
                     db2.execSQL("VACUUM;");
                     db2.close();

                     startActivity(salir);
                     finish();
                 }

                 if (menuItem.getItemId() == R.id.nav_item_ajustes) {
                     FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                     xfragmentTransaction.replace(R.id.containerView,new ajustes()).commit();
                 }

                 if (menuItem.getItemId() == R.id.nav_item_salida) {
                     finish();
                     Toast toast1 =
                             Toast.makeText(getApplicationContext(),
                                     "Usuario desconectado", Toast.LENGTH_SHORT);

                     toast1.show();

                     startActivity(salir);
                 }
                 return false;
            }

        });

        /**
         * Setup Drawer Toggle of the Toolbar
         */



                toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
                toolbar.getMenu().clear();
                //toolbar.inflateMenu(R.menu.menu_main);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout, toolbar,R.string.app_name,
                R.string.app_name);

                mDrawerLayout.setDrawerListener(mDrawerToggle);

                mDrawerToggle.syncState();

    }
/*
    public void showLogoutSesion(){
        alertDialog = new AlertDialog.Builder(MainActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle("Cerrar Sesión");

        // Setting Dialog Message
        alertDialog.setMessage("¿Desea cerrar sesión?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                db2 = openOrCreateDatabase("MyDB",android.content.Context.MODE_PRIVATE,null);
                db2.execSQL("DELETE FROM user;");
                db2.execSQL("VACUUM;");
                db2.close();

                startActivity(salir);
                finish();
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.create();
        // Showing Alert Message
        alertDialog.show();
    }*/

    public void getinfo(){

        db2 = openOrCreateDatabase("MyDB",MODE_PRIVATE,null);
        Cursor c = db2.rawQuery("SELECT nombre,apellido,email FROM usuario", null);
        c.moveToFirst();

        nombre.setText(c.getString(0) + " "+c.getString(1));
        correo.setText(c.getString(2));

        db2.close();

    }

}