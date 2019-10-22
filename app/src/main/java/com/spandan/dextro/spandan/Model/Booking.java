package com.spandan.dextro.spandan.Model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Booking implements Parcelable {

    private String doctorImage;
    private String doctorName;
    private String centerName;
    private String timeSlot;
    private String address;
    private String phone;
    private Date   date;

    public Booking(JSONObject object) throws JSONException, ParseException {

        SimpleDateFormat parser = new SimpleDateFormat("dd-MM-yyyy",Locale.US);

        doctorImage = object.optString("doctor_image");
        doctorName  = object.optString("doctor_name");
        centerName  = object.optString("center_name");
        timeSlot    = object.optString("booking_timeslot");
        address     = object.optString("address");
        phone       = object.optString("phone");
        date        = parser.parse(object.getString("booking_date"));
    }

    protected Booking(Parcel in) {
        doctorImage = in.readString();
        doctorName = in.readString();
        centerName = in.readString();
        timeSlot = in.readString();
        address = in.readString();
        phone = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(doctorImage);
        dest.writeString(doctorName);
        dest.writeString(centerName);
        dest.writeString(timeSlot);
        dest.writeString(address);
        dest.writeString(phone);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Booking> CREATOR = new Creator<Booking>() {
        @Override
        public Booking createFromParcel(Parcel in) {
            return new Booking(in);
        }

        @Override
        public Booking[] newArray(int size) {
            return new Booking[size];
        }
    };

    public static ArrayList<Booking> fromJSONArray(JSONArray array) throws JSONException, ParseException{

        ArrayList<Booking> bookings = new ArrayList<>();
        for (int i = 0; i < array.length(); i++ ) bookings.add(new Booking(array.getJSONObject(i)));
        return bookings;
    }

    public Date getDate() {
        return date;
    }

    public String getAddress() {
        return address;
    }


    public String getDoctorImage() {
        return doctorImage;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getCenterName() {
        return centerName;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public String getPhone() {
        return phone;
    }
}
