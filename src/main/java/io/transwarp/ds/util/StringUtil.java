package io.transwarp.ds.util;

import java.io.File;
import java.io.FileReader;

public class StringUtil {
    public static String getStringFromTxt(String f){
        try {
            File file=new File(f);
            int length=(int)file.length();

            FileReader inTwo = new FileReader(file);
            char ch[] = new char[length];
            inTwo.read(ch);
            String content = new String(ch);

            inTwo.close();
            return content;
        }catch (Exception e){
            System.out.println("StringUtil getString failed!!!");
        }
        return null;
    }
    public static void main(String args[]){
        String s="中国";
        System.out.println(s.length());

        s="ddddd";
        System.out.println(s.length());
    }
}
