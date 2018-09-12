package io.transwarp.ds.util;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;

public class IndexerUtil {

    private static String TITLE="title";
    private static String CONTENT="content";
    public static void index(String data_dir,String indexed_dir,Analyzer analyzer){

        try {
            Directory dir = FSDirectory.open(Paths.get(indexed_dir));
            IndexWriterConfig iwc=new IndexWriterConfig(analyzer);
            iwc.setUseCompoundFile(false);
            IndexWriter writer=new IndexWriter(dir,iwc);

            index(writer,data_dir);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void index(IndexWriter writer,String datadir) throws Exception{
        File[] files=new File(datadir).listFiles();
        int count=0;
        System.out.println("begin index files");
        for(File f:files){
            if(((count++)%100)==0){
                System.out.println("index 100 files!!");
            }
            indexFile(f,writer);
            //System.out.println("Index 1 file!");
        }
        System.out.println("before writer commit");
        writer.commit();
        System.out.println("writer commit end");
        writer.close();
    }

    /*
        Index all content of one doc into Index,include all char

     */
    private static void indexFile(File f,IndexWriter writer) throws Exception{
        String title=f.getName();

        int length=(int)f.length();
        FileReader inTwo = new FileReader(f);
        char ch[] = new char[length];
        inTwo.read(ch);
        String content = new String(ch);

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

        //System.out.println("content="+content.length()+"\n"+"------------------------------------------------------------------");
        try{
            writer.addDocument(doc);
        }
        catch (Exception e){
            System.out.println("title="+title);
            System.out.println("writer.addDocument exception!");
            System.out.println(e.getMessage());
        }

       inTwo.close();
    }

    public static IndexReader getReader(String indexDir) throws Exception{
        Directory dir=FSDirectory.open(Paths.get(indexDir));
        IndexReader reader=DirectoryReader.open(dir);
       // IndexSearcher searcher=new IndexSearcher(reader);
        return reader;
    }


}
