package com.spandan.dextro.spandan;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.spandan.dextro.spandan.Model.ModelDoctor;
import com.spandan.dextro.spandan.Util.AppController;
import com.spandan.dextro.spandan.Util.MySharedPref;
import com.spandan.dextro.spandan.Util.Utils;
import com.spandan.dextro.spandan.Webservices.Webservice;
import com.squareup.timessquare.CalendarPickerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

public class DoctorFullDetailsActivity_BookingStep_4 extends AppCompatActivity {

    private CalendarPickerView calendar_view;
    private Calendar nextYear;
    private ModelDoctor doctorList;
    private TextView tv_docName, tv_docQualification;
    private ImageView iv_docImage;
    private Spinner spinnerTimeSlots;
    private Button buttonNext;
    private JSONObject post_parameters;
    //private ArrayList<ModelDoctor> modelDoctorsDates;
    ArrayList<ModelDoctor> modelDatesTimeList = new ArrayList<>();
    String stringDate;
    private AlertDialog dialog;
    private TextView tv_info;
    private TextView tv_days, tv_time_slot;
    private String startTime, endTime;
    private TextView tvNextpage;
    private String savedDate;
    private TextView tv_set_time;
    private boolean proceed = false;
    private int mHour, mMinute;
    private String selectedTimeSlot = "";
    private String doc_Id, patient_Id;
    private boolean isValidTime = false;

