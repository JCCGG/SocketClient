package cn.wwdab.socketclient.socket;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketAddress;

public class MySocket extends Thread {
    private String ip;
    private int port;
    private Socket mSocket = null;
    private Handler mHandler;
    private BufferedReader mIn;
    private BufferedWriter mOut;
    private Message mMsg = new Message();

    public MySocket(String ip, int port, Handler handler) {
//        构造MyScoket对象
        this.ip = ip;
        this.port = port;
        this.mHandler = handler;
    }

    public String getRemoteIp() {
        SocketAddress remoteSocketAddress = mSocket.getRemoteSocketAddress();
        String rIp = remoteSocketAddress.toString().replace("/", "");
        return rIp;
    }

    public void disConnection() {
//        断开连接
        try {
            if (mSocket != null)
                mSocket.close();
            if (mSocket.isClosed()) {
                mMsg = new Message();
                mMsg.what = -1;
                mHandler.sendMessage(mMsg);
            }
            if (mIn != null)
                mIn.close();
            if (mOut != null)
                mOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendMessage(final String data) {
//        发送消息
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mSocket.isConnected()) {
                    try {
                        Log.e(MySocket.class.getSimpleName(),"发送消息");
                        mOut.write(data);
                        mOut.write("\n");
                        mOut.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void run() {
        super.run();
        try {
            mSocket = new Socket(ip, port);
            if (mSocket.isConnected()) {
//                连接成功！

                mMsg.what = 0;
                mHandler.sendMessage(mMsg);
//                Log.e(MySocket.class.getSimpleName(),"氨基酸地方"+mSocket.isConnected());

                mOut = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream(),"gbk"));
                mIn = new BufferedReader(new InputStreamReader(mSocket.getInputStream(),"gbk"));

//                while (true){
//                    if (mSocket.isConnected()) {
//                        try {
//                            mOut.write("data");
//                            mOut.flush();
//                            try {
//                                sleep(500);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }

                String len = "";
                while ((len = mIn.readLine()) != null) {
                    Message msg = Message.obtain();
                    msg.what = 1;
                    msg.obj = len;
                    mHandler.sendMessage(msg);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
