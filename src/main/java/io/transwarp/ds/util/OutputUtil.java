package io.transwarp.ds.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class OutputUtil {

    private String path;
    private FileWriter outOne;
    private BufferedWriter outTwo;
    public OutputUtil(String p) throws IOException{
        this.path=p;
        outOne=new FileWriter(new File(path));
        outTwo=new BufferedWriter(outOne);
    }

    public boolean writeStrTo(String content) throws Exception{


        outTwo.write(content);
        outTwo.newLine();
        outTwo.flush();
        return true;
    }
    public boolean writeStrToWithoutEn(String content) throws Exception{


        outTwo.write(content);

        outTwo.flush();
        return true;
    }
    protected void finalize() throws Throwable {
        System.out.println("OutputUtil is finalized!!!");
        outTwo.close();
        outOne.close();
    }
    public void close_all_stream() throws Exception{
        outTwo.close();
        outOne.close();
    }

}
