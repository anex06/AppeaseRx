package com.spandan.dextro.spandan;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;
import com.spandan.dextro.spandan.Util.AppController;
import com.spandan.dextro.spandan.Util.MySharedPref;
import com.spandan.dextro.spandan.Util.Utils;
import com.spandan.dextro.spandan.Webservices.Webservice;

import org.json.JSONException;
import org.json.JSONObject;

import dmax.dialog.SpotsDialog;

public class BookAppointmentActivity extends AppCompatActivity {


    Toolbar toolbar;
    AlertDialog dialog;
    JSONObject postParams;
    String getDate, getTime,getDay;
    String getDocId, getPatienId,getCenterId;
    String sf_contact,sf_email,sf_patient_name;
    Button btn_myself, btn_others, appointmentBtn;
    EditText et_patientName, et_contact, et_email,et_message;
    TextView tv_date, tv_time;
    JSONObject inputParams;
    ImageView iv_btn_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        SharedPreferences prefsLoginData = getSharedPreferences(MySharedPref.SHARED_PREF_NAME, MODE_PRIVATE);
        sf_contact = prefsLoginData.getString(MySharedPref.USER_CONTACT_PREF,"No Contact!");
        sf_email  = prefsLoginData.getString(MySharedPref.USER_EMAIL_PREF,"No Email!");
        sf_patient_name = prefsLoginData.getString(MySharedPref.USERNAME_SHARED_PREF,"No Name!");

        getDate = getIntent().getStringExtra("selectedDate");
        getTime = getIntent().getStringExtra("selectedTimeSlot");
        getDay  = getIntent().getStringExtra("selectedDays");
        getDocId = getIntent().getStringExtra("docId");
        getPatienId = getIntent().getStringExtra("patientId");
        getCenterId = getIntent().getStringExtra("centerId");

        Log.e("data::", getDate + "-" + getTime + "-" + getDocId + "-" + getPatienId+ "-"+getCenterId);
        dialog = new SpotsDialog(this);

        btn_myself = findViewById(R.id.btn_myself);
        btn_others = findViewById(R.id.btn_others);
        appointmentBtn = findViewById(R.id.appointmentBtn);

        et_patientName = findViewById(R.id.et_patientName);
        et_contact = findViewById(R.id.et_contact);
        et_email = findViewById(R.id.et_email);

        tv_date = findViewById(R.id.tv_date);
        tv_time = findViewById(R.id.tv_time);
        et_message = findViewById(R.id.et_message);
        iv_btn_back = findViewById(R.id.iv_btn_back);

        tv_date.setText(getDate);
        tv_time.setText(getTime);

        et_contact.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        et_message.setFilters(new InputFilter[]{new InputFilter.LengthFilter(200)});

        btn_myself.setOnClickListener(view -> {
            btn_others.setBackgroundResource(R.color.color_grey);
            btn_myself.setBackgroundResource(R.color.color_green);
            et_patientName.setText(sf_patient_name);
            et_contact.setText(sf_contact);
            et_email.setText(sf_email);

        });

        btn_others.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_others.setBackgroundResource(R.color.color_green);
                btn_myself.setBackgroundResource(R.color.color_grey);
                et_patientName.setText("");
                et_contact.setText("");
                et_email.setText("");
            }
        });

        iv_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.switchActivity(getApplicationContext(),new DoctorFullDetailsActivity_BookingStep_4());
                finishAffinity();
            }
        });


        appointmentBtn.setOnClickListener(view -> {
            boolean isOkEdittext = validateEdittext(new EditText[]{et_patientName, et_contact, et_email,et_message});
            boolean isOkTextview = validateTextView(new TextView[]{tv_date, tv_time});

            if (isOkEdittext && isOkTextview){

                inputParams = new JSONObject();//docid,centerid,patientId,bookintime,bookday,patname,contact,message,datebook
                try {
                    inputParams.put("doctorId",getDocId);
                    inputParams.put("center_id",getCenterId);
                    inputParams.put("patient_id",getPatienId);
                    inputParams.put("booking_timeslot",getTime);
                    inputParams.put("booking_date",getDate);
                    inputParams.put("booking_day",getDay);
                    inputParams.put("patient_name",et_patientName.getText().toString());
                    inputParams.put("patient_contact",et_contact.getText().toString());
                    inputParams.put("patient_email",et_email.getText().toString());
                    inputParams.put("patient_msg",et_message.getText().toString());




                    getBooked(Webservice.BOOK_APPOINTMENT,inputParams);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                Utils.showMessage(getApplicationContext(),"Some fields are empty!");
            }
        });

    }

    private void getBooked(String bookAppointment, JSONObject inputParams) {
        dialog.show();
        Log.e("PostBooking::",inputParams.toString());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, bookAppointment, inputParams,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                    dialog.dismiss();
                try {
                    if (response.getString("success").equalsIgnoreCase("1")){
                        new FancyAlertDialog.Builder(BookAppointmentActivity.this)
                                .setTitle("Appointment booking")
                                .setBackgroundColor(Color.parseColor("#ffffff"))
                                .setMessage(response.getString("message"))
                                .setNegativeBtnText("Book Another")
                                .setPositiveBtnBackground(Color.parseColor("#ba0b0b"))
                                .setPositiveBtnText("Go Home")
                                .setNegativeBtnBackground(Color.parseColor("#3cbf28"))
                                .setAnimation(Animation.POP)
                                .isCancellable(true)
                                .setIcon(R.drawable.icon_tick_inside_circle, Icon.Visible)
                                .OnPositiveClicked(new FancyAlertDialogListener() {
                                    @Override
                                    public void OnClick() {

                                        Utils.switchActivity(BookAppointmentActivity.this, new MainActivity());
                                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                        finishAffinity();
                                    }
                                })

                                .OnNegativeClicked(() -> {

                                })
                                .build();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            Utils.showMessage(getApplicationContext(),error.toString());
            }
        });
        AppController.getInstance().addToRequestQueue(request);
    }

    /*********Method Textview validation***********/
    private boolean validateTextView(TextView[] textViews) {
        for (TextView currentField : textViews) {
            if (currentField.getText().toString().length() <= 0) {

                return false;
            }
        }
        return true;
    }

    /*********Method edittext validation***********/
    private boolean validateEdittext(EditText[] editTexts) {

        for (EditText currentField : editTexts) {
            if (currentField.getText().toString().length() <= 0) {

                return false;
            }
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        Utils.switchActivity(getApplicationContext(), new DoctorFullDetailsActivity_BookingStep_4());
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finishAffinity();
    }

    public void backHome(View view) {
        Utils.switchActivity(getApplicationContext(), new MainActivity());
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finishAffinity();
    }
}
