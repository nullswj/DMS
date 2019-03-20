package com.example.dms.activity.teacher;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.dms.R;
import com.example.dms.activitycontainer.BaseActivity;
import com.example.dms.util.getMainsocket;

import net.sf.json.JSON;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import static com.example.dms.R.color.colorPrimary;
import static com.example.dms.activity.common.LoginActivity.gt_loginUser;

/**
 * 文件名
 */
public class ResultActivity extends BaseActivity implements AbsentStudentItemAdapter.OnItemClickListener
{
    /** LOGTAG */
    private  String TAG = "ResultActivity";

    private Toolbar tlb_tesult;

    private RecyclerView recyclerView_result;

    private SearchView text_result_search;

    private SearchView.SearchAutoComplete SearchAutoComplete;

    private SwipeRefreshLayout swipr_result;

    /** 学生列表组 */
    private volatile List<AbsentStudentItem> m_absenteeismsList = new ArrayList<>();

    /** 学生列表组备份 */
    private volatile List<AbsentStudentItem> m_absenteeismsList_backup = new ArrayList<>();

    /** handler信息 更新文本 */
    private static final int m_UPDATE_TEXT = 0;

    /** handler信息 更新条目*/
    private static final int m_UPDATE_ITEM = 3;

    /** handler信息 更新搜索*/
    private static final int m_UPDATE_SEARCH = 4;

    /** 收到服务器的消息 */
    private volatile String m_receiver;

    private volatile String m_send;


    /** RecyclerView adapter */
    private volatile AbsentStudentItemAdapter m_adapter;

    /** 数据源组 */
    private volatile List<String[]> m_result = new ArrayList<>();

    private volatile List<Bitmap> m_image = new ArrayList<>();

    /** 服务器IP */
    private String m_ip = "47.106.151.249";


    @SuppressLint("ResourceAsColor")
    private void initView()
    {
        swipr_result = findViewById(R.id.result_swipe);
        swipr_result.setColorSchemeColors(colorPrimary);


        /*
         *下拉刷新
         */
        swipr_result.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        recyclerView_result = findViewById(R.id.result_recyclerview);
    }

