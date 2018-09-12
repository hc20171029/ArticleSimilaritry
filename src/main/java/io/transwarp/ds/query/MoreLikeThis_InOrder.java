package io.transwarp.ds.query;


import io.transwarp.ds.service.ValidChecker;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.util.PriorityQueue;


import java.io.IOException;
import java.util.*;

/*
MoreLikeThis的改编版本
不再依据tf*idf值高低作为筛选依据，而是将一篇文章顺序切分为一个个有效单元，使用SpanQuery限定，后面的Token不能出现在前面的
Token之前

作者：hc
2018年9月10日
 */
public class MoreLikeThis_InOrder {


    public static final int DEFAULT_MAX_NUM_TOKENS_PARSED = 5000;

    public static final int DEFAULT_MIN_TERM_FREQ = 2;

    public static final int DEFAULT_MIN_DOC_FREQ = 5;

    public static final int DEFAULT_MAX_DOC_FREQ = Integer.MAX_VALUE;

    public static final boolean DEFAULT_BOOST = false;

    public static final String[] DEFAULT_FIELD_NAMES = new String[]{"contents"};

    public static final int DEFAULT_MIN_WORD_LENGTH = 0;

    public static final int DEFAULT_MAX_WORD_LENGTH = 0;

    public static final Set<?> DEFAULT_STOP_WORDS = null;

    private int slop;

    private ValidChecker validChecker;

    private Set<?> stopWords = DEFAULT_STOP_WORDS;

    public static final int DEFAULT_MAX_QUERY_TERMS = 25;

    private Analyzer analyzer = null;

    private int minTermFreq = DEFAULT_MIN_TERM_FREQ;

    private int minDocFreq = DEFAULT_MIN_DOC_FREQ;

    private int maxDocFreq = DEFAULT_MAX_DOC_FREQ;

    private boolean boost = DEFAULT_BOOST;

    private String[] fieldNames = DEFAULT_FIELD_NAMES;

    private int maxNumTokensParsed = DEFAULT_MAX_NUM_TOKENS_PARSED;

    private int minWordLen = DEFAULT_MIN_WORD_LENGTH;

    private int maxWordLen = DEFAULT_MAX_WORD_LENGTH;

    private int maxQueryTerms = DEFAULT_MAX_QUERY_TERMS;



    /**
     * For idf() calculations.
     */
    private TFIDFSimilarity similarity;// = new DefaultSimilarity();

    /**
     * IndexReader to use
     */
    private final IndexReader ir;

    /**
     * Boost factor to use when boosting the terms
     */
    private float boostFactor = 1;

    private PriorityQueue<NewMoreLikeThis.ScoreTerm> scoreTermsQueue=null;

    public int getSlop() {
        return slop;
    }

    public void setSlop(int slop) {
        this.slop = slop;
    }

    public ValidChecker getValidChecker() {
        return validChecker;
    }

    public void setValidChecker(ValidChecker validChecker) {
        this.validChecker = validChecker;
    }

    public float getBoostFactor() {
        return boostFactor;
    }

    /**
     * Sets the boost factor to use when boosting terms
     *
     * @see #getBoostFactor()
     */

    private boolean useSentenceFuzzy=false;

    public void setUseSentenceFuzzy(boolean useSentenceFuzzy){
        this.useSentenceFuzzy=useSentenceFuzzy;
    }



    public void setBoostFactor(float boostFactor) {
        this.boostFactor = boostFactor;
    }


    public PriorityQueue<NewMoreLikeThis.ScoreTerm> getScoreTermsQueue(){
        return scoreTermsQueue;
    }

    /**
     * Constructor requiring an IndexReader.
     */
    public MoreLikeThis_InOrder(IndexReader ir) {
        this(ir, new ClassicSimilarity());
    }

    public MoreLikeThis_InOrder(IndexReader ir, TFIDFSimilarity sim) {
        this.ir = ir;
        this.similarity = sim;
    }


    public TFIDFSimilarity getSimilarity() {
        return similarity;
    }

    public void setSimilarity(TFIDFSimilarity similarity) {
        this.similarity = similarity;
    }

    /**
     * Returns an analyzer that will be used to parse source doc with. The default analyzer
     * is not set.
     *
     * @return the analyzer that will be used to parse source doc with.
     */
    public Analyzer getAnalyzer() {
        return analyzer;
    }

