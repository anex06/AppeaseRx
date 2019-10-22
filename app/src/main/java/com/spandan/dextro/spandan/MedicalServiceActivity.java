package com.spandan.dextro.spandan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.spandan.dextro.spandan.Util.MySharedPref;
import com.spandan.dextro.spandan.Util.Utils;

import java.util.Objects;

public class MedicalServiceActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout opd, investigation_services;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_service);

     /*   Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Medical Services");

        toolbar.setTitleTextColor(getResources().getColor(R.color.color_white));
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finishAffinity();

        });*/

        opd = findViewById(R.id.opd);
        investigation_services = findViewById(R.id.investigation_services);


    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.opd) {
            SharedPreferences.Editor editor = getSharedPreferences(MySharedPref.FLAG_PAGE_REDIRECTION, MODE_PRIVATE).edit();
            editor.putBoolean("flag_from_mServices", true);
            editor.apply();

            Utils.switchActivity(this, new DepartmentListActivity_BookingStep_1());
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finishAffinity();
        }
    }

    @Override
    public void onBackPressed() {
            Utils.switchActivity(getApplicationContext(),new MainActivity());
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finishAffinity();
    }

    public void backToMain(View view) {
        Utils.switchActivity(getApplicationContext(),new MainActivity());
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finishAffinity();
    }
}