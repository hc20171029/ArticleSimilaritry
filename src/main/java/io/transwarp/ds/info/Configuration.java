package io.transwarp.ds.info;

import java.util.HashMap;
import java.util.Map;

public class Configuration {
    private Map<String,String> confs;
    public Configuration(){
        confs=new HashMap();
    }
    public void put(String key,String val){
        confs.put(key,val);
    }
    public String get(String key){
        return confs.get(key);
    }
}
