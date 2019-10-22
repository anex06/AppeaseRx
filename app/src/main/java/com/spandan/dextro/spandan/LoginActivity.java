package com.spandan.dextro.spandan;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.spandan.dextro.spandan.Util.AppController;
import com.spandan.dextro.spandan.Util.MySharedPref;
import com.spandan.dextro.spandan.Util.Utils;
import com.spandan.dextro.spandan.Webservices.Webservice;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    Animation animFadein;
    ImageView imageView;
    boolean show = false;
    ConstraintLayout cc1;

    EditText editTextUser, editTextPassword;
    TextView textViewSignUp, textViewLogin;
    Button buttonSignIn;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        cc1 = findViewById(R.id.cons1);
        textViewLogin = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageViewTouch);
        textViewSignUp = findViewById(R.id.textViewSignUp);
        editTextUser = findViewById(R.id.editTextUser);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonSignIn = findViewById(R.id.buttonSignIn);

        animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.blink);
        textViewSignUp.startAnimation(animFadein);
        dialog = new ProgressDialog(this);
        dialog.setTitle("Please wait");
        dialog.setMessage("Logging in...");

        imageView.setOnClickListener(this);
        buttonSignIn.setOnClickListener(this);
        requestPermission();
    }

    private void revertAnimation() {

        show = false;
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this, R.layout.activity_login);

        ChangeBounds transition = new ChangeBounds();
        transition.setInterpolator(new AnticipateInterpolator(2.0f));
        transition.setDuration(1200);

        TransitionManager.beginDelayedTransition(cc1, transition);
        constraintSet.applyTo(cc1);

    }

    private void showAnimation() {
        show = true;

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this, R.layout.activity_login_animated);

        ChangeBounds transition = new ChangeBounds();
        transition.setInterpolator(new AnticipateInterpolator(1.0f));
        transition.setDuration(1200);

        TransitionManager.beginDelayedTransition(cc1, transition);
        constraintSet.applyTo(cc1);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageViewTouch:
                if (show)
                    revertAnimation();
                else
                    showAnimation();
                break;

            case R.id.buttonSignIn:
                if(isValid()){
                    try {
                        dialog.show();
                        JSONObject loginParams = new JSONObject();
                        loginParams.put("patient_email",editTextUser.getText().toString());
                        loginParams.put("patient_pass",editTextPassword.getText().toString());
                        logMeIn(Webservice.LOGIN,loginParams);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                break;
        }
    }
/**********************Login process******************/
    private void logMeIn(String login, JSONObject loginParams) {
       // Log.e("Loginparams::",loginParams.toString());

        JsonObjectRequest loginRequest =  new JsonObjectRequest(Request.Method.POST, login, loginParams, response -> {
            dialog.dismiss();
            Log.e("Response login::",response.toString());
            try {
                if (response.getString("success").equalsIgnoreCase("1")){
                    String name = "",email="",dob="",contact="",gender="",id="";

                    JSONObject object = response.getJSONObject("user_details");
                    for (int i = 0;i<object.length();i++){
                        name=object.getString("patient_name");
                        email=object.getString("patient_email");
                        dob=object.getString("patient_dob");
                        contact=object.getString("patient_contact");
                        gender=object.getString("patient_gender");
                        id=object.getString("patient_id");


                    }
              //      Log.e("Name::",name);
                    Utils.switchActivity(getApplicationContext(),new MainActivity());

                    /*********Save user data to shared pref****************/
                    SharedPreferences sharedPreferences = LoginActivity.this
                            .getSharedPreferences(MySharedPref.
                                    SHARED_PREF_NAME, Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor_logged = sharedPreferences.edit();

                    editor_logged.putBoolean(MySharedPref.LOGGEDIN_SHARED_PREF, true);
                    editor_logged.putString(MySharedPref.USER_CONTACT_PREF, contact);
                    editor_logged.putString(MySharedPref.USERNAME_SHARED_PREF, name);
                    editor_logged.putString(MySharedPref.USER_GENDER_PREF, gender);
                    editor_logged.putString(MySharedPref.USER_EMAIL_PREF, email);
                    editor_logged.putString(MySharedPref.USER_DOB_PREF, dob);
                    editor_logged.putString(MySharedPref.USER_ID,id);

                    editor_logged.apply();

                }
                Utils.showMessage(getApplicationContext(),response.getString("message"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error login::",error.toString());
            }
        });
        AppController.getInstance().addToRequestQueue(loginRequest);

    }

    private boolean isValid() {
        if (editTextUser.getText().toString().isEmpty() || editTextUser.getText().toString().length()<3){
            editTextUser.setError("Invalid username!");
            return false;
        }else if (editTextPassword.getText().toString().isEmpty() || editTextPassword.getText().toString().length()<4){
            editTextPassword.setError("Invalid password!");
            return false;
        }
        return true;
    }

    public void signUp(View view) {
        startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        SharedPreferences sharedPreferences = getSharedPreferences(MySharedPref.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        boolean loggedIn = sharedPreferences.getBoolean(MySharedPref.LOGGEDIN_SHARED_PREF, false);
        if (loggedIn) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);

        }
    }

    /*****************Request permission************/
    private void requestPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CALL_PHONE
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                          //  Utils.showMessage(LoginActivity.this,"All permission is granted!");

                        }
                        for (int i = 0; i < report.getDeniedPermissionResponses().size(); i++) {
                            Utils.showMessage(LoginActivity.this, "Permission denied!");

                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permenantly, navigate user to app settings

                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", getPackageName(), null));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }


                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(error -> Utils.showMessage(LoginActivity.this, "Permission issue found!"))
                .onSameThread()
                .check();
    }

}
