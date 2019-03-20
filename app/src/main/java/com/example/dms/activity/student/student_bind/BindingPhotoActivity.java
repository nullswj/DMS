package com.example.dms.activity.student.student_bind;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.dms.activity.common.LoginActivity;
import com.example.dms.activitycontainer.AtyContainer;
import com.example.dms.activitycontainer.BaseActivity;
import com.example.dms.R;
import com.example.dms.util.ProgressDialogUtil;
import com.example.dms.util.getMainsocket;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import static com.example.dms.activity.common.LoginActivity.gs_loginUser;
import static com.example.dms.activity.common.LoginActivity.gt_loginUser;
import static com.example.dms.util.CompressPicture.comp;


public class BindingPhotoActivity extends BaseActivity implements View.OnClickListener
{

    private String TAG = "BindingPhotoActivity";

    private Toolbar tlb_bindphoto;

    private Button btn_bindphoto_takephoto;

    private Button btn_bindphoto_submit;

    private ImageView imv_bindphoto_picture;

    private static final int m_TAKE_PHOTO = 1;

    private static  volatile boolean m_isfinish = false;

    private Uri m_imageUri;

    private File m_outputImage;

    private String m_send;

    private String m_user_name;

    private String m_s_id;

    private volatile String m_receive;

    private Boolean isBindLocation;

    private void initView()
    {
        btn_bindphoto_takephoto = findViewById(R.id.bind_takephoto);
        btn_bindphoto_submit = findViewById(R.id.bind_upload);
        imv_bindphoto_picture = findViewById(R.id.bind_picture);
    }

    private void setButtonClick()
    {
        btn_bindphoto_takephoto.setOnClickListener(this);                                               //获取验证码按钮事件
        btn_bindphoto_submit.setOnClickListener(this);                                                //确定按钮事件
    }

    private void setToolBar()
    {
        tlb_bindphoto = findViewById(R.id.bindphoto_tlb);
        setSupportActionBar(tlb_bindphoto);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_photo);

        Intent intent = getIntent();
        m_user_name = intent.getStringExtra("user_name");
        m_s_id = intent.getStringExtra("s_id");
        isBindLocation = intent.getBooleanExtra("loc",false);

