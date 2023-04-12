package service;

import common.Message;
import common.MessageType;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * 客户端语音通话服务
 */
public class ClientCallService {

    /*
    打电话
     */
    public static void Call(String senderId, String input){

        String[] splits = input.split("@");
        String receiverId = splits[1];
        Message message = new Message();
        message.setType(MessageType.CALL);
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        ClientConnectServerThread clientConnectServerThread = ManageService.getClientConnectServiceThread(senderId);

        try {
            ObjectOutputStream oos = new ObjectOutputStream(clientConnectServerThread.getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*
    挂断电话
     */
    public static void HangUp(String senderId){

        if (ManageService.getClientCallHm().containsKey(senderId)){
            Message message = new Message();
            message.setSenderId(senderId);
            message.setType(MessageType.HANG_UP);

            try {
                ClientConnectServerThread clientConnectServerThread = ManageService.getClientConnectServiceThread(senderId);
                ObjectOutputStream oos = new ObjectOutputStream(clientConnectServerThread.getSocket().getOutputStream());
                oos.writeObject(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            System.out.println("There is no call to be hung up");
        }
    }
}
