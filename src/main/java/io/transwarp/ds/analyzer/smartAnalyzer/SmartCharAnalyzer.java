package io.transwarp.ds.analyzer.smartAnalyzer;

import org.apache.lucene.analysis.Analyzer;

public class SmartCharAnalyzer extends Analyzer {
    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        return new TokenStreamComponents(new SmartCharTokenizer());
    }
}
