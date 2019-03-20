package com.example.dms.activity.student.student_work;

import android.Manifest;
import android.app.Activity;
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

import com.example.dms.activity.student.student_bind.BindingPhotoActivity;
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
import static com.example.dms.util.CompressPicture.comp;


public class UploadphotoActivity extends BaseActivity implements View.OnClickListener
{

    private String TAG = "UploadphotoActivity";

    private Toolbar tlb_upphoto;

    private Button btn_upphoto_takephoto;

    private Button btn_upphoto_submit;

    private ImageView image_picture;

    private static final int m_TAKE_PHOTO = 1;

    private static  volatile boolean m_isfinish = false;

    private Uri m_imageUri;

    private File m_outputImage;

    private String m_send;

    private String m_recever;

    private void initView()
    {
        btn_upphoto_submit = findViewById(R.id.img_upload);
        btn_upphoto_takephoto = findViewById(R.id.img_takephoto);
        image_picture = findViewById(R.id.image_tmp);
    }

    private void setButtonClick()
    {
        btn_upphoto_submit.setOnClickListener(this);                                               //获取验证码按钮事件
        btn_upphoto_takephoto.setOnClickListener(this);                                                //确定按钮事件
    }

    private void setToolBar()
    {
        tlb_upphoto = findViewById(R.id.tmpphoto_tlb);
        setSupportActionBar(tlb_upphoto);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void showBcakDialog()
    {
        final AlertDialog.Builder bindDialog = new AlertDialog.Builder(UploadphotoActivity.this);
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

    private void showSubmitStudentDialog()
    {
        final AlertDialog.Builder bindDialog = new AlertDialog.Builder(UploadphotoActivity.this);
        bindDialog.setIcon(R.drawable.title);
        bindDialog.setTitle("照片上传");
        bindDialog.setMessage("照片上传成功!\n是否立即上传位置?\n上传位置时，请确保设备位于寝室");

        bindDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
//                Intent intent=new Intent(UploadphotoActivity.this,BindingLocationActivity.class);//显式,第二次迭代跳转到InforVerifyActivity
//                startActivity(intent);
                Intent intent = new Intent(UploadphotoActivity.this,GetLocationActivity.class);
                startActivity(intent);
                finish();
            }
        });

        bindDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Intent intent=new Intent(UploadphotoActivity.this,LoginActivity.class);
//                startActivity(intent);                      //跳转至登陆界面
                finish();
            }
        });

        bindDialog.setCancelable(false);
        AlertDialog dialog = bindDialog.create();
        dialog.show();
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
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadphoto);

        initView();
        setToolBar();
        setButtonClick();

    }
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            final int portMes = msg.arg1;
            Log.d(TAG,"进入");
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        JSONObject jsonPost = new JSONObject();
                        jsonPost.put("S_ID",gs_loginUser.getsId());

                        m_send = jsonPost.toString();

                        Log.d(TAG,m_send);
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

                        Log.d(TAG,"新线程");
                        socket = new Socket();
                        SocketAddress addr = new InetSocketAddress("47.106.151.249",portMes);
                        socket.connect(addr,10000);
                        socket.setSoTimeout(30000);


                        Log.d(TAG,"握手成功2");
                        OutputStream outputStream = socket.getOutputStream();

                        FileInputStream file=new FileInputStream(m_outputImage);
                        byte[] buf = new byte[1024];
                        int loc = 0;

                        while((loc = file.read(buf)) != -1)
                        {
                            outputStream.write(buf,0,loc);
                            //Log.e(TAG,"传输异常");
                        }
                        Log.d(TAG,"成功");

                        outputStream.close();
                        socket.close();
                        file.close();


                        Thread.sleep(5000);

                        socket = new Socket();
                        addr = new InetSocketAddress("47.106.151.249",portMes);
                        socket.connect(addr,10000);
                        socket.setSoTimeout(30000);

                        DataInputStream in = new DataInputStream(socket.getInputStream());      //创建输入流
                        m_recever= in.readUTF();                            //读取服务器发送的信息

                        m_isfinish = true;
                    }
                    catch (IOException e)
                    {
                        Log.d("tst","图片发送失败");
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
            while(!m_isfinish){ }
            m_isfinish = false;
            ProgressDialogUtil.cancel();

            showSubmitStudentDialog();

        }
    } ;

    public void saveBitmapFile(Bitmap bitmap){
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(m_outputImage));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                        image_picture.setImageBitmap(bitmap);
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
                    Toast.makeText(this,"你拒绝了相机权限申请，请允许",Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
            Log.e(TAG, "takephoto: "+ "IO异常");
            e.printStackTrace();
        }
        if(Build.VERSION.SDK_INT >= 24)
            m_imageUri = FileProvider.getUriForFile(UploadphotoActivity.this,"com.example.dms.fileprovider",m_outputImage);
        else
            m_imageUri = Uri.fromFile(m_outputImage);

        Intent intent =  new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,m_imageUri);
        startActivityForResult(intent,m_TAKE_PHOTO);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.img_takephoto:
                if(ContextCompat.checkSelfPermission(UploadphotoActivity.this,Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(UploadphotoActivity.this,new String[]{Manifest.permission.CAMERA},1);
                }
                else
                {
                    takephoto();
                }
                break;
            case R.id.img_upload:
                Log.e(TAG,"点击");
                if (m_outputImage != null && m_outputImage.length()!= 0)
                {
                    try
                    {
                        ProgressDialogUtil.dailogProgress("正在上传",UploadphotoActivity.this);
                        JSONObject jsonPost = new JSONObject();
                        //jsonPost.put("user_name",gs_loginUser);
                        jsonPost.put("request_type","4");
                        jsonPost.put("s_id",gs_loginUser.getsId());
                        m_send = jsonPost.toString();
                        getMainsocket.connMainsocket(TAG,m_send,handler);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(UploadphotoActivity.this,"请先拍照片",Toast.LENGTH_LONG).show();
                }

                break;
        }
    }
}