        initView();
        setToolBar();
        setButtonClick();
    }

    private void showSubmitStudentDialog()
    {
        final AlertDialog.Builder bindDialog = new AlertDialog.Builder(BindingPhotoActivity.this);
        bindDialog.setIcon(R.drawable.title);
        bindDialog.setTitle("照片绑定");
        bindDialog.setMessage("照片绑定成功!\n是否立即上传位置?\n上传位置时，请确保设备位于寝室");

        bindDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(BindingPhotoActivity.this,BindingLocationActivity.class);
                intent.putExtra("s_id",m_s_id);
                intent.putExtra("user_name",m_user_name);
                startActivity(intent);
                finish();
            }
        });

        bindDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        bindDialog.setCancelable(false);
        AlertDialog dialog = bindDialog.create();
        dialog.show();
    }

    private void showBcakDialog()
    {
        final AlertDialog.Builder bindDialog = new AlertDialog.Builder(BindingPhotoActivity.this);
        bindDialog.setIcon(R.drawable.title);
        bindDialog.setTitle("照片绑定");
        bindDialog.setMessage("放弃未上传的照片，确认返回？");

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case m_TAKE_PHOTO:
                if(requestCode == RESULT_FIRST_USER)
                {
                    try
                    {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(m_imageUri));
                        imv_bindphoto_picture.setImageBitmap(bitmap);
                    }
                    catch (FileNotFoundException e)
                    {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {

            final int portMes = msg.arg1;
            Log.e(TAG,"进入");
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        JSONObject jsonPost = new JSONObject();
                        jsonPost.put("S_ID",m_s_id);

                        m_send = jsonPost.toString();

                        Log.e(TAG,"666"+m_send);
                        Socket socket = new Socket("47.106.151.249",portMes);      //创建socket连接
                        socket.setSoTimeout(10000);                                          //设置超时

                        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                        out.writeUTF(m_send);
                        out.close();
                        socket.close();

                        Thread.sleep(1000);

                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(m_imageUri));
                        Bitmap compbitmap = comp(bitmap);
                        saveBitmapFile(compbitmap);

                        Log.e(TAG,"新线程");
                        socket = new Socket();
                        SocketAddress addr = new InetSocketAddress("47.106.151.249",portMes);
                        socket.connect(addr,10000);
                        socket.setSoTimeout(30000);


                        Log.e(TAG,"握手成功2");
                        OutputStream outputStream = socket.getOutputStream();

                        FileInputStream file=new FileInputStream(m_outputImage);
                        byte[] buf = new byte[1024];
                        int loc = 0;

                        while((loc = file.read(buf)) != -1)
                        {
                            outputStream.write(buf,0,loc);
                            //Log.e(TAG,"传输异常");
                        }
                        Log.e(TAG,"成功");

                        outputStream.close();
                        socket.close();
                        file.close();


                        Thread.sleep(5000);

                        Log.e(TAG, "run: "+'1' );
                        socket = new Socket();
                        addr = new InetSocketAddress("47.106.151.249",portMes);
                        socket.connect(addr,10000);
                        Log.e(TAG, "run: "+"握手成功1" );
                        socket.setSoTimeout(30000);

                        DataInputStream in = new DataInputStream(socket.getInputStream());      //创建输入流
                        m_receive= in.readUTF();                            //读取服务器发送的信息
                        Log.e(TAG, "run: "+"读取成功1" );
                        m_isfinish = true;
                    }
                    catch (IOException e)
                    {
                        ProgressDialogUtil.cancel();
                        Log.d("tst","图片发送失败");
                        e.printStackTrace();
                    }
                    catch (JSONException e)
                    {
                        ProgressDialogUtil.cancel();
                        e.printStackTrace();
                    }
                    catch (InterruptedException e)
                    {
                        ProgressDialogUtil.cancel();
                        e.printStackTrace();
                    }
                }
            }).start();
            while(!m_isfinish) { }
            m_isfinish = false;
            if (m_receive.equals("true"))
            {
                ProgressDialogUtil.cancel();
                Toast.makeText(BindingPhotoActivity.this,"绑定成功",Toast.LENGTH_SHORT).show();
                if (isBindLocation)
                {
                    showSubmitStudentDialog();
                }
                else
                {
                    Toast.makeText(BindingPhotoActivity.this,"信息完善成功",Toast.LENGTH_LONG).show();
                    AtyContainer.finishAllActivity();
                    Intent intent = new Intent(BindingPhotoActivity.this,LoginActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isauto",false);
                    intent.putExtras(bundle);
                    int type = LoginActivity.wahtType();
                    if (type == 0)
                        gt_loginUser.clear();
                    else if (type == 1)
                        gs_loginUser.clear();
                    startActivity(intent);
                }
            }


        }
    } ;

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case 1:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    takephoto();
                }
                else
                {
                    Toast.makeText(this,"你拒绝了相机权限申请,请允许",Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void saveBitmapFile(Bitmap bitmap){
        try
        {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(m_outputImage));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    private void takephoto()
    {
        m_outputImage = new File(getExternalCacheDir(),"personal_picture.jpg");  //应用关联目录下存放拍摄照片
        try
        {
            if(m_outputImage.exists())
                m_outputImage.delete();
            m_outputImage.createNewFile();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        if(Build.VERSION.SDK_INT >= 24)
            m_imageUri = FileProvider.getUriForFile(BindingPhotoActivity.this,"com.example.dms.fileprovider",m_outputImage);
        else
            m_imageUri = Uri.fromFile(m_outputImage);

        Intent intent =  new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,m_imageUri);
        startActivityForResult(intent,m_TAKE_PHOTO);
    }
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.bind_takephoto:
                if(ContextCompat.checkSelfPermission(BindingPhotoActivity.this,Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(BindingPhotoActivity.this,new String[]{Manifest.permission.CAMERA},1);
                }
                else
                {
                    takephoto();
                }
                break;
            case R.id.bind_upload:
                try
                {

                    if (m_outputImage != null && m_outputImage.length()!= 0)
                    {
                        //Log.e(TAG, "onClick: "+ m_outputImage.length());
                        ProgressDialogUtil.dailogProgress("正在上传",BindingPhotoActivity.this);
                        JSONObject jsonPost = new JSONObject();
                        jsonPost.put("request_type","8");
                        m_send = jsonPost.toString();
                        getMainsocket.connMainsocket(TAG,m_send,handler);
                    }
                    else
                    {
                        Toast.makeText(BindingPhotoActivity.this,"请先拍照片",Toast.LENGTH_LONG).show();
                    }

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                break;
        }
    }
}

