package service;

import java.io.*;
import java.net.Socket;

/**
 * 服务器语音通话线程
 */
public class ServerCallThread extends Thread{

    private String userId;
    private Socket socket;
    private OutputStream os;
    private InputStream is;
    private boolean loop = true;
    private byte[] bos=new byte[2024];
    public ServerCallThread(String userId, Socket socket){
        this.socket = socket;
        this.userId = userId;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getUserId() {
        return userId;
    }

    public void endLoop(){
        loop = false;
    }

    @Override
    public void run(){

        try {

            while (!ManageService.getPairsHm().containsKey(userId));
            while (!ManageService.getServerCallHm().containsKey(ManageService.getPairs(userId)));
            System.out.println("user " + userId + " is talking to user " + ManageService.getPairs(userId) );
            os = ManageService.getServerCallThread(ManageService.getPairs(userId)).getSocket().getOutputStream();
            is = socket.getInputStream();

            while (loop){

                //获取输入流
                int writeLen = is.read(bos,0,bos.length);
                //发送
                if (bos != null && writeLen > 0) {
                    os.write(bos,0,writeLen);
                }

            }
            is.close();
            os.close();
            socket.close();

        }catch (IOException e) {
            //e.printStackTrace();
        }
    }
}
