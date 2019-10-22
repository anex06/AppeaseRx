package com.spandan.dextro.spandan.Util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Utils {
    private static NetworkInfo networkInfo;
    Context context;
    private static long NOD;
    int flagDate;
   static AlertDialog.Builder builder;
   static ProgressDialog dialog;

/******************Dialog yes button**************/
    public static void myDialogSingleButton(Context context, Activity toActivity, String title, String messageAlert, String positive){
        builder = new AlertDialog.Builder(context);
        builder.setMessage(messageAlert)
                .setCancelable(false)
                .setPositiveButton(positive, (dialog, id) -> switchActivity(context,toActivity));
        AlertDialog alert = builder.create();
        alert.setTitle(title);
        alert.show();

    }
    /************Dialog yes/no button**************/
    public static void myDialogButton(Context context,String title,String message,String positive,String negative){
        builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(context, "you choose yes action for alertbox",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                        Toast.makeText(context, "you choose no action for alertbox",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setTitle(title);
        alert.show();

    }


    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = Objects.requireNonNull(cm).getActiveNetworkInfo();
        if (netInfo != null) {
            if(netInfo.isAvailable() && netInfo.isConnectedOrConnecting()){
                return true;
            }else {
                return false;
            }
        } else {
            return false;
        }
    }
    public static void showMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    public static void switchActivity(final Context context, final Activity activity){
        Intent i = new Intent(context,activity.getClass());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(i);



    }

    public static void hideKeyboard(Activity activity){
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            Objects.requireNonNull(imm).hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static boolean checkAndroidRuntimePermission(final Context context, String permissionVal, final int requestInt, String alertTitle, String alertMessage) {
        final String[] permissionArray = {permissionVal};
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(context, permissionVal) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permissionVal)) {
                    AlertDialog.Builder alertBuilder= new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle(alertTitle);//"Permission Required");
                    alertBuilder.setMessage(alertMessage);//"This permission is necessary to perform application properly");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog , int which) {
                            ActivityCompat.requestPermissions((Activity)context, permissionArray, requestInt);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity)context, permissionArray, requestInt);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }


    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        try {
            networkInfo = Objects.requireNonNull(cm).getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // test for connection for WIFI
        if (networkInfo != null
                && networkInfo.isAvailable()
                && networkInfo.isConnected()) {
            return true;
        }

        networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        // test for connection for Mobile
        if (networkInfo != null
                && networkInfo.isAvailable()
                && networkInfo.isConnected()) {
            return true;
        }

        return false;
    }
    public static long getNumberOfDays(String myDate, String currentDate) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date date1 = myFormat.parse(myDate);
            Date date2 = myFormat.parse(currentDate);
            long diff = date2.getTime() - date1.getTime();
            NOD = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            NOD = NOD + 1;
            //Log.e("NOD>>", String.valueOf(NOD));
            // textViewNOD.setText(String.valueOf(NOD) + "days");
            System.out.println("Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return NOD;
    }



}
