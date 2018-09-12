package io.transwarp.ds.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

public class CheckResult {
    public static void main(String args[]) throws Exception{
        FileReader inOne=new FileReader("./de_result");
        BufferedReader inTwo=new BufferedReader(inOne);

        String tmpLine=null;
        Set<String> de_results=new HashSet<String>();
        while((tmpLine=inTwo.readLine())!=null){
            de_results.add(tmpLine);
        }


        FileReader inThree=new FileReader("./test_result");
        BufferedReader inFour=new BufferedReader(inThree);

        int hit_count=0;
        while((tmpLine=inFour.readLine())!=null){
            if(de_results.contains(tmpLine)) hit_count++;
        }
        System.out.println("hit_count="+hit_count);
    }
}
