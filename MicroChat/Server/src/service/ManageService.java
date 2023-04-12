package service;

import common.Message;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理线程
 */
public class ManageService {

    /*hm管理服务器连接客户端线程*/
    private static HashMap<String, ServerConnectClientThread> hm = new HashMap<>();

    public static void addServerConnectClientThread(String userId, ServerConnectClientThread serverConnectClientThread){

        hm.put(userId,serverConnectClientThread);

    }

    public static HashMap<String, ServerConnectClientThread> getHm() {
        return hm;
    }

    public static ServerConnectClientThread getServerConnectClientThread(String userId){

        return hm.get(userId);

    }

    public static void removeServerConnectClientThread(String userId){
        hm.remove(userId);
    }

    public static boolean isOnline(String userId){
        if (userId == null) return true;
        return hm.containsKey(userId);
    }


    /*db管理离线消息*/
    private static ConcurrentHashMap<String, ArrayList<Message>> db = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String, ArrayList<Message>> getDb() {
        return db;
    }

    public static void removeOutLineMessage(String userId){
        db.remove(userId);
    }

    public static void addOutLineMessage(String userId, Message message){
        if (!db.containsKey(userId)){
            ArrayList<Message> arrayList = new ArrayList<>();
            arrayList.add(message);
            db.put(userId, arrayList);
        }else {
            db.get(userId).add(message);
        }
    }

    /*fileDb管理文件*/
    private static ConcurrentHashMap<String, ArrayList<Message>> fileDb = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String, ArrayList<Message>> getFileDb() {
        return fileDb;
    }

    public static void addFile(String userId, Message message){
        if (!fileDb.containsKey(userId)){
            ArrayList<Message> arrayList = new ArrayList<>();
            arrayList.add(message);
            fileDb.put(userId, arrayList);
        }else {
            fileDb.get(userId).add(message);
        }
    }

    /*pairsHm管理语音通话双方ID*/
    private static ConcurrentHashMap<String, String> pairsHm = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String, String> getPairsHm(){ return pairsHm; }

    public static String getPairs(String userId){ return pairsHm.get(userId); }

    public static void addPairs(String userIdA, String userIdB){
        pairsHm.put(userIdA, userIdB);
        pairsHm.put(userIdB, userIdA);
    }
    public static void removePairs(String userIdA, String userIdB){
        pairsHm.remove(userIdA);
        pairsHm.remove(userIdB);
    }


    /*serverCallHm管理服务器语音通话线程*/
    private static ConcurrentHashMap<String, ServerCallThread> serverCallHm = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String, ServerCallThread> getServerCallHm(){ return  serverCallHm; }

    public static ServerCallThread getServerCallThread(String userId){ return serverCallHm.get(userId); }

    public static void addServerCallThread(String userId, ServerCallThread serverCallThread){
        serverCallHm.put(userId, serverCallThread);
    }

    public static void removeServerCallThread(String userId){
        serverCallHm.remove(userId);
    }

}
