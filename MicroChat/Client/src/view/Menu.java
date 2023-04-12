package view;

import service.*;
import utils.Utility;

/**
 * 客户端主进程
 * 用于交互
 */
public class Menu {

    private boolean loop = true;
    private String key = "";
    private SignInService signInService = new SignInService();
    private ClientMessageService clientMessageService = new ClientMessageService();
    private ClientFileService clientFileService = new ClientFileService();
    private ClientCallService clientCallService = new ClientCallService();
    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        new Menu().mainMenu();
    }
    private void mainMenu(){

        while (loop){
            System.out.println("\t\tWelcome to MicroChat! ");
            System.out.println("1-sign in");
            System.out.println("2-sign up");
            System.out.println("0-exit");

            key = Utility.readString(1);

            switch (key){
                case "1":
                    System.out.println("Id:");
                    String userId = Utility.readString(10);
                    System.out.println("password:");
                    String passwd = Utility.readString(50);
                    if(signInService.checkUser(userId,passwd)){
                        System.out.println("please enter \"-h\" to get help");
                        while (loop){
                            String input = Utility.readString(100);
                            if("-h".equals(input)){
                                System.out.println("-e: exit the MicroChat");
                                System.out.println("-f: show the message format");
                                System.out.println("-l: show the file list");
                                System.out.println("-d index path: download the file");
                                System.out.println("-hp: hang up the call");
                            }else if ("-f".equals(input)) {
                                System.out.println("Public chat format: content ");
                                System.out.println("Private chat format: @receiverId:content");
                                System.out.println("send file format: @receiverId:FilePath -f");
                                System.out.println("Call format: call@receiverId");
                            }else if ("-e".equals(input)){
                                signInService.myExit(userId);
                            }else if ("-l".equals(input)){
                                clientFileService.showFileList(userId);
                                System.out.println("index\ttime\t\t\t\t\t\t\tsenderId\tfileName");
                            }else if (input.length() >= 2 && "-d".equals(input.substring(0,2))){
                                clientFileService.downloadFile(userId, input);
                            }else if (input.length() >= 4 && "call".equals(input.substring(0,4))){
                                clientCallService.Call(userId, input);
                            }else if ("-hp".equals(input)){
                                clientCallService.HangUp(userId);

                            }
                            else {
                                clientMessageService.sendMessage(userId, input);
                            }
                        }
                    }
                    break;
                case "2":
                    System.out.println("create your Id:");
                    String newUserId = Utility.readString(10);
                    System.out.println("set your password:");
                    String newPasswd = Utility.readString(50);
                    signInService.createUser(newUserId,newPasswd);
                    break;
                case "0":
                    loop = false;
                    break;
            }
        }
    }
}
