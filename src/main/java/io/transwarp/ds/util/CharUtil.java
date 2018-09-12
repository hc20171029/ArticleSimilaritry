package io.transwarp.ds.util;

public class CharUtil {

    public enum FILLRESULT{
        SUCCESS,WRONG_INTERVAL,OUT_OF_LENGTH
    }

    public static FILLRESULT  fillCharArr(char target[],String str,int begin,int end){
        if(begin<0||end>=str.length()){
            return FILLRESULT.WRONG_INTERVAL;
        }
        if((end-begin+1)>target.length){
            return FILLRESULT.OUT_OF_LENGTH;
        }
        char tmp_arr[]=str.toCharArray();

        for(int i=begin;i<=end;i++){
            target[i-begin]=tmp_arr[i];
        }

        return FILLRESULT.SUCCESS;
    }
}
