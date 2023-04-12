package service;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理客户端线程
 */
public class ManageService {
    /*
    hm用于管理客户端与服务器端保持连接的主线程
     */
    private static HashMap<String, ClientConnectServerThread> hm = new HashMap<>();

    public static void addClientConnectServiceThread(String userId, ClientConnectServerThread serviceThread){
        hm.put(userId, serviceThread);
    }

    public static ClientConnectServerThread getClientConnectServiceThread(String userId){
        return hm.get(userId);
    }

    public static void removeClientConnectServerThread(String userId){
        hm.remove(userId);
    }


    /*
    clientCallHm用于管理客户端语音通话进程
     */
    private static ConcurrentHashMap<String, ClientCallThread> clientCallHm = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String, ClientCallThread> getClientCallHm(){ return clientCallHm; }
    public static ClientCallThread getClientCallThread(String userId){
        return clientCallHm.get(userId);
    }

    public static void addClientCallThread(String userId, ClientCallThread clientCallThread){
        clientCallHm.put(userId, clientCallThread);
    }

    public static void removeClientCallThread(String userId){
        clientCallHm.remove(userId);
    }
}
