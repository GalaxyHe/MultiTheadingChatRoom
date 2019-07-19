package com.project.server;

/**
 * Created with IntelliJ IDEA
 * @author:He
 * @Date:2019/7/19
 * @Time:10:51
 * @Description
 */


/*
* 1.建立ServerSocket
* 2.接受客户端连接（socket）
* 3.采用线程的方式:
* 4.线程池，执行业务逻辑
*/

import com.project.handler.ClientHandler;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;


public class MultiThreadServer {

    //使用自己定义的线程池
    private static final ExecutorService SERVER_POOL =new ThreadPoolExecutor(
            20,
            40,
            2L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(10),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    public static void main(String[] args){
        //1.键盘输入 Scanner类
        //2.参数 String[] args
        //3.文件 Properties
        //4.数据库（MySql）

        try {
            ServerSocket serverSocket = new ServerSocket(6667);
            System.out.println("服务器端启动 "+serverSocket.getInetAddress()+":"
                    +serverSocket.getLocalPort());

            while(true){

                final Socket client = serverSocket.accept();
                //使用线程池
                SERVER_POOL.execute(new ClientHandler(client));
            }


        } catch (IOException e) {
            System.err.println("服务器启动异常： "+e);
        }

    }

}
