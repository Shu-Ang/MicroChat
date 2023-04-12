package common;

public interface MessageType {
    String SIGN_IN_SUCCEED = "100";
    String SIGN_IN_FAIL = "101";
    String SIGN_UP_SUCCEED = "200";
    String SIGN_UP_FAIL = "201";
    String PRIVATE_CHAT = "000";
    String PUBLIC_CHAT = "001";
    String MESSAGE_FILE = "300";
    String SHOW_FILE_LIST = "301";
    String DOWNLOAD_FILE = "302";
    String USER_NOT_EXIST = "400";
    String USER_NOT_ONLINE = "401";
    String CALL = "501";
    String CALL_Y = "502";
    String CALL_N = "503";
    String HANG_UP = "504";
    String EXIT = "999";
}
