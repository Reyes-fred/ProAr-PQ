package com.buap.Proarpq;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by Alfredo on 04/11/2015.
 */
public class notificacion extends Fragment {
    FragmentManager mFragmentManager;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.notificacion,container,false);
        mFragmentManager = getActivity().getSupportFragmentManager();
        v.findViewById(R.id.mensaje).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction xfragmentTransaction = mFragmentManager.beginTransaction();
                xfragmentTransaction.replace(R.id.containerView,new mensaje()).commit();

            }
        });
        MainActivity.toolbar.getMenu().clear();
        return v;
    }

}
