package com.spandan.dextro.spandan;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.spandan.dextro.spandan.Model.Booking;
import com.spandan.dextro.spandan.Util.MySharedPref;
import com.spandan.dextro.spandan.Util.NetworkDispatcher;
import com.spandan.dextro.spandan.Util.Utils;
import com.spandan.dextro.spandan.Webservices.Webservice;
import com.spandan.dextro.spandan.adapters.BookingsAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

public class BookingHistoryActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {

    RecyclerView previousBookingsView;
    RecyclerView upcomingBookingsView;
    ConstraintLayout loadingData;
    ConstraintLayout upcommingNotFound;
    ConstraintLayout loadingData1;
    ConstraintLayout historyNotFound;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);
        SharedPreferences prefsLoginData = getSharedPreferences(MySharedPref.SHARED_PREF_NAME, MODE_PRIVATE);
        String patient_Id = prefsLoginData.getString(MySharedPref.USER_ID, "No patient id!");

        loadingData = findViewById(R.id.loadingData);
        upcommingNotFound = findViewById(R.id.upcommingNotFound);
        loadingData1 = findViewById(R.id.loadingData1);
        historyNotFound = findViewById(R.id.historyNotFound);

        previousBookingsView = findViewById(R.id.previousBookingsView);
        upcomingBookingsView = findViewById(R.id.upcomingBookingsView);


        JSONObject inputParams = new JSONObject();

        try {
            inputParams.put("patientId", patient_Id);
            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    Webservice.BOOKING_HISTORY,
                    inputParams,
                    this,
                    this);


            NetworkDispatcher.shared.begin(this);

            NetworkDispatcher.shared.add(request);

            onFetchingData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onFetchingData() {
        loadingData.setVisibility(View.VISIBLE);
        loadingData1.setVisibility(View.VISIBLE);
        previousBookingsView.setVisibility(View.GONE);
        upcomingBookingsView.setVisibility(View.GONE);
        upcommingNotFound.setVisibility(View.GONE);
        historyNotFound.setVisibility(View.GONE);
    }

    private void onDataFetchSuccessful(boolean option) {

        if (option) {
            loadingData.setVisibility(View.GONE);
            upcomingBookingsView.setVisibility(View.VISIBLE);
            upcommingNotFound.setVisibility(View.GONE);
        } else {
            loadingData1.setVisibility(View.GONE);
            previousBookingsView.setVisibility(View.VISIBLE);
            historyNotFound.setVisibility(View.GONE);
        }
    }

    private void onDataFetchError(boolean option) {

        if (option) {
            loadingData.setVisibility(View.GONE);
            upcomingBookingsView.setVisibility(View.GONE);
            upcommingNotFound.setVisibility(View.VISIBLE);
        } else {
            loadingData1.setVisibility(View.GONE);
            previousBookingsView.setVisibility(View.GONE);
            historyNotFound.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResponse(JSONObject response) {

        try {

            if (response.isNull("currentBookingDetails")) {
                onDataFetchError(true);
            } else {
                ArrayList<Booking> bookings1 = Booking.fromJSONArray(response.getJSONArray("currentBookingDetails"));
                BookingsAdapter adapter1 = new BookingsAdapter(bookings1, BookingsAdapter.LayoutKind.CurrentBooking);
                upcomingBookingsView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                upcomingBookingsView.setAdapter(adapter1);
                onDataFetchSuccessful(true);
            }

            if (response.isNull("previousBookingDetails")) {
                onDataFetchError(false);
            } else {
                ArrayList<Booking> bookings = Booking.fromJSONArray(response.getJSONArray("previousBookingDetails"));
                BookingsAdapter adapter = new BookingsAdapter(bookings, BookingsAdapter.LayoutKind.PreviousBooking);
                previousBookingsView.setLayoutManager(new LinearLayoutManager(this));
                previousBookingsView.setAdapter(adapter);
                onDataFetchSuccessful(false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException f) {
            f.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    public void backToMain(View view) {
        Utils.switchActivity(getApplicationContext(), new MainActivity());
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finishAffinity();
    }

    @Override
    public void onBackPressed() {
        Utils.switchActivity(getApplicationContext(), new MainActivity());
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finishAffinity();
    }
}
