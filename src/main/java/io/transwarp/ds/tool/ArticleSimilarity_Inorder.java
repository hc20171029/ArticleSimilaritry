package io.transwarp.ds.tool;


import io.transwarp.ds.analyzer.Sentence_Analyzer;
import io.transwarp.ds.query.MoreLikeThis_InOrder;
import io.transwarp.ds.util.IndexerUtil;
import io.transwarp.ds.util.SentenceUtil;
import io.transwarp.ds.vo.ScoreResult;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;

import java.util.ArrayList;
import java.util.List;

public class ArticleSimilarity_Inorder {

    private static String index_dir="./index";
    private static String title_field="title";
    private static String content_field="content";
    private static Analyzer st_analyzer=new Sentence_Analyzer();//默认是以句子分词

    private static int slop=5;


    public static Analyzer getAnalyzer() {
        return st_analyzer;
    }

    public static void setAnalyzer(Analyzer analyzer) {
        st_analyzer = analyzer;
    }

    public static int getSlop() {
        return slop;
    }

    public static void setSlop(int slop) {
        ArticleSimilarity_Inorder.slop = slop;
    }

    public static void setIndex_dir(String dir){
        index_dir=dir;
    }

    public static List<ScoreResult> getSimilarDocs(String content,int topn) throws Exception{
        if(index_dir==null||index_dir.replaceAll(" ","").equals("")){
            System.out.println("Error!index_dir must be set!");
            return new ArrayList<>();
        }
        Analyzer analyzer =st_analyzer;
        if(analyzer==null){
            System.out.println("Error！You must provide a NonNull Analyzer!");
            return new ArrayList<>();
        }
        IndexReader indexReader;
        try {
            indexReader = IndexerUtil.getReader(index_dir);
        }catch (Exception e){
            System.out.println("IndexReader init occur exception");
            System.out.println(e.getMessage());
            return new ArrayList<>();
        }

        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        MoreLikeThis_InOrder nmlt = new MoreLikeThis_InOrder(indexReader);

        nmlt.setAnalyzer(analyzer);
        nmlt.setBoost(true);
        nmlt.setUseSentenceFuzzy(true);
        nmlt.setSlop(slop);
        nmlt.setValidChecker(new SentenceUtil());

        long begin=System.currentTimeMillis();
        Query query = nmlt.like(content_field, content);
        ScoreDoc scoreDocs[] = indexSearcher.search(query, topn).scoreDocs;
        long end=System.currentTimeMillis();
        System.out.println("duration="+(end-begin)+"ms");

        List<ScoreResult> results=new ArrayList<ScoreResult>();

        for (int i = 0; i < scoreDocs.length; i++) {
            ScoreDoc scoreDoc = scoreDocs[i];
            int id = scoreDoc.doc;
            Document doc = indexReader.document(id);
            try {
                String title = doc.getField(title_field).stringValue();
                double score=scoreDoc.score;
                ScoreResult result=new ScoreResult(title,score);
                results.add(result);

            }catch (Exception e){
                System.out.println("make ScoreResult exception");
            }

        }
        return results;
    }

}
