package com.example.dms.activity.homepage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.example.dms.R;
import com.example.dms.activity.common.LoginActivity;
import com.example.dms.activity.teacher.ItemView;
import com.example.dms.activity.useinfo.SettingActivity;
import com.example.dms.activity.useinfo.TeacherInfoActivity;
import com.example.dms.activity.useinfo.UserInfoActivity;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.example.dms.activity.common.LoginActivity.gs_loginUser;
import static com.example.dms.activity.common.LoginActivity.gt_loginUser;


public class PersonStudentInfoFragment extends Fragment {

    private ImageView blurImageView;
    private CircleImageView circleImageView;
    private ItemView iv_nickName;
    private ItemView iv_userinfo;
    private ItemView iv_usersetting;
    private ItemView iv_mly;
    private  Intent intent;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();


        Glide.with(getActivity()).load(R.drawable.ic_test).skipMemoryCache(false)
                .dontAnimate()
                .bitmapTransform(new BlurTransformation(getActivity(), 25), new CenterCrop(getActivity()))
                .into(blurImageView);

        Glide.with(getActivity()).load(R.drawable.ic_test).skipMemoryCache(false)
                .dontAnimate()
                .bitmapTransform(new CropCircleTransformation(getActivity()))
                .into(circleImageView);

        iv_nickName.setItemClickListener(new ItemView.itemClickListener() {
            @Override
            public void itemClick(String text) {


            }
        });
        iv_userinfo.setItemClickListener(new ItemView.itemClickListener() {
            @Override
            public void itemClick(String text) {
                if(gs_loginUser.isIsvalid())
                    intent = new Intent(getActivity(),UserInfoActivity.class);
                else if (gt_loginUser.isIsvalid())
                    intent =  new Intent(getActivity(),TeacherInfoActivity.class);

                startActivity(intent);
            }
        });
        iv_usersetting.setItemClickListener(new ItemView.itemClickListener() {
            @Override
            public void itemClick(String text) {
                Intent setIntent = new Intent(getActivity(),SettingActivity.class);
                startActivity(setIntent);
            }
        });
        iv_mly.setItemClickListener(new ItemView.itemClickListener() {
            @Override
            public void itemClick(String text) {
                Toast.makeText(getActivity(),"beta测试版，版本1.0，暂未发布，谢谢使用",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_person_student_info, container, false);
    }

    private void initView()
    {
          iv_nickName = getActivity().findViewById(R.id.item_nickname);
          int type = LoginActivity.wahtType();
          String name = null;
          if (type == 0)
              name = gt_loginUser.getUsername();
          else if (type == 1)
              name = gs_loginUser.getUsername();
          iv_nickName.setRightDesc(name);
          iv_userinfo = getActivity().findViewById(R.id.item_info);
          iv_usersetting = getActivity().findViewById(R.id.item_setting);
          iv_mly = getActivity().findViewById(R.id.item_czs);
          blurImageView = getActivity().findViewById(R.id.h_back);
          circleImageView = getActivity().findViewById(R.id.h_head);
//        tv_username.setText(gUser.getNickname());
    }
    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

}
