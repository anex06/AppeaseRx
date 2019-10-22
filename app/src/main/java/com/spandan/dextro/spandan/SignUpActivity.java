package com.spandan.dextro.spandan;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.spandan.dextro.spandan.Util.AppController;
import com.spandan.dextro.spandan.Util.Utils;
import com.spandan.dextro.spandan.Webservices.Webservice;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class SignUpActivity extends AppCompatActivity {
    EditText signup_name, signup_password, signup_contact_no, signup_email;
    ProgressDialog progressDialog;
    DatePickerDialog picker;
    RadioGroup radioGroup;
    Button signup_button;
    TextView signup_dob;
    String gender;
    ImageView iv_btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        progressDialog = new ProgressDialog(this);
        signup_contact_no = findViewById(R.id.signup_contact_no);
        signup_password = findViewById(R.id.signup_password);
        signup_button = findViewById(R.id.signup_button);
        signup_email = findViewById(R.id.signup_email);
        signup_name = findViewById(R.id.signup_name);
        radioGroup = findViewById(R.id.radioGroup);
        signup_dob = findViewById(R.id.signup_dob);
        iv_btn_back=findViewById(R.id.iv_btn_back);


        signup_contact_no.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});

        progressDialog.setTitle("Loading..");

        radioGroup.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            switch (checkedId) {
                case R.id.signup_radioMale:
                    gender = "male";
                    break;
                case R.id.signup_radioFemale:
                    gender = "female";
                    break;
            }
        });
iv_btn_back.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Utils.switchActivity(getApplicationContext(),new LoginActivity());
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finishAffinity();
    }
});
        signup_button.setOnClickListener(view -> {

            if (isValid()) {
                progressDialog.show();

                JSONObject inputParams = new JSONObject();

                try {
                    inputParams.put("patient_name", signup_name.getText().toString());
                    inputParams.put("patient_email", signup_email.getText().toString());
                    inputParams.put("patient_password", signup_password.getText().toString());
                    inputParams.put("patient_contact", signup_contact_no.getText().toString());
                    inputParams.put("patient_dob", signup_dob.getText().toString());
                    inputParams.put("patient_gender", gender);

                    signMeUp(Webservice.REGISTRATION, inputParams);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        });

    }

    /******************Signup**********************/
    private void signMeUp(String registration, JSONObject inputParams) {
        JsonObjectRequest signupReq = new JsonObjectRequest(Request.Method.POST, registration, inputParams, response -> {
            progressDialog.dismiss();

            Log.e("RsSignup::",response.toString());
            try {
                if (response.getString("success").equalsIgnoreCase("1")) {
                    Utils.showMessage(getApplicationContext(), response.getString("message"));
                    Utils.myDialogSingleButton(this, new LoginActivity(), "Registration success!", "Please go to login page and login", "Yes");

                } else {
                    Utils.showMessage(getApplicationContext(), response.getString("message"));
                }
            } catch (JSONException je) {
                Log.e("Exception::", "Json");
            }

        }, error -> Log.e("Signup Resp::", error.toString()));
        AppController.getInstance().addToRequestQueue(signupReq);
        signupReq.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    boolean isValid() {
        if (signup_name.getText().toString().isEmpty() || signup_name.getText().toString().length() < 3) {
            Utils.showMessage(getApplicationContext(), "Invalid patient name!");
            return false;
        } else if (signup_password.getText().toString().isEmpty() || signup_password.getText().length() < 4) {
            Utils.showMessage(getApplicationContext(), "Password must be 4-8 characters!");
            return false;
        } else if (signup_dob.getText().toString().isEmpty() || signup_dob.getText().toString().length() < 4) {
            Utils.showMessage(getApplicationContext(), "Invalid Date of birth!");
            return false;
        } else if (signup_contact_no.getText().toString().isEmpty() || signup_contact_no.getText().toString().length() < 10) {
            Utils.showMessage(getApplicationContext(), "Mobile no must be 10 characters!");
            return false;
        } else if (signup_email.getText().toString().isEmpty() || signup_email.getText().toString().length() < 6) {
            Utils.showMessage(getApplicationContext(), "Enter correct email!");
            return false;
        }
        return true;
    }

    public void setDob(View view) {
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);

        picker = new DatePickerDialog(SignUpActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        signup_dob.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, year, month, day);
        picker.show();
    }

    @Override
    public void onBackPressed() {

        Utils.switchActivity(getApplicationContext(),new LoginActivity());
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finishAffinity();
    }

    public void backToMain(View view) {
        Utils.switchActivity(getApplicationContext(),new LoginActivity());
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finishAffinity();
    }
}