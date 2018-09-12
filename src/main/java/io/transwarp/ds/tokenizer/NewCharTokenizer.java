package io.transwarp.ds.tokenizer;


import io.transwarp.ds.util.SentenceUtil;
import org.apache.lucene.analysis.CharacterUtils;
import org.apache.lucene.analysis.CharacterUtils.CharacterBuffer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LetterTokenizer;
import org.apache.lucene.analysis.core.LowerCaseTokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.util.AttributeFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;

/**
 用于Sentence_Analyzer的Tokenizer,实现分句的Tokenizer功能
 对于分句结果进行了有效性检测和过滤
 */
public abstract class NewCharTokenizer extends Tokenizer {

    /**
     * Creates a new {@link org.apache.lucene.analysis.util.CharTokenizer} instance
     */
    public NewCharTokenizer() {
    }

    /**
     * Creates a new {@link org.apache.lucene.analysis.util.CharTokenizer} instance
     *
     * @param factory
     *          the attribute factory to use for this {@link Tokenizer}
     */
    public NewCharTokenizer(AttributeFactory factory) {
        super(factory);
    }

    /**
     * Creates a new instance of CharTokenizer using a custom predicate, supplied as method reference or lambda expression.
     * The predicate should return {@code true} for all valid token characters.
     * <p>
     * This factory is intended to be used with lambdas or method references. E.g., an elegant way
     * to create an instance which behaves exactly as {@link LetterTokenizer} is:
     * <pre class="prettyprint lang-java">
     * Tokenizer tok = CharTokenizer.fromTokenCharPredicate(Character::isLetter);
     * </pre>
     */
    public static org.apache.lucene.analysis.util.CharTokenizer fromTokenCharPredicate(final IntPredicate tokenCharPredicate) {
        return fromTokenCharPredicate(DEFAULT_TOKEN_ATTRIBUTE_FACTORY, tokenCharPredicate);
    }

    /**
     * Creates a new instance of CharTokenizer with the supplied attribute factory using a custom predicate, supplied as method reference or lambda expression.
     * The predicate should return {@code true} for all valid token characters.
     * <p>
     * This factory is intended to be used with lambdas or method references. E.g., an elegant way
     * to create an instance which behaves exactly as {@link LetterTokenizer} is:
     * <pre class="prettyprint lang-java">
     * Tokenizer tok = CharTokenizer.fromTokenCharPredicate(factory, Character::isLetter);
     * </pre>
     */
    public static org.apache.lucene.analysis.util.CharTokenizer fromTokenCharPredicate(AttributeFactory factory, final IntPredicate tokenCharPredicate) {
        return fromTokenCharPredicate(factory, tokenCharPredicate, IntUnaryOperator.identity());
    }

    /**
     * Creates a new instance of CharTokenizer using a custom predicate, supplied as method reference or lambda expression.
     * The predicate should return {@code true} for all valid token characters.
     * This factory also takes a function to normalize chars, e.g., lowercasing them, supplied as method reference or lambda expression.
     * <p>
     * This factory is intended to be used with lambdas or method references. E.g., an elegant way
     * to create an instance which behaves exactly as {@link LowerCaseTokenizer} is:
     * <pre class="prettyprint lang-java">
     * Tokenizer tok = CharTokenizer.fromTokenCharPredicate(Character::isLetter, Character::toLowerCase);
     * </pre>
     */
    public static org.apache.lucene.analysis.util.CharTokenizer fromTokenCharPredicate(final IntPredicate tokenCharPredicate, final IntUnaryOperator normalizer) {
        return fromTokenCharPredicate(DEFAULT_TOKEN_ATTRIBUTE_FACTORY, tokenCharPredicate, normalizer);
    }

