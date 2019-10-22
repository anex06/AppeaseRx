package com.spandan.dextro.spandan.Util;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPref {
    public static final String MY_PREFS = "myPrefs";
    public static final String DOCTOR_DATA = "doctorDetails";
    public static final String SHARED_PREF_NAME = "myapp";
    public static final String SHARED_PREF_DEPARTMENT = "department";
    public static final String USERNAME_SHARED_PREF = "userName";
    public static final String USER_DOB_PREF = "userDob";
    public static final String USER_ID ="patient_id";
    public static final String USER_EMAIL_PREF = "userEmail";
    public static final String USER_CONTACT_PREF = "userContact";
    public static final String USER_GENDER_PREF = "userGender";
    public static final String LOGGEDIN_SHARED_PREF = "loggedin";
    public static final String FLAG_PAGE_REDIRECTION = "flag_page";
    public static final String SHARED_PREF_DEPT = "department";



    public static SharedPreferences getSharedPrefs(Context context) {
        SharedPreferences myPrefs = context.getSharedPreferences(MY_PREFS,
                Context.MODE_PRIVATE);
        return myPrefs;
    }

    public static SharedPreferences.Editor getEditor(Context context) {
        SharedPreferences myprefs = getSharedPrefs(context);
        return myprefs.edit();
    }

    public static String getString(Context context, String defValue, String key) {
        SharedPreferences myprefs = getSharedPrefs(context);
        return myprefs.getString(key, defValue);
    }

    public static void saveString(Context context, String value, String key) {
        SharedPreferences.Editor myeditor = getEditor(context);
        myeditor.putString(key, value);
        myeditor.commit();
    }
}
