package io.transwarp.ds.test;

import io.transwarp.ds.analyzer.Sentence_Analyzer;
import io.transwarp.ds.analyzer.smartAnalyzer.SmartCharAnalyzer;
import io.transwarp.ds.tool.ArticleSimilarity;
import io.transwarp.ds.tool.ArticleSimilarity_Inorder;
import io.transwarp.ds.util.OutputUtil;
import io.transwarp.ds.util.StringUtil;
import io.transwarp.ds.vo.ScoreResult;

import java.util.List;

public class ArticleSimilarityTest {
    public  static void main(String args[]) throws Exception{
        test_article_noOrder();
        CheckResult.main(new String[]{});
    }
    public static void test_article_InOrder() throws Exception{
        String article=StringUtil.getStringFromTxt("./test_article/test.txt");
        ArticleSimilarity_Inorder.setIndex_dir("./index");
        ArticleSimilarity_Inorder.setSlop(5);//设置编辑距离容忍

        List<ScoreResult> results= ArticleSimilarity_Inorder.getSimilarDocs(article,100);
        OutputUtil outputUtil=new OutputUtil("./test_result");
        System.out.println("命中了"+results.size());
        for(ScoreResult result:results){
            outputUtil.writeStrTo(result.getId());
        }
    }
    public static void test_article_noOrder() throws Exception{
        String article=StringUtil.getStringFromTxt("./test_article/test.txt");
        ArticleSimilarity.setIndex_dir("./index");

        ArticleSimilarity.setMaxDocFreq(2000);//最大文档数目，这个值越大，筛选条件越低，入选的文档越多
        ArticleSimilarity.setMaxQueryTerms(50);//最大的Query数组，这个值越大，筛选条件越低，入选的文档越多
        ArticleSimilarity.setMinWordLen(5);//最小单元长度，这个值越大，筛选条件越高，入选的文档越少
        ArticleSimilarity.setMinDocFreq(0);//最小文档数目，这个值越大，筛选条件越高，入选的文档越少
        ArticleSimilarity.setMinTermFreq(1);//最小单元频率，这个值越大，筛选条件越高，入选的文档越少
        List<ScoreResult> results= ArticleSimilarity.getSimilarDocs(article,70);
        OutputUtil outputUtil=new OutputUtil("./test_result");

        System.out.println("命中了"+results.size());
        for(ScoreResult result:results){
            outputUtil.writeStrTo(result.getId());
        }
    }

}
