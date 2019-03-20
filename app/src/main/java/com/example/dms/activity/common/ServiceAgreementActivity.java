package com.example.dms.activity.common;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.dms.R;
import com.example.dms.activitycontainer.BaseActivity;

public class ServiceAgreementActivity extends BaseActivity {

    Toolbar tbl_serviceagreement;

    TextView text_serviceagreement;

    private void setToolBar()
    {
        tbl_serviceagreement = findViewById(R.id.tlb_serviece);
        setSupportActionBar(tbl_serviceagreement);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_agreement);

        text_serviceagreement =findViewById(R.id.text_serviceagreement);

        setToolBar();
    }
}
