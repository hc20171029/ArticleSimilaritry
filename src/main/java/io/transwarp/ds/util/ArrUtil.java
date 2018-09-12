package io.transwarp.ds.util;

public class ArrUtil {

    public static int getLengthFor_NotFullArr(Object arr[]){
        int length=arr.length-1;

        while(arr[length--]==null){}
        return length+2;
    }
}
