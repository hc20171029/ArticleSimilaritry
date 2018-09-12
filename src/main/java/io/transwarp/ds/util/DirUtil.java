package io.transwarp.ds.util;

import java.io.File;

public class DirUtil {

    public static long getTotalSizeOf_OneKindFiles_InDir(final File dir,final String[] postfixs){

        final File[] children = dir.listFiles();
        long total = 0;


        System.out.println(postfixs[0]);
        for (final File child : children) {
            String names[]=child.getName().split("\\.");
            for(String postfix:postfixs) {
                if (names.length==2&&names[1].equals(postfix)){
                    total+=child.length();
                    System.out.println(child.getName()+" "+child.length());
                    break;
                }
            }

        }
        System.out.println("total="+total);
        System.out.println("-----------------------------------------");
        return total;
    }

    public static  long getTotalSizeOfFilesInDir(final File file) {
        if (file.isFile())
            return file.length();
        final File[] children = file.listFiles();
        long total = 0;
        if (children != null)
            for (final File child : children)
                total += getTotalSizeOfFilesInDir(child);
        return total;
    }
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        boolean result=dir.delete();
        return result;
    }
    public static boolean createDir(File dir){

        return dir.mkdir();
    }

    public static void main(String args[]){
        long size=DirUtil.getTotalSizeOf_OneKindFiles_InDir(new File("./data"),new String[]{"txt"});
        System.out.println(size);
    }
}