    /**
     * Creates a new instance of CharTokenizer with the supplied attribute factory using a custom predicate, supplied as method reference or lambda expression.
     * The predicate should return {@code true} for all valid token characters.
     * This factory also takes a function to normalize chars, e.g., lowercasing them, supplied as method reference or lambda expression.
     * <p>
     * This factory is intended to be used with lambdas or method references. E.g., an elegant way
     * to create an instance which behaves exactly as {@link LowerCaseTokenizer} is:
     * <pre class="prettyprint lang-java">
     * Tokenizer tok = CharTokenizer.fromTokenCharPredicate(factory, Character::isLetter, Character::toLowerCase);
     * </pre>
     */
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

    /**
     * Creates a new instance of CharTokenizer using a custom predicate, supplied as method reference or lambda expression.
     * The predicate should return {@code true} for all valid token separator characters.
     * This method is provided for convenience to easily use predicates that are negated
     * (they match the separator characters, not the token characters).
     * <p>
     * This factory is intended to be used with lambdas or method references. E.g., an elegant way
     * to create an instance which behaves exactly as {@link WhitespaceTokenizer} is:
     * <pre class="prettyprint lang-java">
     * Tokenizer tok = CharTokenizer.fromSeparatorCharPredicate(Character::isWhitespace);
     * </pre>
     */
    public static org.apache.lucene.analysis.util.CharTokenizer fromSeparatorCharPredicate(final IntPredicate separatorCharPredicate) {
        return fromSeparatorCharPredicate(DEFAULT_TOKEN_ATTRIBUTE_FACTORY, separatorCharPredicate);
    }

    /**
     * Creates a new instance of CharTokenizer with the supplied attribute factory using a custom predicate, supplied as method reference or lambda expression.
     * The predicate should return {@code true} for all valid token separator characters.
     * <p>
     * This factory is intended to be used with lambdas or method references. E.g., an elegant way
     * to create an instance which behaves exactly as {@link WhitespaceTokenizer} is:
     * <pre class="prettyprint lang-java">
     * Tokenizer tok = CharTokenizer.fromSeparatorCharPredicate(factory, Character::isWhitespace);
     * </pre>
     */
    public static org.apache.lucene.analysis.util.CharTokenizer fromSeparatorCharPredicate(AttributeFactory factory, final IntPredicate separatorCharPredicate) {
        return fromSeparatorCharPredicate(factory, separatorCharPredicate, IntUnaryOperator.identity());
    }

    /**
     * Creates a new instance of CharTokenizer using a custom predicate, supplied as method reference or lambda expression.
     * The predicate should return {@code true} for all valid token separator characters.
     * This factory also takes a function to normalize chars, e.g., lowercasing them, supplied as method reference or lambda expression.
     * <p>
     * This factory is intended to be used with lambdas or method references. E.g., an elegant way
     * to create an instance which behaves exactly as the combination {@link WhitespaceTokenizer} and {@link LowerCaseFilter} is:
     * <pre class="prettyprint lang-java">
     * Tokenizer tok = CharTokenizer.fromSeparatorCharPredicate(Character::isWhitespace, Character::toLowerCase);
     * </pre>
     */
    public static org.apache.lucene.analysis.util.CharTokenizer fromSeparatorCharPredicate(final IntPredicate separatorCharPredicate, final IntUnaryOperator normalizer) {
        return fromSeparatorCharPredicate(DEFAULT_TOKEN_ATTRIBUTE_FACTORY, separatorCharPredicate, normalizer);
    }

    /**
     * Creates a new instance of CharTokenizer with the supplied attribute factory using a custom predicate.
     * The predicate should return {@code true} for all valid token separator characters.
     * This factory also takes a function to normalize chars, e.g., lowercasing them, supplied as method reference or lambda expression.
     * <p>
     * This factory is intended to be used with lambdas or method references. E.g., an elegant way
     * to create an instance which behaves exactly as {@link WhitespaceTokenizer} and {@link LowerCaseFilter} is:
     * <pre class="prettyprint lang-java">
     * Tokenizer tok = CharTokenizer.fromSeparatorCharPredicate(factory, Character::isWhitespace, Character::toLowerCase);
     * </pre>
     */
    public static org.apache.lucene.analysis.util.CharTokenizer fromSeparatorCharPredicate(AttributeFactory factory, final IntPredicate separatorCharPredicate, final IntUnaryOperator normalizer) {
        return fromTokenCharPredicate(factory, separatorCharPredicate.negate(), normalizer);
    }

