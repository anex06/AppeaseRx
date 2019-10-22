package com.spandan.dextro.spandan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.spandan.dextro.spandan.Util.Utils;

import java.util.Objects;

public class MakeQueryActivity extends AppCompatActivity {

    private ImageView iv_btn_back;
    private TextView tv_nxt_app_issue;
    private TextView tv_next_book;
    private TextView tv_next_query;
    private TextView tv_next_query1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_query);

        iv_btn_back=findViewById(R.id.iv_btn_back);
        tv_nxt_app_issue=findViewById(R.id.tv_nxt_app_issue);
        tv_next_book=findViewById(R.id.tv_next_book);
        tv_next_query=findViewById(R.id.tv_next_query);
        tv_next_query1=findViewById(R.id.tv_next_query1);

    }

/*********************Methods for raising any issue****************/
    public void appIssue(View view) {
        Utils.showMessage(getApplicationContext(),"Work in progress!");

    }

    public void bookdoctorIssue(View view) {
        Utils.showMessage(getApplicationContext(),"Work in progress!");
    }

    public void reportBillIssue(View view) {
        Utils.showMessage(getApplicationContext(),"Work in progress!");
    }

    public void anyQuery(View view) {
        Utils.showMessage(getApplicationContext(),"Work in progress!");
    }

    @Override
    public void onBackPressed() {

        Utils.switchActivity(getApplicationContext(),new MainActivity());
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        //finishAffinity();
    }

    public void backToMain(View view) {
        Utils.switchActivity(getApplicationContext(),new MainActivity());
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
