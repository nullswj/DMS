package com.example.dms.util;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class getMainsocket
{
    private static String TAG;
    private static String ip = "47.106.151.249";

    private static int port = 10001;

    public static void connMainsocket(final String tag, final String send, final Handler handler)
    {
        TAG = tag;
        new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    Socket socket = new Socket(ip,port);

                    socket.setSoTimeout(2000);

                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                    DataInputStream in = new DataInputStream(socket.getInputStream());

                    out.writeUTF(send);

                    String receiver = in.readUTF();

                    in.close();
                    out.close();
                    socket.close();

                    JSONObject jsonObject = new JSONObject(receiver);

                    Thread.sleep(1000);

                    Message message = new Message();
                    message.what = 0;
                    message.arg1 = Integer.parseInt(jsonObject.getString("port1"));
                    handler.sendMessage(message);
                }
                catch (IOException e)
                {
                    Log.e(TAG,"连接主端口失败");
                    e.printStackTrace();
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
