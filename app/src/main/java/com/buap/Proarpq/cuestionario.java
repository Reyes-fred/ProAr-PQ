package com.buap.Proarpq;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


/**
 * Created by Alfredo on 04/11/2015.
 */
public class cuestionario extends Fragment {
    Spinner ans1,ans2,ans3,ans4,ans5,ans6,ans7,ans8,ans9,ans10,ans11,ans12,ans13;
    View v;
    ArrayAdapter lista;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         v = inflater.inflate(R.layout.cuestionario,container,false);
        MainActivity.toolbar.getMenu().clear();

        inicializarSpinner();
        return v;
    }


    public void inicializarSpinner(){

        ans1 = (Spinner) v.findViewById(R.id.ans1);
        ans2 = (Spinner) v.findViewById(R.id.ans2);
        ans3 = (Spinner) v.findViewById(R.id.ans3);
        ans4 = (Spinner) v.findViewById(R.id.ans4);
        ans5 = (Spinner) v.findViewById(R.id.ans5);
        ans6 = (Spinner) v.findViewById(R.id.ans6);
        ans7 = (Spinner) v.findViewById(R.id.ans7);
        ans8 = (Spinner) v.findViewById(R.id.ans8);
        ans9 = (Spinner) v.findViewById(R.id.ans9);
        ans10 = (Spinner) v.findViewById(R.id.ans10);
        ans11 = (Spinner) v.findViewById(R.id.ans11);
        ans12 = (Spinner) v.findViewById(R.id.ans12);
        ans13 = (Spinner) v.findViewById(R.id.ans13);
        lista = ArrayAdapter.createFromResource(
                getActivity(), R.array.respuestas, android.R.layout.simple_spinner_item);
        lista.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ans1.setAdapter(lista);
        ans2.setAdapter(lista);
        ans3.setAdapter(lista);
        ans4.setAdapter(lista);
        ans5.setAdapter(lista);
        ans6.setAdapter(lista);
        ans7.setAdapter(lista);
        ans8.setAdapter(lista);
        ans9.setAdapter(lista);
        ans10.setAdapter(lista);
        ans11.setAdapter(lista);
        ans12.setAdapter(lista);
        ans13.setAdapter(lista);

    }

}
