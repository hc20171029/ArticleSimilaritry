package io.transwarp.ds.index;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Paths;

public class ArticleIndexer {

    private static final String TITLE="title";
    private static final String CONTENT="content";

    private Analyzer analyzer;
    private String indexDir;

    private IndexWriter writer;

    public ArticleIndexer(String indexDir,Analyzer analyzer){
        this.analyzer=analyzer;
        this.indexDir=indexDir;
        try {
            Directory dir = FSDirectory.open(Paths.get(indexDir));
            IndexWriterConfig iwc=new IndexWriterConfig(analyzer);
            iwc.setUseCompoundFile(false);

            writer=new IndexWriter(dir,iwc);
        }catch (Exception e){
            System.out.println("Indexer initialize Wrong");
        }
    }

    public void indexOneArticle(String title,String content){
        Document doc=new Document();

        FieldType title_ft=new FieldType();
        title_ft.setStored(true);
        title_ft.setOmitNorms(true);
        title_ft.setStoreTermVectors(false);
        title_ft.setStoreTermVectorOffsets(false);
        title_ft.setStoreTermVectorPositions(false);
        title_ft.setStoreTermVectorPayloads(false);
        title_ft.setTokenized(false);
        title_ft.setIndexOptions(IndexOptions.DOCS);
        title_ft.setDocValuesType(DocValuesType.NONE);

        title_ft.freeze();
        doc.add(new Field(TITLE,title,title_ft));

        FieldType content_ft=new FieldType();
        content_ft.setStored(true);
        content_ft.setOmitNorms(true);
        content_ft.setStoreTermVectors(true);
        content_ft.setStoreTermVectorOffsets(true);
        content_ft.setStoreTermVectorPositions(true);
        content_ft.setStoreTermVectorPayloads(true);
        content_ft.setTokenized(true);
        content_ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
        content_ft.setDocValuesType(DocValuesType.NONE);
        content_ft.freeze();
        doc.add(new Field(CONTENT,content,content_ft));

        try{
            writer.addDocument(doc);
        }
        catch (Exception e){
            System.out.println("title="+title);
            System.out.println("ArticleIndexer index article exception!");
            System.out.println(e.getMessage());
        }
    }
    public void commitALLDoc(){
        try {
            writer.commit();
            writer.close();
        }catch (Exception e){
            System.out.println("writer commit failed");
        }
    }


}
