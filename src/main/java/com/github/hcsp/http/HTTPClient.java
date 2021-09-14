package com.github.hcsp.http;

import java.io.*;
import java.net.Socket;

public class HTTPClient {
    private Socket socket;
    private PrintWriter pw;
    private BufferedReader br;

    // 其实和server的差不多，但是client需要确认ip？
    public void TCPClient(String ip, String port) throws IOException {
        //向服务器发起连接，实现三次握手
        // 不成功则抛出错误
        socket = new Socket(ip, Integer.parseInt(port));

        // 得到输出字节流地址，flush
        OutputStream socketOut = socket.getOutputStream();
        pw = new PrintWriter(new OutputStreamWriter(socketOut, "utf-8"), true);

// 输入字节流地址，封装（咋封的？
        InputStream socketIn = socket.getInputStream();
        br = new BufferedReader(new InputStreamReader(socketIn, "utf-8"));
    }

    // 实现网络通信发送和接收
    public void send(String msg) {
        // 输出字节流
        pw.println(msg);
    }

    public String receive() {
        String msg = null;
        try {
            msg = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return msg;
    }

    public void close(){
        try {
            if(socket!=null){
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
