package service;

import common.Message;
import common.MessageType;
import common.User;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;


/**
 * 登录服务
 */
public class SignInService {

    private User user = new User();
    private Socket socket = new Socket();
    public boolean checkUser(String userId, String passwd){
        user.setUserId(userId);
        user.setPasswd(passwd);
        user.setStatus(1);
        boolean succeed = false;
        try{
            socket = new Socket(InetAddress.getByName("162.14.77.42"),9999);    //服务器ip、port
            socket.setReuseAddress(true);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(user);

            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message ms = (Message)ois.readObject();

            if(MessageType.SIGN_IN_SUCCEED.equals(ms.getType())){
                System.out.println("succeed! \n");
                ClientConnectServerThread clientConnectServerThread = new ClientConnectServerThread(socket);
                clientConnectServerThread.start();
                ManageService.addClientConnectServiceThread(userId, clientConnectServerThread);
                succeed = true;

            }else if(MessageType.SIGN_IN_FAIL.equals(ms.getType())){
                System.out.println("wrong password! \n");
                socket.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return succeed;
    }
    public boolean createUser(String userId, String passwd){
        user.setUserId(userId);
        user.setPasswd(passwd);
        user.setStatus(2);
        boolean succeed = false;
        try{
            socket = new Socket(InetAddress.getByName("162.14.77.42"),9999);    //服务器ip、port
            socket.setReuseAddress(true);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(user);

            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message ms = (Message)ois.readObject();
            if(MessageType.SIGN_UP_SUCCEED.equals(ms.getType())){
                System.out.println("succeed! \n");
                succeed = true;
                socket.close();
            }else if(MessageType.SIGN_UP_FAIL.equals(ms.getType())){
                System.out.println("user Id already exists!\n");
                socket.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return succeed;
    }
    public void myExit(String userId){
        Message message = new Message();
        message.setType(MessageType.EXIT);
        message.setSenderId(userId);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(message);
            ManageService.getClientConnectServiceThread(userId).getSocket().close();
            ManageService.removeClientConnectServerThread(userId);
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
