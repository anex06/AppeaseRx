package com.spandan.dextro.spandan;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;
import com.spandan.dextro.spandan.Util.AppController;
import com.spandan.dextro.spandan.Util.MySharedPref;
import com.spandan.dextro.spandan.Util.Utils;
import com.spandan.dextro.spandan.Webservices.Webservice;

import org.json.JSONException;

import java.util.Calendar;

import dmax.dialog.SpotsDialog;

import static com.spandan.dextro.spandan.Util.MySharedPref.SHARED_PREF_NAME;
import static com.spandan.dextro.spandan.Util.MySharedPref.USERNAME_SHARED_PREF;
import static com.spandan.dextro.spandan.Util.MySharedPref.USER_EMAIL_PREF;


public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        NavigationView.OnNavigationItemSelectedListener {

    private ImageView imageMedicalService, imageMakeAnQuery, imageBookApntmnt, imageViewReport;
    private ImageView iv_nav_menu;
    private TextView tv_logout, tv_reminder;
    private TextView textViewBrowseDoctor;
    private ImageView image_tips;
    private TextView textViewTipSubject, textViewTips, textViewTipsByDr;
    private SpotsDialog dialog;
    private String tip_subject;
    private String tip;
    private String suggest_by;
   // private ImageView tv_logout,tv_reminder;
    private NavigationView navigationView;
    private DrawerLayout mDrawerLayout;
    private View navHeader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        String userName = prefs.getString(USERNAME_SHARED_PREF,"No name");
        String userEmail = prefs.getString(USER_EMAIL_PREF,"No email");

      //  Log.e("Username::",userName+"::"+userEmail);

        imageMedicalService = findViewById(R.id.imageMedicalService);
        imageMakeAnQuery = findViewById(R.id.imageMakeAnQuery);
        imageBookApntmnt = findViewById(R.id.imageBookApntmnt);
        textViewTipSubject = findViewById(R.id.tv_tip_subject);
        imageViewReport = findViewById(R.id.imageViewReport);
        tv_reminder = findViewById(R.id.tv_reminder);
        iv_nav_menu= findViewById(R.id.iv_nav_menu);
        tv_logout = findViewById(R.id.tv_logout);


       // tv_reminder= findViewById(R.id.tv_reminder);
        textViewTipsByDr = findViewById(R.id.tv_tips_by);
        image_tips = findViewById(R.id.image_tips);
        textViewTips = findViewById(R.id.tv_tips);
       // tv_logout = findViewById(R.id.tv_logout);

        final CoordinatorLayout content = findViewById(R.id.content);
        mDrawerLayout                   = findViewById(R.id.drawer_layout);
        navigationView                  =  findViewById(R.id.nav_view);
        navHeader                       = navigationView.getHeaderView(0);
        TextView txtName                =  navHeader.findViewById(R.id.user_name);
        TextView txtEmail               = navHeader.findViewById(R.id.user_email);
        navigationView.setItemIconTintList(null);
        txtEmail.setText(userEmail);
        txtName.setText(userName);

        /*********************Nav drawer push activity on opening****************/
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            private float scaleFactor = 10f;
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                float slideX = drawerView.getWidth() * slideOffset;
                content.setTranslationX(slideX);
                content.setScaleX(1 - (slideOffset / scaleFactor));
                content.setScaleY(1 - (slideOffset / scaleFactor));
            }
        };

        mDrawerLayout.setScrimColor(Color.TRANSPARENT);
        mDrawerLayout.setDrawerElevation(0f);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        Glide.with(this).load(R.raw.icon_tip).into(image_tips);
        navigationView.setNavigationItemSelectedListener(this);
        imageMedicalService.setOnClickListener(this);
        imageMakeAnQuery.setOnClickListener(this);
        imageBookApntmnt.setOnClickListener(this);
        imageViewReport.setOnClickListener(this);
        dialog = new SpotsDialog(this);
        iv_nav_menu.setOnClickListener(this);
        tv_reminder.setOnClickListener(this);
        tv_logout.setOnClickListener(this);

        /**********Get health tips**************/
        getTips(Webservice.GET_HEALTH_TIPS);

    }

    /***************Get health tips**************/
    private void getTips(String getHealthTips) {
        dialog.show();
        JsonObjectRequest requestHealthTips = new JsonObjectRequest(Request.Method.GET, getHealthTips, null, response -> {
            dialog.dismiss();

            try {
                tip_subject = response.getString("tip_subject");
                tip = response.getString("tip").trim();
                suggest_by = response.getString("suggest_by");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            textViewTipSubject.setText(tip_subject);
            textViewTips.setText(tip);
            textViewTipsByDr.setText(suggest_by);

        }, error -> Utils.showMessage(getApplicationContext(), error.toString()));
        AppController.getInstance().addToRequestQueue(requestHealthTips);
        requestHealthTips.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
              case R.id.iv_nav_menu:
                  if (!mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                      mDrawerLayout.openDrawer(GravityCompat.START);
                  } else {
                      mDrawerLayout.closeDrawer(GravityCompat.END);
                  }
                break;

            case R.id.tv_logout:
                logout();
                break;

            case R.id.tv_reminder:
                setReminder();
                break;
            case R.id.imageMedicalService:
                //loadFragment(new MedicalServiceHomeFragment());
                Utils.switchActivity(this,new  MedicalServiceActivity());
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finishAffinity();
                break;
            case R.id.imageMakeAnQuery:
                Utils.switchActivity(this, new MakeQueryActivity());
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finishAffinity();
                break;
            case R.id.imageBookApntmnt:
                SharedPreferences.Editor editor = getSharedPreferences(MySharedPref.FLAG_PAGE_REDIRECTION, MODE_PRIVATE).edit();
                editor.putBoolean("flag_from_mServices", false);
                editor.apply();

                Utils.switchActivity(this,new DepartmentListActivity_BookingStep_1());
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finishAffinity();
                break;
            case R.id.imageViewReport:
                Utils.switchActivity(getApplicationContext(),new ViewReportActivity());
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                finishAffinity();

        }

    }

    /***************Reminder setting*************/
    private void setReminder() {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.item/event");

        Calendar cal = Calendar.getInstance();
        long startTime = cal.getTimeInMillis();
        long endTime = cal.getTimeInMillis() + 60 * 60 * 1000;

        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime);
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime);
        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);

        intent.putExtra(CalendarContract.Events.TITLE, "Set Medicine time");
        intent.putExtra(CalendarContract.Events.DESCRIPTION, "Medicine description");
        intent.putExtra(CalendarContract.Events.RRULE, "FREQ=YEARLY");

        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout){
            logout();

        }
        return super.onOptionsItemSelected(item);

    }

    private void logout() {
        new FancyAlertDialog.Builder(this)
                .setTitle("Are you sure?")
                .setBackgroundColor(Color.parseColor("#ffffff"))
                .setMessage("Logout?")
                .setNegativeBtnText("No")
                .setPositiveBtnBackground(Color.parseColor("#ba0b0b"))
                .setPositiveBtnText("Yes")
                .setNegativeBtnBackground(Color.parseColor("#3cbf28"))
                .setAnimation(Animation.POP)
                .isCancellable(true)
                .setIcon(R.drawable.icon_warning, Icon.Visible)
                .OnPositiveClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                        SharedPreferences preferences = getSharedPreferences(SHARED_PREF_NAME,
                                Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean(MySharedPref.LOGGEDIN_SHARED_PREF, false);
                        editor.putString(USERNAME_SHARED_PREF, "");
                        editor.apply();

                        Utils.switchActivity(getApplicationContext(), new LoginActivity());
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                })

                .OnNegativeClicked(() -> {

                })
                .build();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        if (id==R.id.nav_booking_history){
            drawer.closeDrawer(GravityCompat.START);
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    Utils.switchActivity(MainActivity.this,new BookingHistoryActivity());
                    overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                }
            });

        }

        return false;
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
 /*   private void loadFragment(Fragment currentFragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, currentFragment);
        fragmentTransaction.commit();
    }*/
}
