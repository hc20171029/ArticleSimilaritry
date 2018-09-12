package io.transwarp.ds.data;

import io.transwarp.ds.analyzer.Sentence_Analyzer;
import io.transwarp.ds.index.ArticleIndexer;
import io.transwarp.ds.util.DirUtil;
import io.transwarp.ds.util.StringUtil;
import org.apache.lucene.analysis.Analyzer;

import java.io.File;

public class LuceneIndexer_fromTxt {
    public static void main(String args[]){
        rebuildIndex("./data","./index",true);
    }
    public static boolean rebuildIndex(String dataDir,String indexDir,boolean appendIndex){
        File idxDir=new File(indexDir);
        if(!appendIndex){
            DirUtil.deleteDir(idxDir);
        }
        if(!idxDir.exists()){
            DirUtil.createDir(idxDir);
        }

        File dtDir=new File(dataDir);
        File dtFiles[]=dtDir.listFiles();

        Analyzer sentence_analyzer=new Sentence_Analyzer();
        ArticleIndexer article_indexer=new ArticleIndexer(indexDir,sentence_analyzer);

        int count=0;
        for(File f:dtFiles){
            String title=f.getName();
            String content= StringUtil.getStringFromTxt(f.getAbsolutePath());
            article_indexer.indexOneArticle(title,content);
            count++;
            if(count%100==0){
                System.out.println("Index 100 file!!!");
            }
        }
        article_indexer.commitALLDoc();
        System.out.println("Index build compelte");
        return true;
    }
}
