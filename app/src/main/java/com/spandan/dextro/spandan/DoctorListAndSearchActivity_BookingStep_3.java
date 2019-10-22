package com.spandan.dextro.spandan;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
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

import dmax.dialog.SpotsDialog;

public class DoctorListAndSearchActivity_BookingStep_3 extends AppCompatActivity implements OnQueryResult<ModelDoctor> {

    private ArrayList<ModelDoctor> modelDoctorLists;
    private RecyclerView rcv_doctor_list;
    private JSONObject post_parameters;
    private AlertDialog dialog;
    private MyAdapter mAdapter;
    private boolean flagpage_action_from;
    private String  getDepartment,getCenter;
    private String doctorDetails;
    private EditText editTextSearchDoctor;
    private ImageView iv_btn_back;
    PrefixTree<ModelDoctor> searchQueryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list_and_search);

        searchQueryList= new PrefixTree<>(this);

        editTextSearchDoctor= findViewById(R.id.et_search_department);
        rcv_doctor_list = findViewById(R.id.rcv_doctor_list);

        modelDoctorLists = new ArrayList<>();
        rcv_doctor_list.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rcv_doctor_list.setLayoutManager(layoutManager);
        dialog = new SpotsDialog(this);

        SharedPreferences prefs = getSharedPreferences(MySharedPref.FLAG_PAGE_REDIRECTION, MODE_PRIVATE);
        flagpage_action_from= prefs.getBoolean("flag_page_from_opd", false);

        getDepartment=prefs.getString("sfDepartment","No dept");
        getCenter = prefs.getString("sfCenter","No center");

        iv_btn_back=findViewById(R.id.iv_btn_back);
        iv_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.switchActivity(getApplicationContext(),new CenterSelectionActivity_BookingStep_2());
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finishAffinity();
            }
        });


        post_parameters = new JSONObject();
        try {
            post_parameters.put("deptId",getDepartment);
            post_parameters.put("centerId",getCenter);
            getDoctorList(Webservice.GET_DOCTORS_BY_DEPT,post_parameters);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        editTextSearchDoctor.addTextChangedListener(new TextWatcher() {
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

    /*****************Get all the doctors of particular type*******************/
    private void getDoctorList(String doctorList, JSONObject post_parameters) {
        dialog.show();

        JsonObjectRequest requestDocDetails = new JsonObjectRequest(Request.Method.POST, doctorList,
                post_parameters, response -> {
            dialog.dismiss();
            modelDoctorLists.clear();
            Log.e("DocResponse::",response.toString());
            try {

                if (response.getString("success").equalsIgnoreCase("1")) {

                    JSONArray array = response.getJSONArray("doctorDetails");

                    for (int i = 0; i < array.length(); i++) {
                        ModelDoctor doctorData = new ModelDoctor();

                        JSONObject object = array.getJSONObject(i);

                        doctorData.setDoctorName(object.getString("doctor_name"));
                        doctorData.setDocId(object.getString("doctor_id"));
                        doctorData.setDocFees(object.getString("doctor_fees"));
                        doctorData.setDoctorImage(object.getString("doctor_image"));
                     /* doctorData.setDocExperience(object.getDouble("doctor_experience"));
                        doctorData.setDocAddress(object.getString("doctor_address"));
                        doctorData.setDocFees(object.getDouble("doctor_fees"));
                        doctorData.setDocFeedBack(object.getDouble("doctor_feedback"));
                        doctorData.setDocNextVisitDay(object.getString("doctor_next_day"));*/
                        doctorData.setDocQualification(object.getString("doctor_qualification"));

                        modelDoctorLists.add(doctorData);
                        searchQueryList.insert(doctorData.getDoctorName(),doctorData);
                    }
                    mAdapter = new MyAdapter(this, modelDoctorLists);
                    rcv_doctor_list.setAdapter(mAdapter);

                } else {
                    Utils.showMessage(getApplicationContext(), "No data found!");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Utils.showMessage(getApplicationContext(),error.toString());
            Utils.switchActivity(getApplicationContext(),new CenterSelectionActivity_BookingStep_2());
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finishAffinity();
        });
        AppController.getInstance().addToRequestQueue(requestDocDetails);
        requestDocDetails.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }


    @Override
    public void onResult(ArrayList<ModelDoctor> result) {
        Log.e("FilteredResult::",""+result.size());
        mAdapter.changeDataSet(result);

    }
/*****************To home*******************/
    public void backHome(View view) {
        Utils.switchActivity(getApplicationContext(),new MainActivity());
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finishAffinity();
    }

    /**********************Doctor list recyclerview****************************/
    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private Context context;
        private ArrayList<ModelDoctor> doctorLists;

        public MyAdapter(DoctorListAndSearchActivity_BookingStep_3 context, ArrayList<ModelDoctor> modelDoctorLists) {
            this.context = context;
            this.doctorLists = modelDoctorLists;

        }

        public void changeDataSet(ArrayList<ModelDoctor> newDoctors){
            this.doctorLists = newDoctors;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_doctor, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.itemView.setTag(doctorLists.get(position));
            ModelDoctor list = doctorLists.get(position);

            holder.tv_doc_name.setText(list.getDoctorName());
            holder.tv_qualification.setText(list.getDocQualification());
            holder.tv_fees_label.setText(list.getDocFees());


            Picasso.with(getApplicationContext()).load(doctorLists.get(position).getDoctorImage())
                    .error(R.drawable.icon_doctor)
                    .placeholder(R.drawable.icon_doctor)
                    .error(R.drawable.icon_doctor)
                    //.resize(200,220)//Customize the image size
                    .fit()//Fit to the imageview attributes (height and width)
                    .placeholder(R.drawable.icon_doctor)
                    .into(holder.iv_doc_img);

           /* holder.tv_experience_label.setText(String.valueOf(list.getDocExperience()));
            holder.tv_doc_address.setText(list.getDocAddress());
            holder.tv_fees_label.setText(String.valueOf(list.getDocFees()));
            holder.tv_feedback_label.setText(String.valueOf(list.getDocFeedBack())+"%");
            holder.tv_next_visit.setText(list.getDocNextVisitDay());*/

        }

        @Override
        public int getItemCount() {
            return doctorLists.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private TextView tv_doc_name;
            private TextView tv_experience_label;
            private TextView tv_qualification;
            private TextView tv_doc_address;
            private TextView tv_fees_label;
            private TextView tv_next_visit;
            private TextView tv_feedback_label;
            private Button btn_contact;
            private Button btn_visit;
            private ImageView iv_doc_img;


            public ViewHolder(View itemView) {
                super(itemView);

                tv_doc_name = itemView.findViewById(R.id.tv_doc_name);
                //tv_experience_label = itemView.findViewById(R.id.tv_experience_label);
                tv_qualification = itemView.findViewById(R.id.tv_qualification);
                tv_fees_label = itemView.findViewById(R.id.tv_fees_label);
                iv_doc_img= itemView.findViewById(R.id.iv_doc_img);
               /* tv_doc_address = itemView.findViewById(R.id.tv_doc_address);
                tv_fees_label = itemView.findViewById(R.id.tv_fees_label);
                tv_next_visit = itemView.findViewById(R.id.tv_next_visit);
                tv_feedback_label = itemView.findViewById(R.id.tv_feedback_label);
                btn_contact = itemView.findViewById(R.id.btn_contact);
                btn_visit = itemView.findViewById(R.id.btn_visit);*/

                itemView.setOnClickListener(view -> {
                    ModelDoctor model = (ModelDoctor) view.getTag();

                    Gson gson = new Gson();
                    doctorDetails = gson.toJson(model);
                    MySharedPref.saveString(getApplicationContext(), doctorDetails, MySharedPref.DOCTOR_DATA);


                    startActivity(new Intent(getApplicationContext(), DoctorFullDetailsActivity_BookingStep_4.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finishAffinity();

                });
            }
        }
    }
    @Override
    public void onBackPressed() {

            Utils.switchActivity(getApplicationContext(),new CenterSelectionActivity_BookingStep_2());
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finishAffinity();
    }
}
