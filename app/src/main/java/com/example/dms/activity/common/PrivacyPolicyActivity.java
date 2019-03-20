package com.example.dms.activity.common;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.dms.R;
import com.example.dms.activitycontainer.BaseActivity;

import org.w3c.dom.Text;

public class PrivacyPolicyActivity extends BaseActivity {

    TextView text_privacyplicy;

    Toolbar tbl_privacy_policy;

    private void setToolBar()
    {
        tbl_privacy_policy = findViewById(R.id.tlb_privvacy_police);
        setSupportActionBar(tbl_privacy_policy);
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
        setContentView(R.layout.activity_privacy_policy);

        text_privacyplicy = findViewById(R.id.text_privacypolicy);
        text_privacyplicy.setMovementMethod(ScrollingMovementMethod.getInstance());

        setToolBar();
    }
}
