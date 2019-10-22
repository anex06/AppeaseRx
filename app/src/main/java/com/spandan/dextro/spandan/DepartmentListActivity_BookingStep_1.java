package com.spandan.dextro.spandan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.spandan.dextro.spandan.Model.ModelCenter;
import com.spandan.dextro.spandan.Model.ModelDepartment;
import com.spandan.dextro.spandan.Model.ModelDoctor;
import com.spandan.dextro.spandan.Util.AppController;
import com.spandan.dextro.spandan.Util.MySharedPref;
import com.spandan.dextro.spandan.Util.OnQueryResult;
import com.spandan.dextro.spandan.Util.PrefixTree;
import com.spandan.dextro.spandan.Util.Utils;
import com.spandan.dextro.spandan.Webservices.Webservice;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import dmax.dialog.SpotsDialog;

public class DepartmentListActivity_BookingStep_1 extends AppCompatActivity implements View.OnClickListener, OnQueryResult<ModelDepartment> {

    private ArrayList<ModelDepartment> modelDepartments = new ArrayList<>();
    private RecyclerView rcv_department_list;
    private JSONObject post_parameters;
    private AlertDialog dialog;
    private MyAdapter mAdapter;
    private boolean flagpage_action_from;
    private ImageView iv_btn_back;
    private EditText et_search_department;
    private PrefixTree<ModelDepartment> searchQueryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_list);
        searchQueryList= new PrefixTree<>(this);

        SharedPreferences prefs = getSharedPreferences(MySharedPref.FLAG_PAGE_REDIRECTION, MODE_PRIVATE);
        flagpage_action_from= prefs.getBoolean("flag_from_mServices", false);

       // Log.e("FromMedicalServices::",String.valueOf(flagpage_action_from));
        iv_btn_back=findViewById(R.id.iv_btn_back);
        iv_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flagpage_action_from){
                    Utils.switchActivity(getApplicationContext(),new MedicalServiceActivity());
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finishAffinity();
                }else {
                    Utils.switchActivity(getApplicationContext(),new MainActivity());
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finishAffinity();
                }
            }
        });
        et_search_department = findViewById(R.id.et_search_department);
        rcv_department_list = findViewById(R.id.rcv_medical_department);
        rcv_department_list.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this,2);
        rcv_department_list.setLayoutManager(layoutManager);
        dialog = new SpotsDialog(this);

        getAllDepartment(Webservice.GET_ALL_DEPARTMENTS);

        et_search_department.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable queryString) {
                searchQueryList.query(queryString.toString().toLowerCase());
            }
        });
    }

    /*****************All department list**************/
    private void getAllDepartment(String getAllDeptUrl) {
        dialog.show();
        modelDepartments.clear();
        JsonObjectRequest requestAllDepartment =  new JsonObjectRequest(Request.Method.GET, getAllDeptUrl, null,
                response -> {
                    try {

                        if (response.getString("success").equalsIgnoreCase("1")) {
                            Log.e("DocResponse::", response.toString());
                            dialog.dismiss();
                            JSONArray array = response.getJSONArray("data");
                            for (int i = 0; i < array.length(); i++) {
                                ModelDepartment departmentData = new ModelDepartment();

                                JSONObject object = array.getJSONObject(i);
                                departmentData.setDeptName(object.getString("dept_name"));
                                departmentData.setDeptId(object.getString("id"));
                                departmentData.setDeptImage(object.getString("dept_icon"));
                               /* departmentData.setDeptBanner(object.getString("dept_banner"));*/
                                modelDepartments.add(departmentData);
                                searchQueryList.insert(departmentData.getDeptName(),departmentData);

                            }
                            mAdapter = new MyAdapter(getApplicationContext(),modelDepartments);
                            rcv_department_list.setAdapter(mAdapter);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utils.showMessage(getApplicationContext(),error.toString());
            }
        });
        AppController.getInstance().addToRequestQueue(requestAllDepartment);
        requestAllDepartment.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }
    @Override
    public void onResult(ArrayList<ModelDepartment> result) {
        Log.e("FilteredResult::",""+result.size());
        mAdapter.changeDataSet(result);

    }
    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private Context context;
        private ArrayList<ModelDepartment> departmentLists;

        public MyAdapter(Context context, ArrayList<ModelDepartment> departmentLists) {
            this.context = context;
            this.departmentLists = departmentLists;
        }
        public void changeDataSet(ArrayList<ModelDepartment> newDept){
            this.departmentLists = newDept;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_department, parent, false);
            return new MyAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder, int position) {
            holder.itemView.setTag(departmentLists.get(position));
            ModelDepartment list = departmentLists.get(position);
            holder.tv_dept_name.setText(list.getDeptName());

                Picasso.with(getApplicationContext()).load(departmentLists.get(position).getDeptImage())
                        .error(R.drawable.icon_defimage)
                        .placeholder(R.drawable.icon_defimage)
                        .error(R.drawable.icon_defimage)
                        //.resize(200,220)//Customize the image size
                        .fit()//Fit to the imageview attributes (height and width)
                        .placeholder(R.drawable.icon_doctor)
                        .into(holder.iv_dept_icon);


        }

        @Override
        public int getItemCount() {
            return departmentLists.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private TextView tv_dept_name;
            private ImageView iv_dept_icon;

            public ViewHolder(View itemView) {
                super(itemView);
                tv_dept_name = itemView.findViewById(R.id.tv_department);
                iv_dept_icon = itemView.findViewById(R.id.iv_dept);

                itemView.setOnClickListener(view -> {
                    ModelDepartment model = (ModelDepartment) view.getTag();
                    Log.e("DeptId::",model.getDeptId());

                    SharedPreferences.Editor editor = context.getSharedPreferences(MySharedPref.FLAG_PAGE_REDIRECTION, MODE_PRIVATE).edit();
                   //editor.putBoolean("flag_page_from_opd", true);
                    editor.putString("sfDepartment",model.getDeptId());
                    editor.apply();
                    startActivity(new Intent(DepartmentListActivity_BookingStep_1.this, CenterSelectionActivity_BookingStep_2.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finishAffinity();
                });
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (flagpage_action_from){
            Utils.switchActivity(getApplicationContext(),new MedicalServiceActivity());
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finishAffinity();
        }else {
            Utils.switchActivity(getApplicationContext(),new MainActivity());
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finishAffinity();
        }
    }
}