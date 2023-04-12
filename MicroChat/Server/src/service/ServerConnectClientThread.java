package service;

import common.Message;
import common.MessageType;
import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 服务器连接客户端线程
 */
public class ServerConnectClientThread extends Thread{

    private Socket socket;
    private String userId;

    public ServerConnectClientThread(Socket socket, String userId) {
        this.socket = socket;
        this.userId = userId;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getUserId() {
        return userId;
    }



    @Override
    public void run(){

        String fileName =  "/home/lighthouse/MicroChat/data/log.txt"; //服务器日志
        File file = new File(fileName);
        FileWriter fw = null;

        try {
            fw = new FileWriter(file,true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedWriter bw = new BufferedWriter(fw);
        while (true){

            try {

                /*用户上线后检查是否有离线消息，若有则发送*/
                if (ManageService.getDb().containsKey(userId)){
                    Iterator<Message> iterator = ManageService.getDb().get(userId).iterator();
                    while (iterator.hasNext()){
                        ObjectOutputStream oos = new ObjectOutputStream(ManageService.getServerConnectClientThread(userId).getSocket().getOutputStream());
                        Message message = iterator.next();
                        oos.writeObject(message);
                    }
                    ManageService.removeOutLineMessage(userId);
                }

                /*接收消息*/
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject();

                /*如果用户不存在*/
                if (!ManageUserDB.isUserExist(message.getReceiverId()) && message.getReceiverId() != null){
                    ServerConnectClientThread serverConnectClientThread = ManageService.getServerConnectClientThread(message.getSenderId());
                    ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                    Message mes = new Message();
                    mes.setType(MessageType.USER_NOT_EXIST);
                    mes.setReceiverId(message.getSenderId());
                    oos.writeObject(mes);
                }
                /* 如果用户离线，则加入到离线消息map中*/
                else if (!ManageService.isOnline(message.getReceiverId()) && !MessageType.MESSAGE_FILE.equals(message.getType())){
                    ManageService.addOutLineMessage(message.getReceiverId(),message);

                }
                /*私聊*/
                else if (MessageType.PRIVATE_CHAT.equals(message.getType())){
                    ServerConnectClientThread serverConnectClientThread = ManageService.getServerConnectClientThread(message.getReceiverId());
                    ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                    oos.writeObject(message);
                }
                /*群聊 转发至除发送者以外所有在线用户*/
                else if (MessageType.PUBLIC_CHAT.equals(message.getType())){
                    HashMap<String ,ServerConnectClientThread> hm = ManageService.getHm();
                    Iterator<String > iterator = hm.keySet().iterator();
                    while (iterator.hasNext()){
                        String onlineUserId = iterator.next();
                        if (!onlineUserId.equals(message.getSenderId())){
                            ObjectOutputStream oos = new ObjectOutputStream(hm.get(onlineUserId).getSocket().getOutputStream());
                            oos.writeObject(message);
                        }
                    }
                }
                /*退出客户端*/
                else if (MessageType.EXIT.equals(message.getType())){
                    socket.close(); //关闭socket
                    ManageService.removeServerConnectClientThread(message.getSenderId());
                    break;          //退出线程
                }
                /*发送文件 加入到离线文件map*/
                else if (MessageType.MESSAGE_FILE.equals(message.getType())){
                    ManageService.addFile(message.getReceiverId(),message);
                    Message mes = new Message();
                    mes.setType(MessageType.MESSAGE_FILE);
                    mes.setTime(message.getTime());
                    mes.setFileBytes(null);
                    mes.setSenderId(message.getSenderId());
                    mes.setReceiverId(message.getReceiverId());
                    mes.setSrc(message.getSrc());
                    /*若用户离线 提示消息加入到离线消息map*/
                    if (!ManageService.isOnline(message.getReceiverId())){
                        ManageService.addOutLineMessage(message.getReceiverId(),mes);
                    }else {
                        ServerConnectClientThread serverConnectClientThread = ManageService.getServerConnectClientThread(mes.getReceiverId());
                        ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                        oos.writeObject(mes);
                    }
                }
                /*显示文件列表*/
                else if (MessageType.SHOW_FILE_LIST.equals(message.getType())){
                    if (ManageService.getFileDb().containsKey(message.getSenderId())){
                        Iterator<Message> iterator = ManageService.getFileDb().get(userId).iterator();
                        if (iterator.hasNext()){
                            ServerConnectClientThread serverConnectClientThread = ManageService.getServerConnectClientThread(message.getSenderId());
                            int index = 1;
                            while (iterator.hasNext()){
                                Message mes = iterator.next();
                                mes.setType(MessageType.SHOW_FILE_LIST);
                                mes.setContent(String.valueOf(index));
                                ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                                oos.writeObject(mes);
                                index++;
                            }
                        }
                    }
                }
                /*下载文件*/
                else if (MessageType.DOWNLOAD_FILE.equals(message.getType())){
                    int index = Integer.parseInt(message.getContent());
                    Iterator<Message> iterator = ManageService.getFileDb().get(userId).iterator();
                    if (iterator.hasNext()){
                        ServerConnectClientThread serverConnectClientThread = ManageService.getServerConnectClientThread(message.getSenderId());
                        int i = 1;
                        while (iterator.hasNext()){
                            Message mes = iterator.next();
                            if (i == index){
                                mes.setType(MessageType.DOWNLOAD_FILE);
                                mes.setSrc(message.getSrc());
                                ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                                oos.writeObject(mes);
                            }
                            i++;
                        }
                    }
                }
                /*打电话*/
                else if (MessageType.CALL.equals(message.getType())){
                    if (ManageService.getHm().containsKey(message.getReceiverId())){
                        ServerConnectClientThread serverConnectClientThread = ManageService.getServerConnectClientThread(message.getReceiverId());
                        ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                        oos.writeObject(message);
                    }else {
                        ServerConnectClientThread serverConnectClientThread = ManageService.getServerConnectClientThread(message.getSenderId());
                        ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                        Message mes = new Message();
                        mes.setSenderId(message.getReceiverId());
                        mes.setReceiverId(message.getSenderId());
                        mes.setType(MessageType.USER_NOT_ONLINE);
                        oos.writeObject(mes);
                    }
                }
                /*接电话*/
                else if (MessageType.CALL_Y.equals(message.getType())){
                    ServerConnectClientThread serverConnectClientThread = ManageService.getServerConnectClientThread(message.getReceiverId());
                    ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                    oos.writeObject(message);
                    ManageService.addPairs(message.getSenderId(), message.getReceiverId());

                }
                /*不接*/
                else if (MessageType.CALL_N.equals(message.getType())){
                    ServerConnectClientThread serverConnectClientThread = ManageService.getServerConnectClientThread(message.getReceiverId());
                    ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                    oos.writeObject(message);
                }
                /*挂断电话*/
                else if (MessageType.HANG_UP.equals(message.getType())){

                    String otherId = ManageService.getPairs(message.getSenderId());
                    message.setReceiverId(otherId);
                    ServerConnectClientThread serverConnectClientThread = ManageService.getServerConnectClientThread(otherId);
                    ObjectOutputStream oos = new ObjectOutputStream(serverConnectClientThread.getSocket().getOutputStream());
                    oos.writeObject(message);

                    Message mes = new Message();
                    mes.setReceiverId(message.getSenderId());
                    mes.setType(MessageType.HANG_UP);
                    ServerConnectClientThread serverConnectClientThread1 = ManageService.getServerConnectClientThread(mes.getReceiverId());
                    ObjectOutputStream oos1 = new ObjectOutputStream(serverConnectClientThread1.getSocket().getOutputStream());
                    oos1.writeObject(mes);

                    ManageService.removePairs(message.getSenderId(), otherId);
                    ManageService.getServerCallThread(message.getSenderId()).endLoop();
                    ManageService.getServerCallThread(otherId).endLoop();
                    ManageService.removeServerCallThread(message.getSenderId());
                    ManageService.removeServerCallThread(otherId);
                }


            }catch (Exception e){
                e.printStackTrace();
            }
        }
        try {
            bw.write(new Date().toString() + " user " + userId + " logged out \n");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
