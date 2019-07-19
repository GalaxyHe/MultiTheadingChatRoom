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
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ClientHandler implements Runnable {
    private static final Map<String, Socket> SOCKET_CONCURRENT_HASH_MAP = new ConcurrentHashMap<>();
    private final Socket client;
    private String CurrentOnlineName;

    public ClientHandler(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = this.client.getInputStream();
            Scanner scanner = new Scanner(inputStream);

            while (true){
                if (scanner.hasNextLine()) {
                    String strFromClient = scanner.nextLine();

                    // windows下将默认换行/r/n中的/r替换为空字符串
                    Pattern pattern = Pattern.compile("\r");
                    Matcher matcher = pattern.matcher(strFromClient);
                    strFromClient = matcher.replaceAll("");



                    if (strFromClient.startsWith("R")) {
                        //注册流程
                        String userName = strFromClient.split(":")[1];
                        Register(userName,this.client);
                        continue;
                    }

                    if(strFromClient.startsWith("G")){
                        //群聊流程
                        String msg = strFromClient.split(":")[1];
                        GroupChat(msg);
                        continue;
                    }

                    if(strFromClient.startsWith("P")){
                        //私聊流程
                        String OtherName = strFromClient.split("-")[1]
                                .split(":")[0];
                        String msg = strFromClient.split("-")[1]
                                .split(":")[1];

                        PrivateChat(OtherName,msg,this.client);
                        continue;
                    }


                    if("Q".equals(strFromClient)){
                        Quit();
                    }


                }
            }
        } catch (IOException e) {
            System.err.println("服务器之间的通信发生异常："+e);
        }
    }

    private void PrivateChat(String Othername, String message,Socket myclient) {
        Socket privatesocket = SOCKET_CONCURRENT_HASH_MAP.get(Othername);
        String myName = "";

        for (Map.Entry<String, Socket> entry:SOCKET_CONCURRENT_HASH_MAP.entrySet()) {
            if(entry.getValue().equals(myclient)){
                myName = entry.getKey();
            }
        }

        try {
            OutputStream outputStream =  privatesocket.getOutputStream();
            PrintStream out = new PrintStream(outputStream);
            out.println("收到来自"+myName+"的私聊信息为> "+message);
            out.flush();
        } catch (IOException e) {
            System.err.println("私聊异常 "+e);
        }

    }

    private void Quit() {
        Iterator<Map.Entry<String,Socket>> iterator = SOCKET_CONCURRENT_HASH_MAP.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String,Socket> entry = iterator.next();
            if(entry.getValue() == this.client){
                System.out.println("用户："+entry.getKey() +"已退出！");
                iterator.remove();
                break;
            }
        }

    }

    private void GroupChat(String message) {
        Set<Map.Entry<String,Socket>> clientSet = SOCKET_CONCURRENT_HASH_MAP.entrySet();
        for(Map.Entry<String,Socket> entry : clientSet){
            String key = entry.getKey();
            Socket socket = entry.getValue();
            if(socket == this.client){
                CurrentOnlineName = key;
            }
        }

        for(Map.Entry<String,Socket> entry : clientSet){
            if(!entry.getKey().equals(CurrentOnlineName)){
                try {
                    OutputStream outputStream = entry.getValue().getOutputStream();
                    PrintStream printStream = new PrintStream(outputStream);
                    printStream.println("收到来自"+this.CurrentOnlineName+"的群聊信息为> "+message);
                } catch (IOException e) {
                    System.err.println("群聊异常"+e);
                }
            }
        }
    }

    private void Register(String name,Socket newclient) {
        //注册
        //name -> socket
        //key -> value
        SOCKET_CONCURRENT_HASH_MAP.put(name,newclient);
        this.SendMessage(this.client, "恭喜 <" + name + "> 注册成功");
        printOnlineClient();
    }

    private void SendMessage(Socket socket, String message) {
        try {
            OutputStream outputStream = socket.getOutputStream();
            PrintStream out = new PrintStream(outputStream);
            out.println(message);
            out.flush();
        } catch (IOException e) {
            System.err.println("发送消息时产生的的错误为："+e);
        }
    }

    private void printOnlineClient() {
        System.out.println("当前在线的客户端共有" + SOCKET_CONCURRENT_HASH_MAP.size() + "个，名称列表如下：");
        for(String key : SOCKET_CONCURRENT_HASH_MAP.keySet()){
            System.out.println(key+" ");
        }
    }
}
