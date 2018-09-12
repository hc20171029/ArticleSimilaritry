package io.transwarp.ds.analyzer.smartAnalyzer;

import java.io.IOException;

import java.util.*;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.seg.common.Term;
import io.transwarp.ds.util.CharUtil;
import org.apache.lucene.analysis.CharacterUtils.CharacterBuffer;
import org.apache.lucene.analysis.CharacterUtils;

import org.apache.lucene.analysis.Tokenizer;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.util.AttributeFactory;

import static com.hankcs.hanlp.corpus.tag.Nature.nx;

/**
 关键句子Tokenizer
 */
public class SmartCharTokenizer extends Tokenizer {


    public SmartCharTokenizer() {
    }


    public SmartCharTokenizer(AttributeFactory factory) {
        super(factory);
    }


    public static org.apache.lucene.analysis.util.CharTokenizer fromTokenCharPredicate(final IntPredicate tokenCharPredicate) {
        return fromTokenCharPredicate(DEFAULT_TOKEN_ATTRIBUTE_FACTORY, tokenCharPredicate);
    }

    public static org.apache.lucene.analysis.util.CharTokenizer fromTokenCharPredicate(AttributeFactory factory, final IntPredicate tokenCharPredicate) {
        return fromTokenCharPredicate(factory, tokenCharPredicate, IntUnaryOperator.identity());
    }

    public static org.apache.lucene.analysis.util.CharTokenizer fromTokenCharPredicate(final IntPredicate tokenCharPredicate, final IntUnaryOperator normalizer) {
        return fromTokenCharPredicate(DEFAULT_TOKEN_ATTRIBUTE_FACTORY, tokenCharPredicate, normalizer);
    }

    public static org.apache.lucene.analysis.util.CharTokenizer fromTokenCharPredicate(AttributeFactory factory, final IntPredicate tokenCharPredicate, final IntUnaryOperator normalizer) {
        Objects.requireNonNull(tokenCharPredicate, "predicate must not be null.");
        Objects.requireNonNull(normalizer, "normalizer must not be null");
        return new org.apache.lucene.analysis.util.CharTokenizer(factory) {
            @Override
            protected boolean isTokenChar(int c) {
                return tokenCharPredicate.test(c);
            }

            @Override
            protected int normalize(int c) {
                return normalizer.applyAsInt(c);
            }
        };
    }

    public static org.apache.lucene.analysis.util.CharTokenizer fromSeparatorCharPredicate(final IntPredicate separatorCharPredicate) {
        return fromSeparatorCharPredicate(DEFAULT_TOKEN_ATTRIBUTE_FACTORY, separatorCharPredicate);
    }


    public static org.apache.lucene.analysis.util.CharTokenizer fromSeparatorCharPredicate(AttributeFactory factory, final IntPredicate separatorCharPredicate) {
        return fromSeparatorCharPredicate(factory, separatorCharPredicate, IntUnaryOperator.identity());
    }

    public static org.apache.lucene.analysis.util.CharTokenizer fromSeparatorCharPredicate(final IntPredicate separatorCharPredicate, final IntUnaryOperator normalizer) {
        return fromSeparatorCharPredicate(DEFAULT_TOKEN_ATTRIBUTE_FACTORY, separatorCharPredicate, normalizer);
    }

    public static org.apache.lucene.analysis.util.CharTokenizer fromSeparatorCharPredicate(AttributeFactory factory, final IntPredicate separatorCharPredicate, final IntUnaryOperator normalizer) {
        return fromTokenCharPredicate(factory, separatorCharPredicate.negate(), normalizer);
    }

    private int offset = 0, bufferIndex = 0, dataLen = 0, finalOffset = 0;
    private static final int MAX_WORD_LEN = 255;
    private static final int IO_BUFFER_SIZE = 4096;

    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);

    private final CharacterBuffer ioBuffer = CharacterUtils.newCharacterBuffer(IO_BUFFER_SIZE);

    protected boolean isTokenChar(int c){
        return true;
    };

    protected int normalize(int c) {
        return c;
    }

    private int tokenId=0;

    @Override
    public final boolean incrementToken() throws IOException {
        clearAttributes();
        if(!hasSplitToken){
            //////////////////////////////////////////////////////////////////////////
            StringBuilder article=new StringBuilder();
            char[] article_buf=new char[100];
            int len=100;
            int count=0;
            while(count!=-1){
                try {
                    count = input.read(article_buf, 0, len);
                    String str = new String(article_buf, 0, count);
                    article.append(str);
                    offset += count;
                }catch (IndexOutOfBoundsException e){
                    len/=2;
                    continue;
                }
            }
            /////////////////////////////////////////////////////////////////////////
            tokens=get_tokens(article.toString());
            hasSplitToken=true;
        }

        if(tokenId>=tokens.size()){
            hasSplitToken=false;
            tokenId=0;
            tokens.clear();
            return false;
        }

        int start = -1;
        int end = -1;
        char[] buffer = termAtt.buffer();

        String str=tokens.get(tokenId++);
        int strLength=str.length();

        ///////////////////////////////////////////////////////////////////
        CharUtil.FILLRESULT result =null;
        while(true) {
            result = CharUtil.fillCharArr(buffer, str, 0, str.length() - 1);
            if(result==CharUtil.FILLRESULT.OUT_OF_LENGTH){
                buffer=termAtt.resizeBuffer(strLength+2);
                continue;
            }
            if(result== CharUtil.FILLRESULT.SUCCESS){
                break;
            }
        }
        //这段代码是将str添到buffer中
        /////////////////////////////////////////////////////////////////////////////////

        start=0;
        end=strLength;

        termAtt.setLength(strLength);
        assert start != -1;
        offsetAtt.setOffset(correctOffset(start), finalOffset = correctOffset(end));
        return true;
    }

    @Override
    public final void end() throws IOException {
        super.end();
        offsetAtt.setOffset(finalOffset, finalOffset);
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        bufferIndex = 0;
        offset = 0;
        dataLen = 0;
        finalOffset = 0;
        ioBuffer.reset(); // make sure to reset the IO buffer!!
    }

    private List<String> tokens=new ArrayList<String>();
    private boolean hasSplitToken=false;


    private List<String> get_tokens(String article){
        tokens.clear();

        List<Term> terms=HanLP.segment(article);

        boolean mark_begin=false;
        StringBuilder mark_sb=new StringBuilder();
        for(Term t:terms){
            String word=t.word;

            if(!mark_begin&&(word.equals("《")||word.equals("“")||word.equals("”"))){
                mark_begin=true;
                continue;
            }
            if(mark_begin&&(word.equals("》")||word.equals("”")||word.equals("“"))){
                tokens.add(mark_sb.toString());
                mark_sb.delete(0,mark_sb.length());
                mark_begin=false;
                continue;
            }
            if(mark_begin){
                mark_sb.append(word);
                continue;
            }
            if(shouldInclude(t)){
                tokens.add(word);
            }
        }

        return tokens;
    }
    private boolean shouldInclude(Term t){
        boolean standard_should=CoreStopWordDictionary.shouldInclude(t);

        boolean isNx=t.nature==nx;

        if(!standard_should||isNx)
            return false;
        return true;
    }
}
