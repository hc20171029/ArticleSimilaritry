package io.transwarp.ds.analyzer;

import io.transwarp.ds.tokenizer.Sentence_Tokenizer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;

public class Sentence_Analyzer extends Analyzer {

    @Override
    protected TokenStreamComponents createComponents(String text) {
        Tokenizer tokenizer=new Sentence_Tokenizer();
        return new TokenStreamComponents(tokenizer);
    }

}
