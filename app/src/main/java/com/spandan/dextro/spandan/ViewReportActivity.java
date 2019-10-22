package com.spandan.dextro.spandan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;
import com.spandan.dextro.spandan.Model.ModelDepartment;
import com.spandan.dextro.spandan.Model.ModelReport;
import com.spandan.dextro.spandan.Util.AppController;
import com.spandan.dextro.spandan.Util.MySharedPref;
import com.spandan.dextro.spandan.Util.Utils;
import com.spandan.dextro.spandan.Webservices.Webservice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import dmax.dialog.SpotsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static com.spandan.dextro.spandan.Util.MySharedPref.USERNAME_SHARED_PREF;

public class ViewReportActivity extends AppCompatActivity {

    private ArrayList<ModelReport> modelReports = new ArrayList<>();
    private RecyclerView rcv_report_list;
    private JSONObject post_parameters;
    private AlertDialog dialog;
    private RecyclerView.Adapter mAdapter;
    private String getPatientId;
    private static final String TAG = ViewReportActivity.class.getSimpleName();
    private ImageView iv_btn_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report);

        SharedPreferences prefs = getSharedPreferences(MySharedPref.SHARED_PREF_NAME, MODE_PRIVATE);
        getPatientId=prefs.getString(MySharedPref.USER_ID,"No id");
        Log.e("PatientID::",getPatientId);
        iv_btn_back=findViewById(R.id.iv_btn_back);
        iv_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.switchActivity(getApplicationContext(),new MainActivity());
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finishAffinity();
            }
        });

        rcv_report_list = findViewById(R.id.rcv_medical_report);
        rcv_report_list.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rcv_report_list.setLayoutManager(layoutManager);
        dialog = new SpotsDialog(this);

        try {
            post_parameters = new JSONObject();
            post_parameters.put("patientID",getPatientId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getReport(Webservice.GET_REPORTS,post_parameters);

    }
/**********************Get Report api calling*****************/
    private void getReport(String getReports, JSONObject post_parameters) {
        dialog.show();
        JsonObjectRequest requestreport = new JsonObjectRequest(Request.Method.POST, getReports, post_parameters, response -> {
            dialog.dismiss();
            //Log.e("ReportResp::",response.toString());
            try {

                if (response.getString("success").equalsIgnoreCase("1")) {
                    Log.e("DocResponse::", response.toString());
                    dialog.dismiss();
                    JSONArray array = response.getJSONArray("reportDetails");
                    for (int i = 0; i < array.length(); i++) {
                        ModelReport modelData = new ModelReport();

                        JSONObject object = array.getJSONObject(i);

                        modelData.setReportCategory(object.getString("report_category"));
                        modelData.setReportGenDate(object.getString("report_date"));
                        modelData.setReportFilePath(object.getString("report_file"));

                        modelReports.add(modelData);

                    }
                    mAdapter = new MyAdapter(getApplicationContext(),modelReports);
                    rcv_report_list.setAdapter(mAdapter);
                }else {
                    Utils.myDialogSingleButton(this, new MainActivity(), "No report found!", "Back to home?", "Yes");

                  //  Utils.showMessage(getApplicationContext(),"No report found!");

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();

                Utils.switchActivity(getApplicationContext(),new MainActivity());
            }
        });
        AppController.getInstance().addToRequestQueue(requestreport);
        requestreport.setRetryPolicy(new DefaultRetryPolicy(0, -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    public void onBackPressed() {
        Utils.switchActivity(getApplicationContext(),new MainActivity());
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finishAffinity();
    }

    public void backToMain(View view) {
        Utils.switchActivity(getApplicationContext(),new MainActivity());
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finishAffinity();
    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private Context context;
        private ArrayList<ModelReport> reportArrayList;

        public MyAdapter(Context context, ArrayList<ModelReport> modelReports) {
            this.context = context;
            this.reportArrayList = modelReports;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_report, parent, false);
            return new MyAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.itemView.setTag(reportArrayList.get(position));
            ModelReport list = reportArrayList.get(position);
            holder.tv_report_name.setText(list.getReportCategory());
            holder.tv_report_date.setText(list.getReportGenDate());

            holder.iv_download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String fileUrl=Webservice.VIEW_REPORT_PDF+list.getReportFilePath();
                    Log.e("FilePath",fileUrl);
                    new DownloadFile().execute(fileUrl);
                }
            });
        }

        @Override
        public int getItemCount() {
            return reportArrayList.size();
        }


        class ViewHolder extends RecyclerView.ViewHolder  {

            private TextView tv_report_name;
            private TextView tv_report_date;
            private ImageView iv_download;

            public ViewHolder(View itemView) {
                super(itemView);
                tv_report_name = itemView.findViewById(R.id.tv_report_name);
                tv_report_date = itemView.findViewById(R.id.tv_report_date);
                iv_download = itemView.findViewById(R.id.iv_download);

                itemView.setOnClickListener(view -> {
                    ModelReport model = (ModelReport) view.getTag();
                   // Log.e("FilePath::",model.getReportFilePath());
                    Intent intentViewPdf = new Intent(getApplicationContext(),ReportPdfViewActivity.class);
                    intentViewPdf.putExtra("pdfPath",Webservice.VIEW_REPORT_PDF+model.getReportFilePath());
                    startActivity(intentViewPdf);

                });
            }

        }

        /**
         * Async Task to download file from URL
         */
        private class DownloadFile extends AsyncTask<String, String, String> {

            private ProgressDialog progressDialog;
            private String fileName;
            private String folder;
            private boolean isDownloaded;

            /**
             * Before starting background thread
             * Show Progress Bar Dialog
             */
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                this.progressDialog = new ProgressDialog(ViewReportActivity.this);
                this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                this.progressDialog.setCancelable(false);
                this.progressDialog.show();
            }

            /**
             * Downloading file in background thread
             */
            @Override
            protected String doInBackground(String... file_url) {
                int count;
                try {
                    URL url = new URL(file_url[0]);
                    URLConnection connection = url.openConnection();
                    connection.connect();
                    // getting file length
                    int lengthOfFile = connection.getContentLength();


                    // input stream to read file - with 8k buffer
                    InputStream input = new BufferedInputStream(url.openStream(), 8192);

                    String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

                    //Extract file name from URL
                    fileName = file_url[0].substring(file_url[0].lastIndexOf('/') + 1);

                    //Append timestamp to file name
                    fileName = timestamp + "_" + fileName;

                    //External directory path to save file
                    folder = Environment.getExternalStorageDirectory() + File.separator + "Medical report/";

                    //Create Medical report folder if it does not exist
                    File directory = new File(folder);

                    if (!directory.exists()) {
                        directory.mkdirs();
                    }

                    // Output stream to write file
                    OutputStream output = new FileOutputStream(folder + fileName);

                    byte data[] = new byte[1024];

                    long total = 0;

                    while ((count = input.read(data)) != -1) {
                        total += count;
                        // publishing the progress....
                        // After this onProgressUpdate will be called
                        publishProgress("" + (int) ((total * 100) / lengthOfFile));
                        Log.d(TAG, "Progress: " + (int) ((total * 100) / lengthOfFile));

                        // writing data to file
                        output.write(data, 0, count);
                    }

                    // flushing output
                    output.flush();

                    // closing streams
                    output.close();
                    input.close();
                    //return "Downloaded at: " + folder + fileName;
                    return folder + fileName;

                } catch (Exception e) {
                    Log.e("Error: ", e.getMessage());
                }

                return "Something went wrong";
            }

            /**
             * Updating progress bar
             */
            protected void onProgressUpdate(String... progress) {
                // setting progress percentage
                progressDialog.setProgress(Integer.parseInt(progress[0]));
            }


            @Override
            protected void onPostExecute(String message) {
                this.progressDialog.dismiss();

                new FancyAlertDialog.Builder(ViewReportActivity.this)
                        .setTitle("Download success")
                        .setBackgroundColor(Color.parseColor("#ffffff"))
                        .setMessage("Downloaded at-"+message)
                        .setNegativeBtnText("No")
                        .setPositiveBtnBackground(Color.parseColor("#ba0b0b"))
                        .setPositiveBtnText("Go Home")
                        .setNegativeBtnBackground(Color.parseColor("#3cbf28"))
                        .setAnimation(Animation.POP)
                        .isCancellable(true)
                        .setIcon(R.drawable.icon_warning, Icon.Visible)
                        .OnPositiveClicked(new FancyAlertDialogListener() {
                            @Override
                            public void OnClick() {

                                Utils.switchActivity(ViewReportActivity.this, new MainActivity());
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                finish();
                            }
                        })
                        /* .OnPositiveClicked(this::UnregisterToken

                         )*/
                        .OnNegativeClicked(() -> {

                        })
                        .build();
            }
        }
    }

}
