package com.spandan.dextro.spandan;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.spandan.dextro.spandan.Model.ModelCenter;
import com.spandan.dextro.spandan.Util.AppController;
import com.spandan.dextro.spandan.Util.MySharedPref;
import com.spandan.dextro.spandan.Util.Utils;
import com.spandan.dextro.spandan.Webservices.Webservice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import dmax.dialog.SpotsDialog;

public class CenterSelectionActivity_BookingStep_2 extends AppCompatActivity {

    private ArrayList<ModelCenter> modelCenters;
    private RecyclerView rcv_center;
    private JSONObject post_parameters;
    private AlertDialog dialog;
    private RecyclerView.Adapter mAdapter;
    private boolean flagpage_action_from;
    private String getDepartment;
    private ImageView iv_btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center_selection);

        SharedPreferences prefs = getSharedPreferences(MySharedPref.FLAG_PAGE_REDIRECTION, MODE_PRIVATE);
        flagpage_action_from = prefs.getBoolean("flag_page_from_opd", false);
        getDepartment = prefs.getString("sfDepartment", "No dept");

        iv_btn_back=findViewById(R.id.iv_btn_back);
        iv_btn_back.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), DepartmentListActivity_BookingStep_1.class));
            finishAffinity();
        });

       // Log.e("GotDeptId::",getDepartment);

      /*  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Select Center");

        toolbar.setTitleTextColor(getResources().getColor(R.color.color_white));
        Objects.requireNonNull(getSupportActionBar()).setHomeButtonEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(view -> {

            startActivity(new Intent(getApplicationContext(), DepartmentListActivity_BookingStep_1.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finishAffinity();

        });*/
        rcv_center = findViewById(R.id.rcv_medical_center);
        rcv_center.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rcv_center.setLayoutManager(layoutManager);
        dialog = new SpotsDialog(this);

        try {
            post_parameters = new JSONObject();
            post_parameters.put("deptId", getDepartment);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        getCenterList(Webservice.GET_CENTER_BY_DEPT_ID, post_parameters);

    }

    private void getCenterList(String getCenterByDeptId, JSONObject post_parameters) {
        dialog.show();
        JsonObjectRequest requestAllDepartment = new JsonObjectRequest(Request.Method.POST, getCenterByDeptId, post_parameters,
                response -> {
                    try {

                        if (response.getString("success").equalsIgnoreCase("1")) {
                           // Log.e("CenterResponse::", response.toString());
                            dialog.dismiss();
                            modelCenters = new ArrayList<>();

                            JSONArray array = response.getJSONArray("centerDetails");
                            for (int i = 0; i < array.length(); i++) {
                                ModelCenter centerData = new ModelCenter();

                                JSONObject object = array.getJSONObject(i);
                                centerData.setCenter_enquiry_help_desk(object.getString("center_enquiry_help_desk"));
                                centerData.setCenter_address(object.getString("center_address"));
                                centerData.setCenterName(object.getString("center_name"));
                                centerData.setCenter_type(object.getString("center_type"));
                                centerData.setCenterId(object.getString("id"));

                                modelCenters.add(centerData);


                            }
                            mAdapter = new MyAdapter(getApplicationContext(), modelCenters);
                            rcv_center.setAdapter(mAdapter);

                        }else {
                            dialog.dismiss();
                            Utils.showMessage(getApplicationContext(),response.getString("message"));
                            Utils.switchActivity(getApplicationContext(),new DepartmentListActivity_BookingStep_1());
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }, error -> Utils.showMessage(getApplicationContext(), error.toString()));
        AppController.getInstance().addToRequestQueue(requestAllDepartment);
        requestAllDepartment.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
/***************Home navigate*********/
    public void backHome(View view) {
        Utils.switchActivity(getApplicationContext(),new MainActivity());
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finishAffinity();
    }

    /***********************Center list implementation to Recyclerview**************/
    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private Context context;
        private ArrayList<ModelCenter> centerArrayList;

        public MyAdapter(Context applicationContext, ArrayList<ModelCenter> modelCenters) {
            this.context = applicationContext;
            this.centerArrayList = modelCenters;
        }

        @NonNull
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_center, parent, false);
            return new MyAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder, int position) {
            holder.itemView.setTag(centerArrayList.get(position));
            ModelCenter list = centerArrayList.get(position);

            holder.tv_center_name.setText(list.getCenterName());
            holder.tv_center_address.setText(list.getCenter_address());
            holder.tv_helpdesk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    permitAndcallHelpDesk(list.getCenter_enquiry_help_desk());
                }

                private void permitAndcallHelpDesk(String contact) {

                    if (Utils.checkAndroidRuntimePermission(CenterSelectionActivity_BookingStep_2.this, Manifest.permission.CALL_PHONE, 100, "CALL Permission Required", "To make a phone call, please allow the Call permission in the next dialog.")) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + contact));//change the number
                        startActivity(callIntent);
                    }
                }
            });

            if (list.getCenter_type().equalsIgnoreCase("H")){
                holder.tv_center_type.setText("H O S P I T A L");
            }else {
                holder.tv_center_type.setText("D I A G N O S T I C");
            }


        }

        @Override
        public int getItemCount() {
            return centerArrayList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private TextView tv_center_name;
            private TextView tv_center_address;
            private TextView tv_helpdesk;
            private TextView tv_center_type;

            public ViewHolder(View itemView) {
                super(itemView);
                tv_center_name = itemView.findViewById(R.id.tv_center_name);
                tv_center_address = itemView.findViewById(R.id.tv_center_address);
                tv_helpdesk = itemView.findViewById(R.id.tv_help_desk);
                tv_center_type = itemView.findViewById(R.id.tv_center_type);

                itemView.setOnClickListener(view -> {
                    ModelCenter model = (ModelCenter) view.getTag();
                    Log.e("CenterId::", model.getCenterId());

                    SharedPreferences.Editor editor = context.getSharedPreferences(MySharedPref.FLAG_PAGE_REDIRECTION, MODE_PRIVATE).edit();
                    // editor.putBoolean("flag_page_from_opd", true);
                    editor.putString("sfCenter", model.getCenterId());
                    editor.apply();
                    startActivity(new Intent(getApplicationContext(), DoctorListAndSearchActivity_BookingStep_3.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                });
            }


        }
    }

    @Override
    public void onBackPressed() {
            Utils.switchActivity(getApplicationContext(),new DepartmentListActivity_BookingStep_1());
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finishAffinity();
    }
}
