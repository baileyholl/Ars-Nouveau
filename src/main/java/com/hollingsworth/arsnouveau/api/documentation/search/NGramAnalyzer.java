package com.hollingsworth.arsnouveau.api.documentation.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.ngram.NGramTokenFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class NGramAnalyzer extends Analyzer {
    private final int minGram;
    private final int maxGram;

    public NGramAnalyzer(int minGram, int maxGram) {
        this.minGram = minGram;
        this.maxGram = maxGram;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer tokenizer = new StandardTokenizer();
        TokenStream tokenStream = new EnglishPossessiveFilter(tokenizer);
        tokenStream = new LowerCaseFilter(tokenStream);
//        tokenStream = new StopFilter(tokenStream, EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);
        tokenStream = new NGramTokenFilter(tokenStream, minGram, maxGram, false);
        return new TokenStreamComponents(tokenizer, tokenStream);
    }
}