package com.spandan.dextro.spandan.Fragments;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.spandan.dextro.spandan.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class OPDFragment extends Fragment {


    public OPDFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_opd, container, false);
    }

}