    private int offset = 0, bufferIndex = 0, dataLen = 0, finalOffset = 0;
    private static final int MAX_WORD_LEN = 255;
    private static final int IO_BUFFER_SIZE = 4096;

    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);

    private final CharacterBuffer ioBuffer = CharacterUtils.newCharacterBuffer(IO_BUFFER_SIZE);

    /**
     * Returns true iff a codepoint should be included in a token. This tokenizer
     * generates as tokens adjacent sequences of codepoints which satisfy this
     * predicate. Codepoints for which this is false are used to define token
     * boundaries and are not included in tokens.
     */
    protected abstract boolean isTokenChar(int c);

    /**
     * Called on each token character to normalize it before it is added to the
     * token. The default implementation does nothing. Subclasses may use this to,
     * e.g., lowercase tokens.
     */
    protected int normalize(int c) {
        return c;
    }

    @Override
    public final boolean incrementToken() throws IOException {
        clearAttributes();
        int length = 0;
        int start = -1; // this variable is always initialized
        int end = -1;
        char[] buffer = termAtt.buffer();

        while (true) {
            if (bufferIndex >= dataLen) {
                offset += dataLen;
                CharacterUtils.fill(ioBuffer, input);
                if (ioBuffer.getLength() == 0) {
                    dataLen = 0;
                    if (length > 0) {//检查句子是否有效
                        String tmp_sentence=new String(buffer);
                        tmp_sentence=tmp_sentence.substring(0,length);
                        boolean isValid=SentenceUtil.isValidSentence(tmp_sentence);
                        if(!isValid){
                            return false;
                        }
                        break;
                    } else {
                        finalOffset = correctOffset(offset);
                        return false;
                    }
                }
                dataLen = ioBuffer.getLength();
                bufferIndex = 0;
            }
            // use CharacterUtils here to support < 3.1 UTF-16 code unit behavior if the char based methods are gone
            final int c = Character.codePointAt(ioBuffer.getBuffer(), bufferIndex, ioBuffer.getLength());

            final int charCount = Character.charCount(c);
            bufferIndex += charCount;

           if(c==42||c==9633){//如果是换行符和空格，直接返回
               continue;
           }

            if (isTokenChar(c)) {               // if it's a token char
                if (length == 0) {                // start of token
                    assert start == -1;
                    start = offset + bufferIndex - charCount;
                    end = start;
                } else if (length >= buffer.length-1) { // check if a supplementary could run out of bounds
                    buffer = termAtt.resizeBuffer(2+length); // make sure a supplementary fits in the buffer
                }
                end += charCount;
                length += Character.toChars(normalize(c), buffer, length); // buffer it, normalized
                if (length >= MAX_WORD_LEN) { // buffer overflow! make sure to check for >= surrogate pair could break == test
                    break;
                }
            }
            else if (length > 0) {
                String tmp_sentence=new String(buffer);
                tmp_sentence=tmp_sentence.substring(0,length);
                boolean isValid=SentenceUtil.isValidSentence(tmp_sentence);
                if(isValid){
                    break;
                }
                else{
                    start=-1;
                    end=-1;
                    length=0;
                    continue;
                }//对状态进行重置

            }
        }

        termAtt.setLength(length);
        assert start != -1;
        offsetAtt.setOffset(correctOffset(start), finalOffset = correctOffset(end));
        return true;
    }

    @Override
    public final void end() throws IOException {
        super.end();
        // set final offset
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
}
