package service;

import common.Message;
import common.MessageType;
import common.User;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.Date;


/**
 * 服务器主进程
 * 用于监听端口、保持通信
 */
public class Server {

    private ServerSocket serverSocket = null;


    public Server(){
        try{
            serverSocket = new ServerSocket(9999);
            ServerCallService serverCallService = new ServerCallService();
            serverCallService.start();
            System.out.println("server is listening at port 9999...\n");

            while (true){
                String fileName =  "/home/lighthouse/MicroChat/data/log.txt";   //服务器日志
                File file = new File(fileName);
                FileWriter fw = new FileWriter(file,true);
                BufferedWriter bw = new BufferedWriter(fw);

                Socket socket = serverSocket.accept();
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                User user = (User)ois.readObject();
                Message message = new Message();

                /*
                登陆成功创建服务器连接客户端线程
                 */
                if (user.getStatus() == 1){
                    if(ManageUserDB.CheckId(user.getUserId(),user.getPasswd())){
                        message.setType(MessageType.SIGN_IN_SUCCEED);
                        oos.writeObject(message);
                        bw.write(new Date().toString() + " user " + user.getUserId() + " succeed to sign in \n");
                        ServerConnectClientThread serverConnectClientThread = new ServerConnectClientThread(socket, user.getUserId());
                        serverConnectClientThread.start();
                        ManageService.addServerConnectClientThread(user.getUserId(), serverConnectClientThread);

                    }else {
                        message.setType(MessageType.SIGN_IN_FAIL);
                        oos.writeObject(message);
                        bw.write(new Date().toString() + " user " + user.getUserId() + " failed to sign in \n");
                        socket.close();
                    }
                }else if (user.getStatus() == 2){
                    if(ManageUserDB.CreateId(user.getUserId(),user.getPasswd())){
                        message.setType(MessageType.SIGN_UP_SUCCEED);
                        oos.writeObject(message);
                        bw.write(new Date().toString() + " user " + user.getUserId() + " succeed to sign up \n");
                    }else {
                        message.setType(MessageType.SIGN_UP_FAIL);
                        oos.writeObject(message);
                        bw.write(new Date().toString() + " user " + user.getUserId() + " failed to sign up \n");
                    }
                    socket.close();
                }
                bw.close();
            }
        }catch (Exception e){
            e.printStackTrace();

        }finally {
            try {
                serverSocket.close();
            }catch (IOException e){
                e.printStackTrace();
            }

        }

    }
}
