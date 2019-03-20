package com.example.dms.activity.homepage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.dms.R;
import com.example.dms.activity.student.student_work.StudentLeavewordActivity;
import com.example.dms.activity.student.student_work.StudentMessageActivity;
import com.example.dms.activity.student.student_work.UploadphotoActivity;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.vmloft.develop.library.tools.utils.VMLog;
import com.vmloft.develop.library.tools.utils.VMSPUtil;

import static com.example.dms.activity.common.LoginActivity.gs_loginUser;
import static com.example.dms.util.Encryption.md5;


public class HomePageFragment extends Fragment
{

    private String TAG = "HomePageFragment";
    private Button btn_receieve;
    private Button btn_checkNotice;
    private Button btn_leave_word;


    private void loginhyphenatechat()
    {
        String S_ID = gs_loginUser.getsId();
        if (!gs_loginUser.getsId().equals(""))
        {
            final String password = md5("shabi_wangluyao");
            StringBuffer sb = null;
            int len = S_ID.length();
            while (len < 12) {
                sb = new StringBuffer();
                sb.append("0").append(S_ID);
                S_ID = sb.toString();
                len = S_ID.length();
            }
            S_ID = "s" + S_ID;
            final String account = S_ID;
            EMClient.getInstance().login(account, password, new EMCallBack() {
                @Override
                public void onSuccess() {
                    VMLog.i("login success");
                    VMSPUtil.put("username", account);
                    VMSPUtil.put("password", password);
                }

                @Override
                public void onError(final int i, final String s)
                {
                    String errorMsg = "login error: " + i + "; " + s;
                    VMLog.i(errorMsg);
                }
                @Override
                public void onProgress(int i, String s) {
                }
            });
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginhyphenatechat();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btn_receieve = getActivity().findViewById(R.id.btn_receieve);
        btn_checkNotice = getActivity().findViewById(R.id.btn_check_notice);
        btn_leave_word = getActivity().findViewById(R.id.btn_leave_word);

        btn_receieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),UploadphotoActivity.class);
                startActivity(intent);
            }
        });
        btn_checkNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),StudentMessageActivity.class);
                startActivity(intent);
            }
        });
        btn_leave_word.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发送留言
                Intent intent = new Intent(getActivity(),StudentLeavewordActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        return view;
    }
}