    public void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    /**
     * Returns the frequency below which terms will be ignored in the source doc. The default
     * frequency is the {@link #DEFAULT_MIN_TERM_FREQ}.
     *
     * @return the frequency below which terms will be ignored in the source doc.
     */
    public int getMinTermFreq() {
        return minTermFreq;
    }

    /**
     * Sets the frequency below which terms will be ignored in the source doc.
     *
     * @param minTermFreq the frequency below which terms will be ignored in the source doc.
     */
    public void setMinTermFreq(int minTermFreq) {
        this.minTermFreq = minTermFreq;
    }

    /**
     * Returns the frequency at which words will be ignored which do not occur in at least this
     * many docs. The default frequency is {@link #DEFAULT_MIN_DOC_FREQ}.
     *
     * @return the frequency at which words will be ignored which do not occur in at least this
     *         many docs.
     */
    public int getMinDocFreq() {
        return minDocFreq;
    }

    /**
     * Sets the frequency at which words will be ignored which do not occur in at least this
     * many docs.
     *
     * @param minDocFreq the frequency at which words will be ignored which do not occur in at
     * least this many docs.
     */
    public void setMinDocFreq(int minDocFreq) {
        this.minDocFreq = minDocFreq;
    }

    /**
     * Returns the maximum frequency in which words may still appear.
     * Words that appear in more than this many docs will be ignored. The default frequency is
     * {@link #DEFAULT_MAX_DOC_FREQ}.
     *
     * @return get the maximum frequency at which words are still allowed,
     *         words which occur in more docs than this are ignored.
     */
    public int getMaxDocFreq() {
        return maxDocFreq;
    }

    /**
     * Set the maximum frequency in which words may still appear. Words that appear
     * in more than this many docs will be ignored.
     *
     * @param maxFreq the maximum count of documents that a term may appear
     * in to be still considered relevant
     */
    public void setMaxDocFreq(int maxFreq) {
        this.maxDocFreq = maxFreq;
    }

    /**
     * Set the maximum percentage in which words may still appear. Words that appear
     * in more than this many percent of all docs will be ignored.
     *
     * @param maxPercentage the maximum percentage of documents (0-100) that a term may appear
     * in to be still considered relevant
     */
    public void setMaxDocFreqPct(int maxPercentage) {
        this.maxDocFreq = maxPercentage * ir.numDocs() / 100;
    }


    /**
     * Returns whether to boost terms in query based on "score" or not. The default is
     * {@link #DEFAULT_BOOST}.
     *
     * @return whether to boost terms in query based on "score" or not.
     * @see #setBoost
     */
    public boolean isBoost() {
        return boost;
    }

    /**
     * Sets whether to boost terms in query based on "score" or not.
     *
     * @param boost true to boost terms in query based on "score", false otherwise.
     * @see #isBoost
     */
    public void setBoost(boolean boost) {
        this.boost = boost;
    }

    /**
     * Returns the field names that will be used when generating the 'More Like This' query.
     * The default field names that will be used is {@link #DEFAULT_FIELD_NAMES}.
     *
     * @return the field names that will be used when generating the 'More Like This' query.
     */
    public String[] getFieldNames() {
        return fieldNames;
    }

    /**
     * Sets the field names that will be used when generating the 'More Like This' query.
     * Set this to null for the field names to be determined at runtime from the IndexReader
     * provided in the constructor.
     *
     * @param fieldNames the field names that will be used when generating the 'More Like This'
     * query.
     */
    public void setFieldNames(String[] fieldNames) {
        this.fieldNames = fieldNames;
    }

    /**
     * Returns the minimum word length below which words will be ignored. Set this to 0 for no
     * minimum word length. The default is {@link #DEFAULT_MIN_WORD_LENGTH}.
     *
     * @return the minimum word length below which words will be ignored.
     */
    public int getMinWordLen() {
        return minWordLen;
    }

    /**
     * Sets the minimum word length below which words will be ignored.
     *
     * @param minWordLen the minimum word length below which words will be ignored.
     */
    public void setMinWordLen(int minWordLen) {
        this.minWordLen = minWordLen;
    }

