package cn.wwdab.socketclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import cn.wwdab.socketclient.socket.MySocket;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mIpEditText;
    private EditText mPortEditText;
    private EditText mReceiveText;
    private EditText mSendText;
    private Handler mHandler;
    private Button mConnectBtn;
    private Button mDisConnectBtn;
    private MySocket mMySocket;
    private Button mSendBtn;
    private Button mClearBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        handlerInit();

    }
    public void handlerInit(){
        mHandler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 0:
//                        连接成功！
                        mConnectBtn.setEnabled(false);
                        mConnectBtn.setText("已连接");
                        mDisConnectBtn.setEnabled(true);
                        mReceiveText.append("连接"+mMySocket.getRemoteIp()+"成功！\n");
                        break;
                    case -1:
//                        断开连接成功！
                        mConnectBtn.setEnabled(true);
                        mConnectBtn.setText("连接");
                        mDisConnectBtn.setEnabled(false);
                        break;
                    case 1:
//                       接收到消息，并显示出来
                        mReceiveText.append("接收消息:"+msg.obj.toString()+"\n");
                        break;
                }
            }
        };
    }

    public void initView(){
        mIpEditText = (EditText) findViewById(R.id.ip_editText);
        mPortEditText = (EditText) findViewById(R.id.port_editText);
        mReceiveText = (EditText) findViewById(R.id.receive_text);
        mSendText = (EditText) findViewById(R.id.send_text);
        mSendText.setText("");
        mReceiveText.setText("");
        mConnectBtn = (Button)findViewById(R.id.connect_btn);
        mDisConnectBtn = (Button) findViewById(R.id.disconnect_btn);
        mSendBtn = (Button) findViewById(R.id.send_btn);
        mClearBtn = (Button) findViewById(R.id.clear_btn);

        mClearBtn.setOnClickListener(this);
        mSendBtn.setOnClickListener(this);
        mConnectBtn.setOnClickListener(this);
        mDisConnectBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.connect_btn:
//                点击连接
//                Toast.makeText(this,"66",Toast.LENGTH_SHORT).show();
                String ip = mIpEditText.getText().toString();
                String p = mPortEditText.getText().toString();
                int port = Integer.parseInt(p);
                mMySocket = new MySocket(ip, port,mHandler);
                mMySocket.start();
                break;
            case R.id.disconnect_btn:
//                关闭socket
                mMySocket.disConnection();
                break;
            case R.id.send_btn:
//                发送消息
                String data = mSendText.getText().toString();
                if(mMySocket!=null){
                    mMySocket.sendMessage(data);
                    mReceiveText.append("发送消息:"+data+"\n");
                }
                break;
            case R.id.clear_btn:
//                清空消息
                mReceiveText.setText("");
                mSendText.setText("");
                break;
            default:
                break;

        }
    }
}
