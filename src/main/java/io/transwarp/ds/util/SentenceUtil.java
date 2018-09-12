package io.transwarp.ds.util;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.seg.common.Term;
import io.transwarp.ds.service.ValidChecker;

import java.util.List;

public class SentenceUtil implements ValidChecker{
    private static double v_percent=0.4;
    private static int v_count=2;

    public static void setV_percent(double vv_percent){
        v_percent=vv_percent;
    }
    public static void setV_count(double vv_count){
        v_percent=vv_count;
    }

    public static boolean isValidSentence(String sentence){
        sentence=sentence.trim();
        List<Term> list=HanLP.segment(sentence);

        int all_char_length=0;
        int valid_char_length=0;
        int valid_count=0;

        for(Term t:list) {
            all_char_length+=t.word.length();
            if (CoreStopWordDictionary.shouldInclude(t)) {
                valid_count++;
                //System.out.println(t.word);
                valid_char_length+=t.word.length();
            }
        }


        double valid_percent=(valid_char_length*1.0)/(all_char_length*1.0);

        if(valid_count>=v_count&&valid_percent>=v_percent){
            return true;
        }
        return false;
    }

    public static void main(String args[]){
        isValidSentence("（4分）由省卫生厅科教处负责组织44实施4.0项目的实施时间是否明确（4分）");
    }

    @Override
    public boolean checkValid(String token) {
        return isValidSentence(token);
    }
}
