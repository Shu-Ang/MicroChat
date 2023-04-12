package utils;

import java.util.Scanner;

/*
工具类：限制输入格式
 */
public class Utility {

    private static Scanner scanner;

    static {
        scanner=new Scanner(System.in);
    }

    public Utility(){

    }


    public static String readString(int limit) {
        return readKeyBoard(limit, false);
    }

    private static String readKeyBoard(int limit, boolean blankReturn){
        String line="";

        while (scanner.hasNextLine()){
            line=scanner.nextLine();
            if(line.length()==0){
                if(blankReturn){
                    return line;
                }
            }else {
                if(line.length()>=1 && line.length()<=limit){
                    break;
                }
                System.out.println("输入长度错误（不大于\"" + limit + "\"），请重新输入：");
            }
        }
        return line;
    }

}
