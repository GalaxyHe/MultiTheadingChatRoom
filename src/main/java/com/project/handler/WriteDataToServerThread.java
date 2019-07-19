package com.project.handler;

/**
 * Created with IntelliJ IDEA
 * @author:He
 * @Date:2019/7/19
 * @Time:10:51
 * @Description
 */

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class WriteDataToServerThread implements Runnable {
    private final Socket client;

    public WriteDataToServerThread(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        WriteData();
    }

    private void WriteData() {
        try {
            Scanner scanner = new Scanner(System.in);//控制台
            scanner.useDelimiter("\n");
            OutputStream out = this.client.getOutputStream();
            PrintStream printStream = new PrintStream(out);

            while(true){
                System.out.println("请输入> ");

                if (scanner.hasNextLine()) {
                    String message = scanner.nextLine().trim();
                    printStream.println(message);


                    if("Q".equals(message)){
                        System.out.println("检测到该用户退出了！");
                        scanner.close();
                        out.close();
                        client.close();
                        break;
                    }
                }

            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }


}
