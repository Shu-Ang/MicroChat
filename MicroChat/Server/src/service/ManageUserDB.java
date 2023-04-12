package service;

import java.io.*;

/**
 * 管理用户信息
 */
public class ManageUserDB {

    private static String fileName =  "/home/lighthouse/MicroChat/data/userDB.txt";
    private static File file = new File(fileName);

    /* 检查userId和password */
    public static boolean CheckId(String userId, String passwd){
        try{
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String id;
            String pwd;
            while ((id = br.readLine()) != null){
                pwd = br.readLine();
                if(id.equals(userId) && pwd.equals(passwd)) return true;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

    /* 创建用户 */
    public static boolean CreateId(String userId, String passwd){
        try{
            FileWriter fw = new FileWriter(file,true);
            if (isUserExist(userId) == true) return  false;     //若用户名已存在，则创建失败
            else {
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(userId + "\n");
                bw.write(passwd + "\n");
                bw.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return true;
    }

    /*检查用户是否存在*/
    public static boolean isUserExist(String userId){
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String id;
            while ((id = br.readLine()) != null){
                String pwd = br.readLine();
                if(id.equals(userId)) return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
