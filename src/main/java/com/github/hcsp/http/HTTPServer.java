package com.github.hcsp.http;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

// 要启动server然后再让client给server发消息


public class HTTPServer {
    // 创建socket
    // http代替端口有8080和8008（感觉很多cs模型用的都是这两个端口）
    private int port = 8080;
    private ServerSocket serverSocket;

    // 顾名思义启动监听
    public void TCPServer() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("服务器启动监听" + port + "端口");
    }

    // 定义输入和输出流的方法（其实我就觉得这样写比较正确）(对于server应该是先输入再输出）
    private PrintWriter getWriter(Socket socket) throws IOException {
        // 获得outputStream的地址
        OutputStream socketOut = socket.getOutputStream();
        // 网络流写出需要flush
        // flush()方法可以强迫输出流(或缓冲的流)发送数据，即使此时缓冲区还没有填满
        // printWriter构造方法为自动flush
        return new PrintWriter(new OutputStreamWriter(socketOut, "utf-8"), true);
    }

    private BufferedReader getReader(Socket socket) throws IOException {
        InputStream socketIn = socket.getInputStream();
        return new BufferedReader(new InputStreamReader(socketIn, "utf-8"));
    }

    //     server的核心
    public void Service(){
        while (true){
            Socket socket=null;
            try {
//此处程序阻塞，监听并等待用户发起连接，有连接请求就生成一个套接字
                socket=serverSocket.accept();

//本地服务器控制台显示客户连接的用户信息
                System.out.println("New connection accepted:"+socket.getInetAddress());
                BufferedReader br=getReader(socket);//字符串输入流
                PrintWriter pw=getWriter(socket);//字符串输出流
                pw.println("来自服务器消息：欢迎使用本服务！");

                String msg=null;
//此处程序阻塞，每次从输入流中读入一行字符串
                while ((msg=br.readLine())!=null){
//如果用户发送信息为”bye“，就结束通信
                    if(msg.equals("bye")){
                        pw.println("来自服务器消息：服务器断开连接，结束服务！");
                        System.out.println("客户端离开。");
                        break;
                    }
                    pw.println("来自服务器消息："+msg);
                }
            }catch (IOException e){
                e.printStackTrace();
            }finally {
                try {
                    if (socket!=null)
                        socket.close();//关闭socket连接以及相关的输入输出流
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
}

