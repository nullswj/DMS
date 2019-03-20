package com.example.dms.activity.student.student_bind;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.baidu.location.LocationClientOption;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.dms.activity.common.LoginActivity;
import com.example.dms.activitycontainer.AtyContainer;
import com.example.dms.activitycontainer.BaseActivity;
import com.example.dms.util.GpsUtil;
import com.example.dms.util.getMainsocket;

import org.json.JSONException;
import org.json.JSONObject;
import com.example.dms.R;
import com.google.gson.JsonObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static com.example.dms.activity.common.LoginActivity.gs_loginUser;
import static com.example.dms.activity.common.LoginActivity.gt_loginUser;


/**
 * 文件名 BindingLocationActivity
 * 描述 绑定位置信息
 * 版本 1.0
 */
public class BindingLocationActivity extends BaseActivity implements View.OnClickListener
{


    private String TAG = "BindingLocationActivity";

    private Toolbar tlb_bindlocation;

    private Button btn_bind_getLocation;

    private Button btn_bind_sendLocation;

    /** mLocationClient是用来向服务器请求位置信息*/
    public LocationClient mLocationClient ;

    /** 百度的地图*/
    private BaiduMap baiduMap;

    /**地图的界面组件*/
    private MapView mapView;

    /** 判断线程是否结束*/
    private static volatile boolean m_isFinish = false;

    /**保存纬度信息*/
    private double m_Latitude;//

    /**保存经度信息*/
    private double m_Longitude;

    /**用于判断是不是第一次发送定位请求*/
    private boolean m_isFirstLocate = true;

    /**用于判断是不是已经获取了位置*/
    private boolean m_isGetLocation = false;

    private String m_send;

    private String m_s_id;

    private String m_user_name;

    private volatile boolean isM_isFinish = false;


    private void setToolBar()
    {
        tlb_bindlocation = findViewById(R.id.tlb_get_location);
        setSupportActionBar(tlb_bindlocation);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void showBcakDialog()
    {
        final AlertDialog.Builder bindDialog = new AlertDialog.Builder(BindingLocationActivity.this);
        bindDialog.setIcon(R.drawable.title);
        bindDialog.setTitle("照片绑定");
        bindDialog.setMessage("放弃未上传的位置信息，确认返回？");

        bindDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        bindDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        bindDialog.setCancelable(false);
        AlertDialog dialog = bindDialog.create();
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            showBcakDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            showBcakDialog();
        }
        return true;
    }

