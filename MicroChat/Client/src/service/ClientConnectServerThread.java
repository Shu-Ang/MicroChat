package service;

import common.Message;
import common.MessageType;
import common.User;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

/**
 * 客户端连接服务器端线程
 */
public class ClientConnectServerThread extends Thread{

    private Socket callSocket;  //用于语音通话
    private Socket socket;      //用于其他连接
    private ClientCallThread clientCallThread;
    public ClientConnectServerThread(Socket socket){
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    private boolean loop = true;

    @Override
    public void run(){

        while (loop){

            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message) ois.readObject();

                /*用户不存在*/
                if (MessageType.USER_NOT_EXIST.equals(message.getType())){
                    System.out.println("user " + message.getReceiverId() + " does not exist ");
                }
                /*私聊*/
                else if (MessageType.PRIVATE_CHAT.equals(message.getType())){
                    System.out.println(message.getTime() + " @" + message.getReceiverId() + " " + message.getSenderId() + ": " + message.getContent());
                }
                /*群聊*/
                else if (MessageType.PUBLIC_CHAT.equals(message.getType())){
                    System.out.println(message.getTime() + " " + message.getSenderId() + ": " + message.getContent());
                }
                /*发送文件*/
                else if (MessageType.MESSAGE_FILE.equals(message.getType())){
                    String fileName = message.getSrc().substring(message.getSrc().lastIndexOf('\\') + 1);
                    if(message.getSrc().equals(fileName)){
                        fileName = message.getSrc().substring(message.getSrc().lastIndexOf("/") + 1);
                    }
                    System.out.println(message.getTime() + " " + message.getSenderId() + " sent you a file: " + fileName);
                }
                /*显示文件列表*/
                else if (MessageType.SHOW_FILE_LIST.equals(message.getType())){
                    String fileName = message.getSrc().substring(message.getSrc().lastIndexOf('\\') + 1);
                    if(message.getSrc().equals(fileName)){
                        fileName = message.getSrc().substring(message.getSrc().lastIndexOf("/") + 1);
                    }
                    System.out.println(message.getContent() + "\t\t" + message.getTime() + "\t" + message.getSenderId() + "\t\t\t" + fileName);
                }
                /*下载文件*/
                else if (MessageType.DOWNLOAD_FILE.equals(message.getType())){
                    String fileName = message.getSrc().substring(message.getSrc().lastIndexOf('\\') + 1);
                    if(message.getSrc().equals(fileName)){
                        fileName = message.getSrc().substring(message.getSrc().lastIndexOf("/") + 1);
                    }
                    if(message.getSrc().equals(fileName)){
                        fileName = message.getSrc().substring(message.getSrc().lastIndexOf("/") + 1);
                    }
                    FileOutputStream fileOutputStream = new FileOutputStream(message.getSrc());
                    fileOutputStream.write(message.getFileBytes());
                    fileOutputStream.close();
                    System.out.println("succeed to download " + fileName + " at path " + message.getSrc());
                }
                /*用户离线*/
                else if (MessageType.USER_NOT_ONLINE.equals(message.getType())){
                    System.out.println("user " + message.getSenderId() + " is not online");
                }
                /*打电话*/
                else if (MessageType.CALL.equals(message.getType())){
                    System.out.println("user " + message.getSenderId() + " is calling you. Enter y/Y to answer or enter n/N to reject.");
                    Message mes = new Message();
                    mes.setSenderId(message.getReceiverId());
                    mes.setReceiverId(message.getSenderId());
                    Scanner scanner = new Scanner(System.in);
                    boolean loop = true;
                    while(loop){
                        String choice = scanner.nextLine();
                        if (choice.equals("y")|| choice.equals("Y")){

                            mes.setType(MessageType.CALL_Y);
                            try {
                                callSocket = new Socket(InetAddress.getByName("162.14.77.42"),8888);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            ObjectOutputStream oos = new ObjectOutputStream(callSocket.getOutputStream());
                            User user = new User();
                            user.setUserId(message.getReceiverId());
                            oos.writeObject(user);

                            clientCallThread = new ClientCallThread(message.getReceiverId(), callSocket);
                            ManageService.addClientCallThread(message.getReceiverId(), clientCallThread);
                            clientCallThread.start();
                            break;
                        }else if (choice.equals("n") || choice.equals("N") ){
                            mes.setType(MessageType.CALL_N);
                            break;
                        }else {
                            System.out.println("wrong format. please enter again ");
                        }
                    }
                    ClientConnectServerThread clientConnectServerThread = ManageService.getClientConnectServiceThread(mes.getSenderId());
                    ObjectOutputStream oos = new ObjectOutputStream(clientConnectServerThread.getSocket().getOutputStream());
                    oos.writeObject(mes);
                }
                /*接电话*/
                else if (MessageType.CALL_Y.equals(message.getType())){
                    try {
                        callSocket = new Socket(InetAddress.getByName("162.14.77.42"),8888);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ObjectOutputStream oos = new ObjectOutputStream(callSocket.getOutputStream());
                    User user = new User();
                    user.setUserId(message.getReceiverId());
                    oos.writeObject(user);
                    clientCallThread = new ClientCallThread(message.getReceiverId(), callSocket);
                    ManageService.addClientCallThread(message.getReceiverId(), clientCallThread);
                    clientCallThread.start();
                }
                /*不接*/
                else if (MessageType.CALL_N.equals(message.getType())){
                    System.out.println("user " + message.getSenderId()+ " can not get through ");
                }
                /*挂电话*/
                else if (MessageType.HANG_UP.equals(message.getType())){
                    if (message.getSenderId() != null){
                        System.out.println("user " + message.getSenderId() + " hung up the call");
                    }else {
                        System.out.println("you hung up the call");
                    }

                    ManageService.getClientCallThread(message.getReceiverId()).endLoop();
                    ManageService.removeClientCallThread(message.getReceiverId());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
