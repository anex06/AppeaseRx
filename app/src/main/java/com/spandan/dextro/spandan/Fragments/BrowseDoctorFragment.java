package com.spandan.dextro.spandan.Fragments;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.spandan.dextro.spandan.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BrowseDoctorFragment extends Fragment implements View.OnClickListener {
    Spinner spinnerState, spinnerCenter, spinnerDepartment;
    TextView textViewAdvanceSearch;
    ImageView imageViewSearch;

    public BrowseDoctorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_browse_doctor, container, false);
        spinnerState            = view.findViewById(R.id.spinnerState);
        spinnerCenter           = view.findViewById(R.id.spinnerCenter);
        spinnerDepartment       = view.findViewById(R.id.spinnerDepartment);
        textViewAdvanceSearch   = view.findViewById(R.id.textViewAdvanceSearch);
        imageViewSearch         = view.findViewById(R.id.imageViewSearch);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        textViewAdvanceSearch.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.textViewAdvanceSearch:
                spinnerDepartment.setVisibility(spinnerDepartment.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                break;
        }
    }
}