    /**
     * 描述 重写父类的onCreate()方法
     * 功能 加载界面布局
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());//位置信息的返回监听

        /* 初始化界面布局*/
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_bind_location);

        isOpenGps();

        Intent intent = getIntent();
        m_s_id = intent.getStringExtra("s_id");
        m_user_name = intent.getStringExtra("user_name");

        setToolBar();

        mapView = findViewById(R.id.bind_mapView);
        btn_bind_getLocation = findViewById(R.id.btn_bind_getLocation);
        btn_bind_sendLocation = findViewById(R.id.btn_bind_sendLocation);
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);



        /*相关权限的申请*/
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(BindingLocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(BindingLocationActivity.this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED)
        {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(BindingLocationActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty())
        {
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(BindingLocationActivity.this, permissions, 1);
        }
        else
        {
            requestLocation();
        }

        //获取位置按钮的监听
        btn_bind_getLocation.setOnClickListener(this);

        //发送位置信息的按钮监听
        btn_bind_sendLocation.setOnClickListener(this);

    }

    /**
     * 参数 无
     * 功能 实现初始化地图组件，以及启动地图的客户端请求
     * 返回值 无
     */
    private void requestLocation()
    {
        initLocation(); //初始化

        mLocationClient.start(); //地图请求启动
    }

    /**
     * 参数 无
     * 功能 设定地图组件的基础信息
     * 返回值 无
     */
    private void initLocation()
    {
        LocationClientOption option = new LocationClientOption(); //用于保存设置信息
        //option.setOpenGps(true);
        option.setCoorType("bd09ll");  //设定坐标系
        option.setScanSpan(5000);  //设置定位间隔
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);  //加载设置
    }

    /**
     * @param requestCode 传入请求编码
     * @param permissions 传入权限数组
     * @param grantResults //权限结果
     * 功能 判断用户是否给予了所有权限，如果没有，提示用户
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case 1:
                if (grantResults.length > 0)
                {
                    for (int result : grantResults)  //在grantResult中提取数据
                    {
                        if (result != PackageManager.PERMISSION_GRANTED)
                        {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }

                    }
                    requestLocation();
                }
                else
                {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }

    }

    /**
     * 重写了父类的onResume()方法
     * 功能 将地图组件在暂停后恢复
     */
    @Override
    protected void onResume()
    {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 重写了父类的onResume()方法
     * 功能 将地图组件暂停
     */
    @Override
    protected void onPause()
    {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 重写了父类的onResume()方法
     * 功能 将地图组件销毁
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mLocationClient.stop();
        mapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
    }

    private void isOpenGps()
    {
        boolean isopen = GpsUtil.isOPen(BindingLocationActivity.this);
        Log.e("zqc",""+isopen);
        if(!isopen)
        {
            Log.e("zqc","进入");
            final AlertDialog.Builder bindDialog = new AlertDialog.Builder(BindingLocationActivity.this);
                bindDialog.setIcon(R.drawable.title);
                bindDialog.setTitle("提示");
                bindDialog.setMessage("GPS未开启，定位可能会有偏差，请到设置打开");
                bindDialog.setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });
                bindDialog.setNegativeButton("知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

            bindDialog.setCancelable(false);
            AlertDialog dialog = bindDialog.create();
            dialog.show();
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_bind_getLocation:
                isOpenGps();
                baiduMap.setMyLocationEnabled(true);
                m_isFirstLocate = true;
                m_isGetLocation = true;
                initLocation();
                mLocationClient.start();
                Log.e(TAG, "onClick: 点击1" );
                break;
            case R.id.btn_bind_sendLocation:
                new Thread(new Runnable() {
                    @Override
                    public void run()
                    {
                        try
                        {
                            Log.e(TAG, "onClick: 点击2" );
                            JSONObject jsonObject = new JSONObject(); //保存数据的JSON

                            jsonObject.put("request_type","9");

                            m_send = jsonObject.toString();

                            Socket socket = new Socket("47.106.151.249",10001);
                            Log.e(TAG, "主端口握手成功" );
                            socket.setSoTimeout(2000);

                            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                            DataInputStream in = new DataInputStream(socket.getInputStream());

                            out.writeUTF(m_send);
                            Log.e(TAG, "主端口发送成功" );

                            String receiver = in.readUTF();
                            JSONObject jsonObject1= new JSONObject(receiver);


                            Log.e(TAG, "主端口接收成功" );

                            in.close();
                            out.close();
                            socket.close();

                            Thread.sleep(1000);

                            JSONObject jsonObjectsend = new JSONObject();           //封装发送给服务器的json数据
                            Log.e(TAG, "run: 纬度1"+m_Latitude);
                            Log.e(TAG, "run: 经度"+m_Longitude);
                            jsonObjectsend.put("latitude",String.valueOf(m_Latitude));//纬度
                            Log.e(TAG, "run: 纬度"+m_Latitude);
                            jsonObjectsend.put("longitude",String.valueOf(m_Longitude));//经度
                            Log.e(TAG, "run: 经度"+m_Longitude);
                            jsonObjectsend.put("user_name", m_user_name);
                            jsonObjectsend.put("S_ID",m_s_id);
                            m_send = jsonObjectsend.toString();

                            socket = new Socket();                   //创建socket连接
                            SocketAddress address = new InetSocketAddress("47.106.151.249",Integer.parseInt(jsonObject1.getString("port1")));
                            socket.connect(address,10000);          //设置超时
                            Log.e(TAG, "从端口握手成功" );
                            socket.setSoTimeout(30000);

                            out = new DataOutputStream(socket.getOutputStream());
                            out.writeUTF(m_send);
                            Log.e(TAG, "从端口发送成功" );

                            out.close();
                            socket.close();

                            isM_isFinish = true;


                            //getMainsocket.connMainsocket(TAG,m_send,handler);                              //连接服务器
                        }
                        catch (JSONException e)
                        {
                            //Log.d("abc","JSON异常");
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (SocketException e) {
                            e.printStackTrace();
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                while (!isM_isFinish) {}
                isM_isFinish = false;
                Toast.makeText(BindingLocationActivity.this,"信息绑定完成，返回登陆",Toast.LENGTH_SHORT).show();
                AtyContainer.finishAllActivity();
                Log.e("zqc","跳转登录");
                Intent intent = new Intent(BindingLocationActivity.this,LoginActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("isauto",false);
                intent.putExtras(bundle);
                int type = LoginActivity.wahtType();
                if (type == 0)
                    gt_loginUser.clear();
                else if (type == 1)
                    gs_loginUser.clear();
                startActivity(intent);
                break;
        }
    }

    /**
     * 描述 MyLocationListener 继承了BDAbstractLocationListener
     * 功能 实现监听接口，存储位置的返回信息
     */
    public class MyLocationListener extends BDAbstractLocationListener
    {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            /**判断定位方式*/
//            if( bdLocation.getLocType() == BDLocation.TypeNetWorkLocation)
//            {
//                final AlertDialog.Builder bindDialog = new AlertDialog.Builder(BindingLocationActivity.this);
//                bindDialog.setIcon(R.drawable.title);
//                bindDialog.setTitle("提示");
//                bindDialog.setMessage("GPS未开启，定位可能会有偏差，请到设置打开");
//                bindDialog.setPositiveButton("设置", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                        startActivity(intent);
//                    }
//                });
//                bindDialog.setNegativeButton("知道了", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
//            }
            if(bdLocation.getLocType() == BDLocation.TypeGpsLocation|| bdLocation.getLocType() == BDLocation.TypeNetWorkLocation)
            {
                m_Latitude = bdLocation.getLatitude(); //获取纬度
                m_Longitude = bdLocation.getLongitude();//获取经度
                navigateTo(bdLocation); //设定显示的图标
            }
        }
    }

    /**
     * 功能 利用百度的API完成，将自己的位置显示在地图上的功能
     * @param location 返回位置
     */
    private void navigateTo(BDLocation location)
    {
        if(m_isFirstLocate)
        {

            LatLng mLatLng = new LatLng(location.getLatitude(),location.getLongitude()); //LatLng标识符
            MapStatusUpdate mUpdate = MapStatusUpdateFactory.newLatLng(mLatLng); //利用工厂类将地图更新
            baiduMap.animateMapStatus(mUpdate);
            if(!m_isGetLocation) //判断第几次定位
            {
                mUpdate = MapStatusUpdateFactory.zoomTo(16f);

            }

            else
            {
                mUpdate = MapStatusUpdateFactory.zoomTo(19f);
                m_isGetLocation = false;
            }


            baiduMap.animateMapStatus(mUpdate); //更新后数据传入地图
            m_isFirstLocate = false;
        }
        MyLocationData.Builder locationBuilder = new MyLocationData.Builder(); //位置信息存储类
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.longitude(location.getLongitude());
        MyLocationData locationData = locationBuilder.build(); //传入数据
        baiduMap.setMyLocationData(locationData);
    }
}
