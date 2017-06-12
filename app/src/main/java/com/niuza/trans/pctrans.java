package com.niuza.trans;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.SocketAddress;

import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;

import com.niuza.trans.p2p.FileTransferService;
import com.niuza.trans.utils.NetWorkUtil;
import com.niuza.trans.utils.TransferCounter;
import com.niuza.trans.utils.UriToPath;

public class pctrans extends AppCompatActivity {
    private Button sendButton;
    private Button receiveButton;
    private EditText et;
    private TextView ipview;
    private static ServerSocketChannel serverSocketChannel = null;
    private static SocketChannel socketChannel = null;
    private static Selector selector = null;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //接收返回的数据，也就是选择的文件
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                Uri uri = data.getData();
                String filename = uri.getPath();
                Toast.makeText(getApplicationContext(), "开始传输", Toast.LENGTH_LONG).show();
                try {
//                    Thread.sleep(2000);
                    send(filename);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pctrans);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("与电脑传输文件");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        et = (EditText) findViewById(R.id.ipaddress_text);
        ipview = (TextView) findViewById(R.id.show_ip);
        String targetip = NetWorkUtil.getLocalIpAddress(getApplicationContext());
        if (targetip.equals("0.0.0.0")) {
            et.setText("");
            ipview.setText("请连接到与PC端同一WIFI网络");
        } else {
            et.setText(targetip.substring(0, targetip.lastIndexOf(".") + 1));
            et.setSelection(et.getText().length());
            ipview.setText(NetWorkUtil.getLocalIpAddress(getApplicationContext()));
        }

        sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (!NetWorkUtil.isIP(et.getText().toString()) || et.getText().toString().equals(ipview.getText().toString())) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(pctrans.this);
                    alertDialog.setTitle("注意");
                    alertDialog.setMessage("请正确填写IP地址");
                    alertDialog.setPositiveButton("继续", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                        }
                    });
                    alertDialog.show();
                    return;
                }
                try {
                    //  Intent intent = new Intent();
//                   intent.setAction(android.content.Intent.ACTION_VIEW);
//
//                   intent.setDataAndType(Uri.parse("file://" + Environment.getExternalStorageDirectory() + "/"
//                           + getApplicationContext().getPackageName()), "*/*");
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(intent, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        receiveButton = (Button) findViewById(R.id.receive_button);
        receiveButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(pctrans.this);
                alertDialog.setTitle("注意");
                alertDialog.setMessage("请确保发送方已点击发送!");
                alertDialog.setPositiveButton("继续", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                try {
                                    selector = Selector.open();
                                    serverSocketChannel = ServerSocketChannel.open();
                                    serverSocketChannel.configureBlocking(false);
                                    serverSocketChannel.socket().setReuseAddress(true);
                                    serverSocketChannel.socket().bind(new InetSocketAddress(1991));
                                    serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
                                    while (selector.select() > 0) {
                                        Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                                        while (it.hasNext()) {
                                            SelectionKey readyKey = it.next();
                                            it.remove();

                                            SocketChannel socketChannel = null;
                                            String string = "";
                                            try {
                                                socketChannel = ((ServerSocketChannel) readyKey.channel()).accept();
                                                string = receiveData(socketChannel);
                                                writeFileData(getApplicationContext().getFilesDir().getAbsolutePath(),string);

                                            }catch(Exception ex){
                                                Log.e(ex.getMessage(),"err");

                                            } finally {

                                            }
                                        }
                                    }
                                } catch (Exception ex) {
                                    Log.e(ex.getMessage(),"err");
                                }

                                 finally {
                                    try {
                                        selector.close();
                                    } catch(Exception ex) {

                                    }

                                }
                            }
                        }).start();


                    }
                });
                alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
                alertDialog.show();
            }
        });
    }

    private void send(final String file) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    selector = Selector.open();
                    serverSocketChannel = ServerSocketChannel.open();
                    serverSocketChannel.configureBlocking(false);
                    serverSocketChannel.socket().setReuseAddress(true);
                    serverSocketChannel.socket().bind(new InetSocketAddress(1991));
                    serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
                    while (selector.select() > 0) {
                        Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                        while (it.hasNext()) {
                            SelectionKey readyKey = it.next();
                            it.remove();

                            SocketChannel socketChannel = null;
                            String string = "";
                            try {
                                socketChannel = ((ServerSocketChannel) readyKey.channel()).accept();
                                string = receiveData(socketChannel);
                                Log.i("INFO", string);
                                Log.i("INFOFile", file);
                                if (string.equals("filename")) {
                                    File f = new File(file);
                                    if (f.exists() && f.isFile()) {
                                        sendData(socketChannel, file);
                                    } else {
                                        Log.i("INFO", "文件不存在！");
                                        Log.i("INFO", file);
                                    }
                                }
                                Log.i("INFO", file);
                                File f = null;

                                if (string.equals(file)) {
                                    f = new File(file);
                                    sendFile(socketChannel, f);
                                } else {
                                    f = new File(file);
                                    sendFile(socketChannel, f);
                                }
                                if (f.exists() && f.isFile()) {
                                    TransferCounter.setRecord(pctrans.this, 1, f.length());
                                }
                            } catch (Exception ex) {
                                Log.i("SEVERE", ex.getMessage());
                                Toast.makeText(getApplicationContext(), "传输错误", Toast.LENGTH_LONG).show();
                            } finally {
                                try {
                                    socketChannel.close();
                                } catch (Exception ex) {
                                    Log.i("SEVERE", ex.getMessage());
                                    Toast.makeText(getApplicationContext(), "传输", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
                } catch (ClosedChannelException ex) {
                    Log.i("SEVERE", ex.getMessage());
                    Toast.makeText(getApplicationContext(), "传输错误", Toast.LENGTH_LONG).show();
                } catch (IOException ex) {
                    Log.i("SEVERE", ex.getMessage());
                } finally {
                    try {
                        selector.close();
                    } catch (Exception ex) {
                        Log.i("SEVERE", ex.getMessage());
                    }
                    try {
                        serverSocketChannel.close();
                    } catch (Exception ex) {
                        Log.i("SEVERE", ex.getMessage());
                    }
                }
            }
        }).start();
    }

    private String receiveData(SocketChannel socketChannel) throws IOException {
        String string = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        try {
            byte[] bytes;
            int size = 0;
            while ((size = socketChannel.read(buffer)) >= 0) {
                buffer.flip();
                bytes = new byte[size];
                buffer.get(bytes);
                baos.write(bytes);
                buffer.clear();
            }
            bytes = baos.toByteArray();
            string = new String(bytes);
        } catch (Exception ex) {
            Log.i("SEVERE", ex.getMessage());
        } finally {
            try {
                baos.close();
            } catch (Exception ex) {
                Log.i("SEVERE", ex.getMessage());
            }
        }
        return string;
    }

    private void sendData(SocketChannel socketChannel, String string) throws IOException {
        byte[] bytes = string.getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        socketChannel.write(buffer);
        socketChannel.socket().shutdownOutput();
    }

    private void receiveFile(SocketChannel socketChannel, File file) throws IOException {
        FileOutputStream fos = null;
        FileChannel channel = null;

        try {
            fos = new FileOutputStream(file);
            channel = fos.getChannel();
            ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

            int size = 0;
            while ((size = socketChannel.read(buffer)) != -1) {
                buffer.flip();
                if (size > 0) {
                    buffer.limit(size);
                    channel.write(buffer);
                    buffer.clear();
                }
            }
        } catch (Exception ex) {
            Log.i("SEVERE", ex.getMessage());
        } finally {
            try {
                channel.close();
            } catch (Exception ex) {
                Log.i("SEVERE", ex.getMessage());
            }
            try {
                fos.close();
            } catch (Exception ex) {
                Log.i("SEVERE", ex.getMessage());
            }
        }
    }

    private void sendFile(SocketChannel socketChannel, File file) throws Exception {
        FileInputStream fis = null;
        FileChannel channel = null;
        try {
            fis = new FileInputStream(file);
            channel = fis.getChannel();
            ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
            int size = 0, count = size;
            while ((size = channel.read(buffer)) != -1) {
                buffer.rewind();
                buffer.limit(size);
                socketChannel.write(buffer);
                count += size;
                Log.e("pctrans", "" + count);
                buffer.clear();
            }
            socketChannel.socket().shutdownOutput();
            Toast.makeText(this, "发送完成", Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Log.i("SEVERE", ex.getMessage());
        } finally {
            try {
                channel.close();
            } catch (Exception ex) {
                Log.i("SEVERE", ex.getMessage());
            }
            try {
                fis.close();
            } catch (Exception ex) {
                Log.i("SEVERE", ex.getMessage());
            }
        }
    }

    @SuppressWarnings("finally")
    public void stop() throws Exception {
        try {
            serverSocketChannel.close();
            System.out.println("已经关闭socketChannel");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //向上层抛出异常
        }
    }


    public void writeFileData(String fileName,String message){

        try{

            FileOutputStream fout =openFileOutput(fileName, MODE_PRIVATE);

            byte [] bytes = message.getBytes();

            fout.write(bytes);

            fout.close();

        }

        catch(Exception e){

            e.printStackTrace();

        }

    }
}
