package service;

import common.Message;
import common.MessageType;
import java.io.*;
import java.util.Date;

/**
 * 客户端文件服务类
 * 用于处理发送文件、显示文件列表、下载文件
 */
public class ClientFileService {

    /*
    发送文件
     */
    public static void sendFile(String src, String senderId, String receiverId){

        Message message = new Message();
        message.setSrc(src);
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setType(MessageType.MESSAGE_FILE);
        message.setTime(new Date().toString());
        FileInputStream fileInputStream = null;
        byte[] fileBytes = new byte[(int)new File(src).length()];

        try {
            fileInputStream = new FileInputStream(src);
            fileInputStream.read(fileBytes);
            message.setFileBytes(fileBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (fileInputStream != null){
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        String fileName = message.getSrc().substring(message.getSrc().lastIndexOf('\\') + 1);
        System.out.println( "send " + fileName + " to " + receiverId + " succeed!");

        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageService.getClientConnectServiceThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    显示文件列表
     */
    public static void showFileList(String userId){

        Message message = new Message();
        message.setType(MessageType.SHOW_FILE_LIST);
        message.setSenderId(userId);

        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageService.getClientConnectServiceThread(userId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*
    下载文件
     */
    public static void downloadFile(String userId, String input){

        String[] split = input.split(" ");
        String index = split[1];
        String path = split[2];
        Message message = new Message();
        message.setType(MessageType.DOWNLOAD_FILE);
        message.setSenderId(userId);
        message.setContent(index);
        message.setSrc(path);

        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageService.getClientConnectServiceThread(userId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
