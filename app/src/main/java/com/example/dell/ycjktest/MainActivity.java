package com.example.dell.ycjktest;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.SimpleTimeZone;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    /*
     主 变量
     */
// 主线程Handler
// 用于将从服务器获取的消息显示出来
    public static final int UPDATE_TEXT=1;
    /*private Handler mMainHandler=new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case  UPDATE_TEXT:
                                csdcsocV.setText(String.valueOf(readdata.getInitSOC_Float()));
                                ssdcsocV.setText(String.valueOf(readdata.getRTSOC_Float()));
                                sscdrlV.setText(String.valueOf(readdata.getCapacityByCurrent_Float()));
                                ssdlV.setText(String.valueOf(readdata.getCurrent_Float()));
                                smxhV.setText(String.valueOf(readdata.getLiveCounter()));
                                yqdlV.setText(String.valueOf(readdata.getDevBattaryPower()));
                                scznlV.setText(String.valueOf(readdata.getRTCapacityDiff()));
                                yqdlV.setText(String.valueOf(readdata.getDevBattaryPower()));
                                if (readdata.getLastResult() <= 0) {
                                    zhycjgV.setBackgroundColor(Color.parseColor("#DC143C"));
                                } else {
                                    zhycjgV.setBackgroundColor(Color.parseColor("#00FF00"));
                                }
                                if (readdata.isFaultStatus()) {
                                    FaultStatusV.setBackgroundColor(Color.parseColor("#DC143C"));
                                } else {
                                    FaultStatusV.setBackgroundColor(Color.parseColor("#00FF00"));
                                }
                                if (readdata.isTestStatus()) {
                                    TestStatusV.setBackgroundColor(Color.parseColor("#00FF00"));
                                    statusV.setText("测试中");
                                } else {
                                    TestStatusV.setBackgroundColor(Color.parseColor("#FFFF00"));
                                    statusV.setText("等待测试");
                                }
                                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                                timeV.setText(format.format(readdata.getTesTime() * 1000));
                                //receive_message始终显示最后一行数据
                                int offset = receive_message.getLineCount() * receive_message.getLineHeight();
                                if (offset > receive_message.getHeight()) {
                                    receive_message.scrollTo(0, offset - receive_message.getHeight());
                                }
                    break;
                    default:
                        break;
            }
        }
    };*/

    // Socket变量
    private Socket socket=null;
    private String jsonData;
    private String jsonDataRead;
    SendData senddata=new SendData();
   // ReadData readdata=new ReadData();
    //Receive_Thread receive_Thread = new Receive_Thread();
    // 输入流读取器对象

    // 线程池
// 为了方便展示,此处直接采用线程池进行线程管理,而没有一个个开线程
    private ExecutorService mThreadPool;

    /*
     * 接收服务器消息 变量
     */
    // 输入流对象
    InputStream is;
    InputStreamReader isr ;
    BufferedReader br ;
    // 接收服务器发送过来的消息
    String response;
    /*
     * 发送消息到服务器 变量
     */
    // 输出流对象
    OutputStream outputStream;
    boolean isConnect= true;
    boolean SendDtatus=false;
    //final String IPAdress="120.78.144.43";
    final String IPAdress="192.168.173.1";
    final int Port=4567;
    int StartMark=0,StopMark=0;
/*
 * 按钮 变量
 */

    // 连接 断开连接 发送数据到服务器 的按钮变量