    private Button iv_btn_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_full_details);

        // modelDoctorsDates = new ArrayList<>();

        calendar_view = findViewById(R.id.calendar_view);
        tv_docName = findViewById(R.id.tv_docName);
        tv_docQualification = findViewById(R.id.tv_docQualification);
       /* tv_info = findViewById(R.id.tv_info);
        tv_info.setSelected(true);*/
        iv_btn_back=findViewById(R.id.iv_btn_back);

        tv_days = findViewById(R.id.tv_days);
        tv_time_slot = findViewById(R.id.tv_time_slot);
        tvNextpage = findViewById(R.id.textViewNext);

        iv_docImage = findViewById(R.id.iv_docImage);
        dialog = new SpotsDialog(this);

        nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);
        Date today = new Date();
        calendar_view.init(today, nextYear.getTime())
                .withSelectedDate(today)
                .inMode(CalendarPickerView.SelectionMode.SINGLE);


        doctorList = new ModelDoctor();


        Gson gson = new Gson();
        String doctorrData = MySharedPref.getString(this, "", MySharedPref.DOCTOR_DATA);
        doctorList = gson.fromJson(doctorrData, ModelDoctor.class);



        tv_docName.setText(doctorList.getDoctorName());
        tv_docQualification.setText(doctorList.getDocQualification());

        SharedPreferences prefsLoginData = getSharedPreferences(MySharedPref.SHARED_PREF_NAME, MODE_PRIVATE);
        patient_Id = prefsLoginData.getString(MySharedPref.USER_ID, "No patient id!");

        SharedPreferences prefs = getSharedPreferences(MySharedPref.FLAG_PAGE_REDIRECTION, MODE_PRIVATE);
        String getDepartment = prefs.getString("sfDepartment", "No dept");

        String getCenter = prefs.getString("sfCenter", "No center");
        doc_Id = doctorList.getDocId();

        post_parameters = new JSONObject();
        try {
            post_parameters.put("deptId", getDepartment);
            post_parameters.put("centerId", getCenter);
            post_parameters.put("docId", doc_Id);
            getAvailableDates(Webservice.GET_DOCTOR_AVAILABILITY_DATA, post_parameters);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        iv_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.switchActivity(getApplicationContext(), new DoctorListAndSearchActivity_BookingStep_3());
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finishAffinity();
            }
        });
        calendar_view.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                String selectedDate = sdf.format(date);

                //Log.e("dateIs::",selectedDate);
                for (int i = 0; i < modelDatesTimeList.size(); i++) {
                    savedDate = String.valueOf(modelDatesTimeList.get(i).getDocDates());

                    if (selectedDate.equalsIgnoreCase(savedDate)) {
                        proceed = true;
                        tv_days.setText(modelDatesTimeList.get(i).getDocDays());
                        tv_time_slot.setText(modelDatesTimeList.get(i).getStartTime()+"-"+modelDatesTimeList.get(i).getEndTime());
                        selectedTimeSlot =modelDatesTimeList.get(i).getStartTime()+"-"+modelDatesTimeList.get(i).getEndTime();

                   /*     startTime = modelDatesTimeList.get(i).getStartTime();
                        endTime = modelDatesTimeList.get(i).getEndTime();*/
                        break;
                    } else {
                        proceed = false;
                    }
                }
                if (!proceed) {
                    Utils.showMessage(DoctorFullDetailsActivity_BookingStep_4.this, "Doctor is not available on selected date!");
                    Log.e("Match::", "Not found");
                }


            }

            @Override
            public void onDateUnselected(Date date) {

                //  Toast.makeText(getApplicationContext(), "UnSelected Date is : " + date.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        /****************Time picker********************/
/*
        tv_set_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(DoctorFullDetailsActivity_BookingStep_4.this,
                        (view1, hourOfDay, minute) -> {

                            selectedTimeSlot = (hourOfDay + ":" + minute);

                            if (checktimings(selectedTimeSlot, startTime, endTime)) {
                                tv_set_time.setText(selectedTimeSlot);
                                isValidTime = true;
                            } else {
                                tv_set_time.setText("00:00");
                                isValidTime = false;
                                Utils.showMessage(DoctorFullDetailsActivity_BookingStep_4.this, "Invalid time.");
                            }

                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });
*/
        /***********Go next page************/
        tvNextpage.setOnClickListener(view -> {
            if (proceed) {

                    Intent intentBookFinnal = new Intent(DoctorFullDetailsActivity_BookingStep_4.this, BookAppointmentActivity.class);
                    intentBookFinnal.putExtra("selectedDate", savedDate);
                      intentBookFinnal.putExtra("selectedDays", tv_days.getText().toString());
                    intentBookFinnal.putExtra("selectedTimeSlot", selectedTimeSlot);

                    intentBookFinnal.putExtra("docId", doc_Id);
                    intentBookFinnal.putExtra("patientId", patient_Id);
                    intentBookFinnal.putExtra("centerId",getCenter);
                    startActivity(intentBookFinnal);

            } else {
                Utils.showMessage(DoctorFullDetailsActivity_BookingStep_4.this, "Please select highlighted date!");
            }
        });

    }

    private void getAvailableDates(String getDoctorsByDept, JSONObject post_parameters) {
        dialog.show();
        JsonObjectRequest requestDocDetails = new JsonObjectRequest(Request.Method.POST, getDoctorsByDept,
                post_parameters, response -> {
            dialog.dismiss();
            //   Log.e("ResponseDates::",response.toString());
            try {
                JSONArray jsonArray = response.getJSONArray("availibility");
                for (int i = 0; i < jsonArray.length(); i++) {
                    ModelDoctor doctor = new ModelDoctor();
                    JSONObject jobject = jsonArray.getJSONObject(i);
                    doctor.setDocDays(jobject.getString("day"));
                    doctor.setDocDates(jobject.getString("date"));

                    calendar_view.highlightDates(getHighLightedDates(jobject.getString("date")));
                    doctor.setStartTime(jobject.getString("start_time"));
                    doctor.setEndTime(jobject.getString("end_time"));
                    modelDatesTimeList.add(doctor);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Utils.showMessage(getApplicationContext(), error.toString());
            Utils.switchActivity(getApplicationContext(), new DoctorListAndSearchActivity_BookingStep_3());
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        AppController.getInstance().addToRequestQueue(requestDocDetails);
        requestDocDetails.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    /****************Highlight dates****************/
    private ArrayList<Date> getHighLightedDates(String s) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

        String dateInString = s;

        Date date = null;

        try {
            date = sdf.parse(dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ArrayList<Date> availableDates = new ArrayList<>();
        availableDates.add(date);
        return availableDates;
    }

    /****************Check time*******************/
    private boolean checktimings(String pickTime, String startTime, String endtime) {

       /* Log.e("Picktime::",pickTime);
        Log.e("DocStart::",startTime);
        Log.e("DocEnd::",endtime);*/

        String pattern = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date pickerTime = sdf.parse(pickTime);
            Date docStartTime = sdf.parse(startTime);
            Date docEndTime = sdf.parse(endtime);

            if (pickerTime.before(docEndTime) && pickerTime.after(docStartTime)) {
                return true;
            } else {

                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        Utils.switchActivity(getApplicationContext(), new DoctorListAndSearchActivity_BookingStep_3());
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finishAffinity();
    }

    public void backHome(View view) {
        Utils.switchActivity(getApplicationContext(), new MainActivity());
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finishAffinity();
    }
}
