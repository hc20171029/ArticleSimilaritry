package io.transwarp.ds.vo;

public class ScoreResult{
    private String id;
    private double score;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public ScoreResult(String id,double score){
        this.id=id;
        this.score=score;
    }
}
