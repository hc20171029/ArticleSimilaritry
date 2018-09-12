package io.transwarp.ds.tokenizer;


import org.apache.lucene.util.AttributeFactory;

public class Sentence_Tokenizer extends NewCharTokenizer {

    public Sentence_Tokenizer(){}
    public Sentence_Tokenizer(AttributeFactory factory) {
        super(factory);
    }
    @Override
    protected boolean isTokenChar(int i) {
      return !(i==9||i==10||i==32||i==44||i==65292||i==12290||i==59||i==58||i==65306||i==65307||i==63||i==65311);
    }
}
