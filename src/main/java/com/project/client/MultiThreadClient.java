package com.project.client;

/**
 * Created with IntelliJ IDEA
 * @author:He
 * @Date:2019/7/19
 * @Time:10:51
 * @Description
 */


/*
* 1.建立Socket连接
* 2.启动读数据和写数据线程
 */


import com.project.handler.ReadDataFromServerThread;
import com.project.handler.WriteDataToServerThread;
import java.io.IOException;
import java.net.Socket;


public class MultiThreadClient  {


    public static void main(String[] args) {

        try {
            Socket socket = new Socket("localhost",6667);

            Thread writethread = new Thread(new WriteDataToServerThread(socket));
            writethread.setName("Thread-Client-Write");
            writethread.start();

            Thread readthread = new Thread(new ReadDataFromServerThread(socket));
            readthread.setName("Thread-Client-Read");
            readthread.start();



        } catch (IOException e) {
            System.err.println("客户端连接异常："+e);
        }
    }

}
