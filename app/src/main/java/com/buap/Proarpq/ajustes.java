package com.buap.Proarpq;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by Alfredo on 04/11/2015.
 */
public class ajustes extends Fragment {
   public static SwitchCompat datos;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ajustes,container,false);
        datos = (SwitchCompat) v.findViewById(R.id.datos);
        return v;
    }

}
