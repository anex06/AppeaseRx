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
public class MedicalServiceHomeFragment extends Fragment {


    public MedicalServiceHomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_medical, container, false);
    }

}