//private Button btnConnect, btnDisconnect, btnSend;
    // 显示接收服务器消息 按钮
    private TextView receive_message;
    private EditText VIN,ZNL,ZCDY,SOC,RLPC,XSLC,SCQY;
    private TextView csdcsocV,ssdcsocV,sscdrlV,timeV,scznlV,statusV,ssdlV,smxhV,yqdlV,zhycjgV,TestStatusV,FaultStatusV;
    // 输入需要发送的消息 输入框
    private EditText mEdit;

    @Override
    protected void onStop() {
        super.onStop();
         /*
         * 存取输入变量，防止活动隐藏被清空
         */
        SharedPreferences.Editor editor=getSharedPreferences("data",MODE_PRIVATE).edit();
        editor.putString("VIN",VIN.getText().toString());
        editor.putString("mEdit",mEdit.getText().toString());
        editor.putString("ZCDY",ZCDY.getText().toString());
        editor.putString("SOC",SOC.getText().toString());
        editor.putString("ZNL",ZNL.getText().toString());
        editor.putString("RLPC",RLPC.getText().toString());
        editor.putString("XSLC",XSLC.getText().toString());
        editor.putString("SCQY",SCQY.getText().toString());
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        /*
         * 读取输入变量，防止活动隐藏被清空
         */
        SharedPreferences pref=getSharedPreferences("data",MODE_PRIVATE);
        VIN.setText(pref.getString("VIN",""));
        mEdit.setText(pref.getString("mEdit",""));
        ZCDY.setText(pref.getString("ZCDY",""));
        SOC.setText(pref.getString("SOC",""));
        ZNL.setText(pref.getString("ZNL",""));
        RLPC.setText(pref.getString("RLPC",""));
        XSLC.setText(pref.getString("XSLC",""));
        SCQY.setText(pref.getString("SCQY",""));
    }
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    /*
         * 初始化操作
         */

        csdcsocV=(TextView) findViewById(R.id.csdcsocV);
        ssdcsocV=(TextView) findViewById(R.id.ssdcsocV) ;
        sscdrlV=(TextView) findViewById(R.id.sscdrlV);
        timeV = (TextView) findViewById(R.id.timeV);
        scznlV=(TextView) findViewById(R.id.scznlV);
        statusV=(TextView) findViewById(R.id.statusV);
        ssdlV=(TextView) findViewById(R.id.ssdlV);
        smxhV=(TextView) findViewById(R.id.smxhV);
        yqdlV=(TextView) findViewById(R.id.yqdlV);
        zhycjgV=(TextView) findViewById(R.id.zhycjgV);
        receive_message = (TextView) findViewById(R.id.receive_message);
        TestStatusV=(TextView) findViewById(R.id.TestStatusV);
        FaultStatusV=(TextView) findViewById(R.id.FaultStatusV);

        final Button btnStart=(Button) findViewById(R.id.Start);
        final Button btnStop=(Button) findViewById(R.id.Stop);
        final Button btnConnect = (Button) findViewById(R.id.connect);
        final Button btnDisconnect = (Button) findViewById(R.id.disconnect);
        final Button btnSend = (Button) findViewById(R.id.send);
        Button Clear = (Button) findViewById(R.id.Clear);

        VIN=(EditText) findViewById(R.id.VIN);
        ZNL=(EditText) findViewById(R.id.ZNL);
        ZCDY=(EditText) findViewById(R.id.ZCDY);
        mEdit = (EditText) findViewById(R.id.edit);
        SOC=(EditText) findViewById(R.id.SOC);
        RLPC=(EditText) findViewById(R.id.RLPC);
        XSLC=(EditText) findViewById(R.id.XSLC);
        SCQY=(EditText) findViewById(R.id.SCQY);
        // 初始化线程池
        mThreadPool = Executors.newCachedThreadPool();
        // 初始化按钮状态
        btnDisconnect.setEnabled(false);
        btnStart.setEnabled(false);
        btnStop.setEnabled(false);
        btnSend.setEnabled(false);
        // receive_message可以滑动显示
        receive_message.setMovementMethod(ScrollingMovementMethod.getInstance());
        /*
         * 创建客户端 & 服务器的连接
         */
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnect) {
                    //btnDisconnect.setEnabled(true);
                    //btnConnect.setEnabled(false);
                    mThreadPool.execute(new Runnable() {
                        //@Override
                        public void run() {
                            try {

                                // 创建Socket对象 & 指定服务端的IP 及 端口号
                                //socket = new Socket(IPAdress, Port);
                                runOnUiThread(new Runnable()//不允许其他线程直接操作组件，用提供的此方法可以
                                {
                                    public void run()
                                    {
                                        // TODO Auto-generated method stub
                                        receive_message.append(new String("连接中，请等待!")+"\n");
                                    }
                                });
                                socket=new Socket();
                                SocketAddress endpoint=new InetSocketAddress(IPAdress,Port);
                                socket.connect(endpoint,5000);
                                //socket.setKeepAlive(true);
                                socket.setSoTimeout(10000);
                                // 判断客户端和服务器是否连接成功
                                System.out.println(socket.isConnected());
                                if(socket.isConnected())
                                {
                                    runOnUiThread(new Runnable()//不允许其他线程直接操作组件，用提供的此方法可以
                                    {
                                        public void run() {
                                            // TODO Auto-generated method stub
                                            receive_message.append(new String("连接成功!") + "\n");
                                            isConnect = false;
                                            btnStart.setEnabled(true);
                                            btnStop.setEnabled(true);
                                            btnSend.setEnabled(true);
                                            btnDisconnect.setEnabled(true);
                                            btnConnect.setEnabled(false);
                                            Receive_Thread receive_Thread = new Receive_Thread();
                                            receive_Thread.start();
                                        }
                                    });
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable()//不允许其他线程直接操作组件，用提供的此方法可以
                                {
                                    public void run()
                                    {
                                        // TODO Auto-generated method stub
                                        receive_message.append(new String("连接失败!")+"\n");
                                    }
                                });
                            }
                        }
                    });
                }
                else{
                    // btnDisconnect.setEnabled(false);
                }
            }

        });
        /*
         * 接收 服务器消息
         */
        Clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receive_message.setText("");
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            if(socket.isConnected()) {
                                SetSendData();
                                senddata.setCommandID_U8(4);
                                jsonData = new Gson().toJson(senddata)+ "\n";
                                jsonData="CAERI"+jsonData;
                                outputStream = socket.getOutputStream();
                                // 步骤2：写入需要发送的数据到输出流对象中
                                outputStream.write(jsonData.getBytes("utf-8"));
                                // 特别注意：数据的结尾加上换行符才可让服务器端的readline()停止阻塞
                                // 步骤3：发送数据到服务端
                                outputStream.flush();
                                Thread.sleep(500);
                                senddata.setCommandID_U8(1);
                                jsonData = new Gson().toJson(senddata)+ "\n";
                                jsonData="CAERI"+jsonData;
                                outputStream = socket.getOutputStream();
                                // 步骤2：写入需要发送的数据到输出流对象中
                                outputStream.write(jsonData.getBytes("utf-8"));
                                // 特别注意：数据的结尾加上换行符才可让服务器端的readline()停止阻塞
                                // 步骤3：发送数据到服务端
                                outputStream.flush();
                            }
                        }catch (IOException e)
                        {
                            e.printStackTrace();
                            runOnUiThread(new Runnable()//不允许其他线程直接操作组件，用提供的此方法可以
                            {
                                public void run()
                                {
                                    // TODO Auto-generated method stub
                                    receive_message.append(new String("Send Failed !")+"\n");
                                }
                            });
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            if(socket.isConnected()) {
                                SetSendData();
                                senddata.setCommandID_U8(2);
                                jsonData = new Gson().toJson(senddata) + "\n";
                                jsonData="CAERI"+jsonData;
                                outputStream = socket.getOutputStream();
                                // 步骤2：写入需要发送的数据到输出流对象中
                                outputStream.write(jsonData.getBytes("utf-8"));
                                // 特别注意：数据的结尾加上换行符才可让服务器端的readline()停止阻塞
                                // 步骤3：发送数据到服务端
                                outputStream.flush();
                            }
                        }catch (IOException e)
                        {
                            e.printStackTrace();
                            runOnUiThread(new Runnable()//不允许其他线程直接操作组件，用提供的此方法可以
                            {
                                public void run()
                                {
                                    // TODO Auto-generated method stub
                                    receive_message.append(new String("Send Failed !")+"\n");
                                }
                            });
                        }
                    }
                });
            }
        });
        /**
         * 发送消息 给 服务器
         */
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 利用线程池直接开启一个线程 & 执行该线程
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            // 步骤1：从Socket 获得输出流对象OutputStream

                            // 该对象作用：发送数据
                            if(socket.isConnected()) {
                                SetSendData();
                                senddata.setCommandID_U8(4);
                                jsonData = new Gson().toJson(senddata) + "\n";
                                jsonData="CAERI"+jsonData;
                                outputStream = socket.getOutputStream();
                                // 步骤2：写入需要发送的数据到输出流对象中
                                outputStream.write(jsonData.getBytes("utf-8"));
                                // 特别注意：数据的结尾加上换行符才可让服务器端的readline()停止阻塞
                                // 步骤3：发送数据到服务端
                                outputStream.flush();
                            }
                        }

                        catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable()//不允许其他线程直接操作组件，用提供的此方法可以
                            {
                                public void run()
                                {
                                    // TODO Auto-generated method stub
                                    receive_message.append(new String("Send Failed !")+"\n");
                                }
                            });
                        }
                    }
                });
            }
        });
        /**
         * 断开客户端 & 服务器的连接
         */

        btnDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    // 最终关闭整个Socket连接
                    //System.out.println(socket.isConnected());
                    btnDisconnect.setEnabled(false);
                    btnConnect.setEnabled(true);
                    if(socket.isConnected()) {
                        // 断开 服务器发送到客户端 的连接，即关闭输入流读取器对象BufferedReader
                        // 断开 客户端发送到服务器 的连接，即关闭输出流对象OutputStream
                        //is.close();
                        if(SendDtatus)
                        {
                            outputStream.close();
                            SendDtatus=false;
                        }
                        socket.close();
                        // 判断客户端和服务器是否已经断开连接
                        System.out.println(socket.isConnected());
                        isConnect = true;
                        btnStart.setEnabled(false);
                        btnStop.setEnabled(false);
                        btnSend.setEnabled(false);
                        receive_message.append(new String("Connect Closed !") + "\n");
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                    receive_message.append(new String("DisConnect Failed !")+"\n");
                }
            }
        });
    }

    class Receive_Thread extends Thread
    {
        public void run()//重写run方法
        {
            try
            {
                while (true)
                {

                    if(socket.isConnected()) {
                        final byte[] buffer = new byte[512];//创建接收缓冲区
                        is = socket.getInputStream();
                        final int len = is.read(buffer);//数据读出来，并且返回数据的长度
                        // TODO Auto-generated method stub
                        //receive_message.append(new String(buffer,0,len)+"\n");
                        if (len==0)
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    receive_message.append(new String("Receive Failed len=0 !")+"\n");
                                }
                            });
                            break;
                        }
                        else {
                            jsonDataRead = new String(buffer).trim();
                            StartMark = jsonDataRead.indexOf("{");
                            StopMark = jsonDataRead.indexOf("}");
                            if(StartMark!=-1&&StopMark!=-1&&StartMark<StopMark)

                            {
                                jsonDataRead=jsonDataRead.substring(StartMark,StopMark+1);
                                //final int length=jsonDataRead.length();
                             /*runOnUiThread(new Runnable()//不允许其他线程直接操作组件，用提供的此方法可以
                                {
                                    public void run()
                                    {
                                        // TODO Auto-generated method stub
                                        receive_message.setText(String.valueOf(length)+"_"+String.valueOf(len)+"_"+String.valueOf(StartMark)+"_"+String.valueOf(StopMark));
                                    }
                                });*/
                                Thread.sleep(50);
                                ReadDataHandle();
                               /* Message message=new Message();
                                message.what=UPDATE_TEXT;
                                mMainHandler.sendMessage(message);*/

                            }
                            /*else{
                                runOnUiThread(new Runnable()//不允许其他线程直接操作组件，用提供的此方法可以
                                {
                                    public void run()
                                    {
                                        // TODO Auto-generated method stub
                                        receive_message.append(new String("Receive Failed Data Err!")+"\n");
                                    }
                                });
                            }*/
                        }
                    }
                }
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
                runOnUiThread(new Runnable()//不允许其他线程直接操作组件，用提供的此方法可以
                {
                    public void run()
                    {
                        // TODO Auto-generated method stub
                        receive_message.append(new String("Receive Failed IO Err_请断开连接并重新连接电脑!")+"\n");
                    }
                });
            }
            catch (JsonSyntaxException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
                runOnUiThread(new Runnable()//不允许其他线程直接操作组件，用提供的此方法可以
                {
                    public void run()
                    {
                        // TODO Auto-generated method stub
                        receive_message.append(new String("Json Err_请断开连接并重新连接电脑 !")+"\n");
                    }
                });
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
                runOnUiThread(new Runnable()//不允许其他线程直接操作组件，用提供的此方法可以
                {
                    public void run()
                    {
                        // TODO Auto-generated method stub
                        receive_message.append(new String("Receive Failed Sys_请断开连接并重新连接电脑 !")+"\n");
                    }
                });
            }
        }
    }

    class Connect_Thread extends Thread
    {
        public void run()//重写run方法
        {
            try
            {
                // 创建Socket对象 & 指定服务端的IP 及 端口号
                socket = new Socket(IPAdress, Port);
                // 判断客户端和服务器是否连接成功
                System.out.println(socket.isConnected());
                runOnUiThread(new Runnable()//不允许其他线程直接操作组件，用提供的此方法可以
                {
                    public void run()
                    {
                        // TODO Auto-generated method stub
                        receive_message.append(new String("Connect Success !")+"\n");
                        isConnect = false;
                        Receive_Thread receive_Thread = new Receive_Thread();
                        receive_Thread.start();
                    }
                });
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
                runOnUiThread(new Runnable()//不允许其他线程直接操作组件，用提供的此方法可以
                {
                    public void run()
                    {
                        // TODO Auto-generated method stub
                        receive_message.append(new String("Connect Failed !")+"\n");
                    }
                });
            }
        }
    }

    class Send_Thread extends Thread
    {
        public void run()//重写run方法
        {
            try
            {
                outputStream = socket.getOutputStream();
                // 步骤2：写入需要发送的数据到输出流对象中
                outputStream.write(jsonData.getBytes("utf-8"));
                // 特别注意：数据的结尾加上换行符才可让服务器端的readline()停止阻塞
                // 步骤3：发送数据到服务端
                outputStream.flush();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
                runOnUiThread(new Runnable()//不允许其他线程直接操作组件，用提供的此方法可以
                {
                    public void run()
                    {
                        // TODO Auto-generated method stub
                        receive_message.append(new String("Send Failed !")+"\n");
                    }
                });
            }
        }
    }
    private void SetSendData() {
        try{
        senddata.setCommandID_U8(4);
        senddata.setSeqID_Str("");
        senddata.setUseProperty_Str("");
        senddata.setVehicleModel_Str("");
        senddata.setBoxModel_Str("");
        senddata.setBatch_Str("");
        senddata.setVehicleVIN_Str(VIN.getText().toString());
        senddata.setVehicleNumber_Str(mEdit.getText().toString());
        senddata.setTotalmileage_Str(XSLC.getText().toString());
        senddata.setCompanyName_Str(SCQY.getText().toString());
        senddata.setTotalPower_Str(ZNL.getText().toString());
        senddata.setVoltage_Str(ZCDY.getText().toString());
        if (SOC.getText().toString().equals("")) {
            senddata.setSOCDiff_Float(Float.parseFloat("0"));
        } else {
            senddata.setSOCDiff_Float(Float.parseFloat(SOC.getText().toString()));
        }
        if (RLPC.getText().toString().equals("")) {
            senddata.setAllowableCapacityDeviation_Float(Float.parseFloat("0"));
        } else {
            senddata.setAllowableCapacityDeviation_Float(Float.parseFloat(RLPC.getText().toString()));
        }
        SendDtatus = true;
    }
    catch (Exception e)
    {
        e.printStackTrace();
    }
    }
    public static int byte2ToUnsignedShort(byte[] bytes, int off) {

        int high = bytes[off];

        int low = bytes[off + 1];

        return (high << 8 & 0xFF00) | (low & 0xFF);

    }
    public void  ReadDataHandle() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{
                Gson gs = new Gson();
                ReadData readdata = gs.fromJson(jsonDataRead, ReadData.class);
                csdcsocV.setText(String.valueOf(readdata.getInitSOC_Float()));
                ssdcsocV.setText(String.valueOf(readdata.getRTSOC_Float()));
                sscdrlV.setText(String.valueOf(readdata.getCapacityByCurrent_Float()));
                ssdlV.setText(String.valueOf(readdata.getCurrent_Float()));
                smxhV.setText(String.valueOf(readdata.getLiveCounter()));
                yqdlV.setText(String.valueOf(readdata.getDevBattaryPower()*100));
                scznlV.setText(String.valueOf(readdata.getRTCapacityDiff()));
                yqdlV.setText(String.valueOf(readdata.getDevBattaryPower()));
                if (readdata.getLastResult() <= 0) {
                    zhycjgV.setBackgroundColor(Color.parseColor("#DC143C"));
                } else {
                    zhycjgV.setBackgroundColor(Color.parseColor("#00FF00"));
                }
                if (readdata.isFaultStatus()) {
                    FaultStatusV.setBackgroundColor(Color.parseColor("#DC143C"));
                } else {
                    FaultStatusV.setBackgroundColor(Color.parseColor("#00FF00"));
                }
                if (readdata.isTestStatus()) {
                    TestStatusV.setBackgroundColor(Color.parseColor("#00FF00"));
                    statusV.setText("测试中");
                } else {
                    TestStatusV.setBackgroundColor(Color.parseColor("#FFFF00"));
                    statusV.setText("等待测试");
                }
                SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                timeV.setText(format.format(readdata.getTesTime() * 1000));
                //receive_message始终显示最后一行数据
                int offset = receive_message.getLineCount() * receive_message.getLineHeight();
                if (offset > receive_message.getHeight()) {
                    receive_message.scrollTo(0, offset - receive_message.getHeight());
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                /*runOnUiThread(new Runnable()//不允许其他线程直接操作组件，用提供的此方法可以
                {
                    public void run()
                    {
                        // TODO Auto-generated method stub
                        receive_message.append(new String("转换失败 !")+"\n");
                    }
                });*/
            }
            }
        });
    }
}