    private void refresh()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Log.e(TAG, "run: 点击" );
                    JSONObject jsonObject = new JSONObject();               //将数据封装为json
                    jsonObject.put("request_type","3");
                    m_send = jsonObject.toString();
                    Log.e(TAG, "run: "+ m_send);
                    getMainsocket.connMainsocket(TAG,m_send,handler);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.result_toolbar, menu);

        //找到searchView
        MenuItem searchItem = menu.findItem(R.id.action_search);
        text_result_search = (SearchView) MenuItemCompat.getActionView(searchItem);

        //text_result_search.setIconified(false);//一开始处于展开状态
        text_result_search.onActionViewExpanded();// 当展开无输入内容的时候，没有关闭的图标
        text_result_search.setSubmitButtonEnabled(true);//显示提交按钮
        text_result_search.setQueryHint("筛选");//设置默认无内容时的文字提示
        SearchAutoComplete = text_result_search.findViewById(R.id.search_src_text);

        try
        {
            //利用反射调用收起SearchView的onCloseClicked()方法
            Method method = text_result_search.getClass().getDeclaredMethod("onCloseClicked");
            Log.e(TAG, "onResume: 1" );
            method.setAccessible(true);
            Log.e(TAG, "onResume: 2" );
            method.invoke(text_result_search);
            Log.e(TAG, "onResume: 3" );
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }

        text_result_search.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                //提交按钮的点击事件
                Toast.makeText(ResultActivity.this, query, Toast.LENGTH_SHORT).show();

                /**
                 * 方法名 setOnClickListener
                 * 描述 设置筛选按钮事件
                 * 参数 无
                 * 返回值 无
                 */
                if(!query.trim().equals(""))
                {
                    String search_key = query.trim();
                    int len = m_absenteeismsList_backup.size();

                    /** 发送搜索异步消息*/
                    Message message = new Message();
                    message.what = m_UPDATE_SEARCH;
                    message.arg1 = -1;
                    handler.sendMessage(message);

                    /** 发送搜索异步消息*/
                    for(int i = 0; i < len; i++)
                    {
                        /** 检索范围是学号 姓名 班级*/
                        if(m_absenteeismsList_backup.get(i).getClassname().trim().equals(search_key) || m_absenteeismsList_backup.get(i).getName().trim().equals(search_key) || m_absenteeismsList_backup.get(i).getNumber().trim().equals(search_key))
                        {
                            Message message1 = new Message();
                            message1.what = m_UPDATE_SEARCH;
                            message1.arg1 = i;
                            handler.sendMessage(message1);
                        }
                    }
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //当输入框内容改变的时候回调
                //Log.i(TAG,"内容: " + newText);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }


    private void setToolBar()
    {
        tlb_tesult = findViewById(R.id.result_tlb);
        setSupportActionBar(tlb_tesult);
        getSupportActionBar().setTitle("查寝结果");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                /**
                 * 方法名 setOnClickListener
                 * 描述 设置显示全部按钮事件
                 * 参数 无
                 * 返回值 无
                 */
                if (SearchAutoComplete.isShown())
                {
                    try
                    {
                        SearchAutoComplete.setText("");//清除文本
                        //利用反射调用收起SearchView的onCloseClicked()方法
                        Method method = text_result_search.getClass().getDeclaredMethod("onCloseClicked");
                        Log.e(TAG, "onOptionsItemSelected: 1" );
                        method.setAccessible(true);
                        Log.e(TAG, "onOptionsItemSelected: 2" );

                        method.invoke(text_result_search);
                        Log.e(TAG, "onOptionsItemSelected: 3" );

                        int len = m_absenteeismsList_backup.size();

                        /** 发送取消搜索异步消息*/
                        Message message = new Message();
                        message.what = m_UPDATE_SEARCH;
                        message.arg1 = -1;
                        handler.sendMessage(message);

                        /** 循环检索条件列表*/
                        for(int i = 0; i < len; i++)
                        {
                            Message message1 = new Message();
                            message1.what = m_UPDATE_SEARCH;
                            message1.arg1 = i;
                            handler.sendMessage(message1);
                            continue;
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    finish();
                }
                break;
            /**
             * 方法名 setOnClickListener
             * 描述 设置刷新按钮事件
             * 参数 无
             * 返回值 无
             */
//            case R.id.action_refresh:
//
//                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public String savaBitmap(Bitmap bitmap)
    {
        File file = new File(getExternalCacheDir(),"student_image.jpg");
        String img_file_url= null;
        try
        {
            if(!file.exists())
            {
                file.delete();
            }
            file.createNewFile();
            FileOutputStream fout = null;
            fout  = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fout);

            fout.flush();
            fout.close();

            img_file_url = file.getAbsolutePath();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return img_file_url;
    }
    @Override
    public void onItemClick(int position)
    {
        Intent intent = new Intent(ResultActivity.this,Studentinformation.class);
        String sname = m_result.get(position)[0];
        String sno = m_result.get(position)[1];
        String collage = m_result.get(position)[2];
        String grade = m_result.get(position)[3];
        String major = m_result.get(position)[4];
        String clname = m_result.get(position)[5];
        String sid = m_result.get(position)[6];
        String scname = m_result.get(position)[7];
        String tel = m_result.get(position)[8];
        Bitmap image = m_image.get(position);
        intent.putExtra("name",sname);
        intent.putExtra("no",sno);
        intent.putExtra("collage",collage);
        intent.putExtra("grade",grade);
        intent.putExtra("major",major);
        intent.putExtra("clname",clname);
        intent.putExtra("scname",scname);
        intent.putExtra("name",sname);
        intent.putExtra("tel",tel);
        intent.putExtra("sid",sid);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes=baos.toByteArray();
        intent.putExtra("image",bytes);
//        Uri img_uri = Uri.parse(savaBitmap(image));
//        intent.setData(img_uri);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);             //调用父类方法创建活动
        setContentView(R.layout.activity_result);       //设置活动布局

        initView();
        setToolBar();

        /** 初始化RecyclerView管理器与数据源*/
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView_result.setLayoutManager(layoutManager);
        m_adapter = new AbsentStudentItemAdapter(m_absenteeismsList);
        recyclerView_result.setAdapter(m_adapter);

        m_adapter.setItemClickListener(this);

        refresh();
    }


    /**
     * 方法名 handler
     * 功能 异步处理网络操作
     * 参数 无
     * 返回值 无
     */
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            final int port = msg.arg1;              //端口号
            switch (msg.what)
            {
                case m_UPDATE_TEXT:
                    new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            try
                            {
                                JSONObject jsonObjectsend = new JSONObject();           //封装发送给服务器的json数据
                                jsonObjectsend.put("tid",gt_loginUser.gettId());
                                //jsonObjectsend.put("type","3");
                                m_send = jsonObjectsend.toString();
                                Log.e(TAG, m_send);

                                Socket socket = new Socket();               //创建socket连接
                                SocketAddress address = new InetSocketAddress(m_ip,port);
                                socket.connect(address,100000);
                                socket.setSoTimeout(30000);                 //设置超时
                                Log.e(TAG,"握手成功");

                                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                                out.writeUTF(m_send);

                                out.close();
                                socket.close();

                                Thread.sleep(1000);
                                socket = new Socket();               //创建socket连接
                                address = new InetSocketAddress(m_ip,port);
                                socket.connect(address,100000);
                                socket.setSoTimeout(30000);                 //设置超时


                                DataInputStream in = new DataInputStream(socket.getInputStream());  //创建输入流
                                m_receiver = in.readUTF();                   //读取服务器发送的信息
                                Log.e(TAG,m_receiver);

                                in.close();
                                socket.close();

                                if (m_receiver.equals("empty") || m_receiver.equals("[]"))
                                {
                                    Message message = new Message();
                                    message.what = 0;
                                    handlerToast.sendMessage(message);
                                    return;
                                }
                                JSONArray jsonArray = new JSONArray(m_receiver);     //解析服务器数据

                                m_result.clear();               //清空结果数组

                                /** 将服务器发送的数据存在result数组中 */
                                int len = jsonArray.length();
                                int number = len;
                                for (int i = 0; i < len;i++)
                                {
                                    String[] result1 = new String[10];
                                    Log.e(TAG,"" + jsonArray.length());
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    result1[0] = jsonObject.getString("S_name");
                                    result1[1] = jsonObject.getString("S_No");
                                    result1[2] = jsonObject.getString("C_name");
                                    result1[3] = jsonObject.getString("Grade");
                                    result1[4] = jsonObject.getString("Major");
                                    result1[5] = jsonObject.getString("Class");
                                    result1[6] = jsonObject.getString("S_ID");
                                    result1[7] = jsonObject.getString("SC_name");
                                    result1[8] = jsonObject.getString("Phone");
                                    result1[9] = jsonObject.getString("P_Sys");

                                    if (result1[9].equals(""))
                                    {
                                        number--;
                                    }

                                    m_result.add(result1);
                                    Log.e(TAG,"id = " + m_result.get(i)[0]);
                                    Log.e(TAG,"name = " + m_result.get(i)[1]);
                                    Log.e(TAG,"classname = " + m_result.get(i)[2]);
                                }



                                Thread.sleep(1000);

                                for(int i = 0; i < number; i++)
                                {
                                    Log.e(TAG, "run: 图片"+ i);
                                    socket = new Socket();               //创建socket连接
                                    address = new InetSocketAddress(m_ip,port);
                                    Log.e(TAG, "run: 握手成功" );
                                    socket.connect(address,100000);
                                    socket.setSoTimeout(30000);                 //设置超时

                                    in = new DataInputStream(socket.getInputStream());
                                    int size = in.readInt();
                                    Log.e(TAG, "run: " + i + " " + size);

                                    byte[] data = new byte[size];
                                    int loc = 0;

                                    while (loc < size)
                                    {
                                        loc += in.read(data, loc, size - loc);
                                    }

                                    Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);

                                    m_image.add(bmp);
                                    //Log.e(TAG, "run: " );
                                    in.close();
                                    socket.close();

                                    Thread.sleep(1000);
                                }
                                /** 发送更新UI信息 */
                                Message message1 = new Message();
                                message1.what = m_UPDATE_ITEM;
                                message1.arg1 = len;
                                handler.sendMessage(message1);
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    break;

                case  m_UPDATE_ITEM:

                    final int len = msg.arg1;                   //获取数据量

                    Log.e(TAG,"index="+len);

                    m_absenteeismsList_backup.clear();      //清空数据源

                    /** 清空列表 */
                    int lenlist = m_absenteeismsList.size();
                    for (int i = 0; i < lenlist; i++)
                    {
                        m_adapter.removeItem(0);
                        m_adapter.notifyDataSetChanged();
                        Log.e(TAG,"len="+lenlist);
                    }
                    m_adapter.notifyDataSetChanged();

                    /** 将数据更新至RecyclerView的条目中 */
                    for (int i = 0,j = 0; i < len; i++)
                    {
                        try
                        {
                            String tmp = m_result.get(i)[3]+ "级" + m_result.get(i)[4] + m_result.get(i)[5] + "班";
                            AbsentStudentItem absent = null;
                            if (m_result.get(i)[9].equals(""))
                            {
                                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_photo);
                                absent = new AbsentStudentItem(m_result.get(i)[0],bitmap,m_result.get(i)[1],tmp);
                            }
                            else
                            {
                                absent = new AbsentStudentItem(m_result.get(i)[0],m_image.get(j),m_result.get(i)[1],tmp);
                                j++;
                            }

                            m_absenteeismsList.add(absent);
                            m_absenteeismsList_backup.add(absent);
                            m_adapter.notifyItemChanged(i);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    /** 刷新RecyclerView */
                    m_adapter.notifyItemChanged(0,m_absenteeismsList.size());
                    //swipr_result.
                    swipr_result.setRefreshing(false);
                    break;

                case m_UPDATE_SEARCH:

                    int index = msg.arg1;           //获取条目下标
                    /** 第一次则清空列表 */
                    if (index == -1)
                    {
                        int lenlist2 = m_absenteeismsList.size();
                        for (int i = 0; i < lenlist2; i++)
                        {
                            m_adapter.removeItem(0);
                            m_adapter.notifyDataSetChanged();
                        }
                        m_adapter.notifyDataSetChanged();
                    }
                    /** 每次添加一个 */
                    else
                    {
                        m_absenteeismsList.add(m_absenteeismsList_backup.get(index));
                        m_adapter.notifyItemChanged(index);
                        m_adapter.notifyItemChanged(0,m_absenteeismsList.size());
                    }
                    break;
                default:
                    break;
            }
        }
    };

    Handler handlerToast = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            swipr_result.setRefreshing(false);
            Toast.makeText(ResultActivity.this,"结果为空",Toast.LENGTH_LONG).show();

        }
    };
}


