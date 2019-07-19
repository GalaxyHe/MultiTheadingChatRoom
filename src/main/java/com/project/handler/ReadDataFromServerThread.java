package com.project.handler;

/**
 * Created with IntelliJ IDEA
 * @author:He
 * @Date:2019/7/19
 * @Time:10:51
 * @Description
 */

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Scanner;

public class ReadDataFromServerThread implements Runnable{
    private final Socket client;

    public ReadDataFromServerThread(Socket client) {
        this.client = client;
    }


    @Override
    public void run() {
        ReadData();
    }

    private void ReadData() {
        try {
            InputStream in = this.client.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\n");
            while(true){

                if (scanner.hasNext()) {
                    System.out.println(scanner.nextLine());
                }

                if(client.isClosed()){
                    System.out.println("该用户退出了，关闭此客户端");
                    break;
                }

            }

            scanner.close();
        } catch (IOException e) {
            System.err.println("读取信息异常："+e);
        }
    }
}
