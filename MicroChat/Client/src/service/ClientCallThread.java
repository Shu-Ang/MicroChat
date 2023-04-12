package service;

import utils.AudioUtils;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import java.io.*;
import java.net.Socket;

/**
 * 客户端语音通话线程
 */
public class ClientCallThread extends Thread{

    private Socket socket;
    private String userId;
    private OutputStream os;
    private InputStream is;
    private boolean loop = true;
    private byte[] bos=new byte[2024];
    private byte[] bis=new byte[2024];
    public ClientCallThread(String userId, Socket socket){
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
            System.out.println("Client " + userId + " succeed to connect ");
            os = socket.getOutputStream();
            is = socket.getInputStream();
            TargetDataLine targetDataLine = AudioUtils.getTargetDataLine();
            SourceDataLine sourceDataLine = AudioUtils.getSourceDataLine();

        while (loop){

            //获取音频流
            int writeLen = targetDataLine.read(bos,0,bos.length);
            //发
            if (bos != null && writeLen > 0) {
                //向对方发送拾音器获取到的音频
                os.write(bos,0,writeLen);
            }

            //收
            int readLen = is.read(bis);
            if (bis != null && readLen > 0) {
                //播放对方发送来的音频
                sourceDataLine.write(bis, 0, readLen);
            }
        }
        os.close();
        is.close();
        socket.close();
        }catch (IOException | LineUnavailableException e) {
        //e.printStackTrace();
    }
    }
}
