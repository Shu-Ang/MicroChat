package service;

import common.User;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 服务器语音通话服务
 */
public class ServerCallService extends Thread{

    private ServerSocket callServerSocket = null;

    public ServerCallService(){

    }

    @Override
    public void run(){

        try {
            callServerSocket = new ServerSocket(8888);
            while (true){

                Socket callSocket = callServerSocket.accept();
                ObjectInputStream ois = new ObjectInputStream(callSocket.getInputStream());
                User user = (User)ois.readObject();
                ServerCallThread serverCallThread = new ServerCallThread(user.getUserId(), callSocket);
                ManageService.addServerCallThread(user.getUserId(), serverCallThread);
                serverCallThread.start();

            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
