package com.buap.Proarpq;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Created by Alfredo on 04/11/2015.
 */
public class profile extends Fragment {
    SQLiteDatabase db2;
    TextView nombre,sexo,fechaNac,email,usuario;
    View v;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_profile,container,false);
        MainActivity.toolbar.getMenu().clear();
        nombre = (TextView) v.findViewById(R.id.nombre);
        sexo = (TextView) v.findViewById(R.id.sexo);
        fechaNac = (TextView) v.findViewById(R.id.fechanac);
        email = (TextView) v.findViewById(R.id.email);
        usuario = (TextView) v.findViewById(R.id.usuario);

        getinfo();
        return v;
    }

    public void getinfo(){

        db2 = v.getContext().openOrCreateDatabase("MyDB", Context.MODE_PRIVATE, null);
        Cursor c = db2.rawQuery("SELECT nombre,apellido,sexo,fechaNac,email,usuario FROM usuario", null);
        if(c.moveToFirst()) {

            nombre.setText("Nombre: "+c.getString(0) + " " + c.getString(1));
            if(c.getString(2).equals("M"))
            sexo.setText("Sexo: Masculino");
            else
                fechaNac.setText("Sexo: Femenino");
            fechaNac.setText("Fecha Nac: "+c.getString(3));

            email.setText("Correo: "+c.getString(4));
            usuario.setText("Usuario: "+c.getString(5));
        }
        db2.close();

    }

}
