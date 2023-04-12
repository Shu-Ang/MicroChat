package service;

import common.Message;
import common.MessageType;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;

/**
 * 客户端消息服务类
 * 用于处理私聊、群聊消息
 */

public class ClientMessageService {

    public void sendMessage(String senderId, String input){

        if (input.charAt(0) != '@') publicChat(senderId, input);
        else if (input.charAt(input.length() - 1) == 'f' && input.charAt(input.length() - 2) == '-'){
            int i;
            for(i = 0;;i++){
                if (input.charAt(i) == ':') break;
            }
            String receiverId = input.substring(1,i);
            String src = input.substring(i + 1,input.length() - 3);
            ClientFileService.sendFile(src,senderId,receiverId);
        }
        else {
            int i;
            for(i = 0;;i++){
                if (input.charAt(i) == ':') break;
            }
            String receiverId = input.substring(1,i);
            String content = input.substring(i + 1);
            privateChat(senderId, receiverId, content);
        }
    }

    private void privateChat(String senderId, String receiverId, String content){

        Message message = new Message();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setTime(new Date().toString());
        message.setType(MessageType.PRIVATE_CHAT);

        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageService.getClientConnectServiceThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void publicChat(String senderId, String content){

        Message message = new Message();
        message.setSenderId(senderId);
        message.setContent(content);
        message.setTime(new Date().toString());
        message.setType(MessageType.PUBLIC_CHAT);

        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageService.getClientConnectServiceThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