    /**
     * Returns the maximum word length above which words will be ignored. Set this to 0 for no
     * maximum word length. The default is {@link #DEFAULT_MAX_WORD_LENGTH}.
     *
     * @return the maximum word length above which words will be ignored.
     */
    public int getMaxWordLen() {
        return maxWordLen;
    }

    /**
     * Sets the maximum word length above which words will be ignored.
     *
     * @param maxWordLen the maximum word length above which words will be ignored.
     */
    public void setMaxWordLen(int maxWordLen) {
        this.maxWordLen = maxWordLen;
    }

    /**
     * Set the set of stopwords.
     * Any word in this set is considered "uninteresting" and ignored.
     * Even if your Analyzer allows stopwords, you might want to tell the MoreLikeThis code to ignore them, as
     * for the purposes of document similarity it seems reasonable to assume that "a stop word is never interesting".
     *
     * @param stopWords set of stopwords, if null it means to allow stop words
     * @see #getStopWords
     */
    public void setStopWords(Set<?> stopWords) {
        this.stopWords = stopWords;
    }

    /**
     * Get the current stop words being used.
     *
     * @see #setStopWords
     */
    public Set<?> getStopWords() {
        return stopWords;
    }


    /**
     * Returns the maximum number of query terms that will be included in any generated query.
     * The default is {@link #DEFAULT_MAX_QUERY_TERMS}.
     *
     * @return the maximum number of query terms that will be included in any generated query.
     */
    public int getMaxQueryTerms() {
        return maxQueryTerms;
    }

    /**
     * Sets the maximum number of query terms that will be included in any generated query.
     *
     * @param maxQueryTerms the maximum number of query terms that will be included in any
     * generated query.
     */
    public void setMaxQueryTerms(int maxQueryTerms) {
        this.maxQueryTerms = maxQueryTerms;
    }

    /**
     * @return The maximum number of tokens to parse in each example doc field that is not stored with TermVector support
     * @see #DEFAULT_MAX_NUM_TOKENS_PARSED
     */
    public int getMaxNumTokensParsed() {
        return maxNumTokensParsed;
    }

    /**
     * @param i The maximum number of tokens to parse in each example doc field that is not stored with TermVector support
     */
    public void setMaxNumTokensParsed(int i) {
        maxNumTokensParsed = i;
    }



    /**
     * Return a query that will return docs like the passed Readers.
     * This was added in order to treat multi-value fields.
     *
     * @return a query that will return docs like the passed Readers.
     */
    public Query like(String fieldName, String fieldVal) throws IOException {

        return createQuery(fieldName,fieldVal);
    }

    /**
     * Create the More like query from a PriorityQueue
     */

    private Query createQuery(String fieldName,String fieldVal) {
       TokenStream tokenStream=analyzer.tokenStream(fieldName,fieldVal);
       CharTermAttribute termAtt = tokenStream.addAttribute(CharTermAttribute.class);

       SpanTermQuery pre_stq=null;

       BooleanQuery.Builder queryBuilder=new BooleanQuery.Builder();

       int count=0;
       try {
           tokenStream.reset();
           while (tokenStream.incrementToken()) {

               String token=termAtt.toString();
               boolean isValid=true;//默认单元有效
               if(validChecker!=null){//如果用户设置了检测器，就进行有效性检查，否则不检查
                   isValid=validChecker.checkValid(token);
               }
               if(!isValid){
                   continue;
               }
               if(count==0){
                   count++;
                   pre_stq=new SpanTermQuery(new Term(fieldName,token));
                   continue;
               }
               SpanTermQuery current_stq=new SpanTermQuery(new Term(fieldName,token));
               SpanNearQuery snq=new SpanNearQuery(new SpanQuery[]{pre_stq,current_stq},slop,false);

               queryBuilder.add(snq, BooleanClause.Occur.SHOULD);
               pre_stq=current_stq;
           }
       }catch (Exception e){
           System.out.println("tokenStream.increment() occur an exception!");
           System.out.println(e.getMessage());
       }
       return queryBuilder.build();
    }
    private boolean isNoiseWord(String term) {
        int len = term.length();
        if (minWordLen > 0 && len < minWordLen) {
            return true;
        }
        if (maxWordLen > 0 && len > maxWordLen) {
            return true;
        }
        return stopWords != null && stopWords.contains(term);
    }
}
