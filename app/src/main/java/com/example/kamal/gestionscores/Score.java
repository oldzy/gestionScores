package com.example.kamal.gestionscores;

import java.io.Serializable;

/**
 * Created by Kamal on 27-12-16.
 */

public class Score implements Serializable{
    private int id;
    private String jeu;
    private double score;

    public void setJeu(String jeu) {
        this.jeu = jeu;
    }

    public String getJeu() {
        return jeu;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getScore() {
        return score;
    }

    public Score(){

    }

    public Score(String _jeu, double _score){
        setJeu(_jeu);
        setScore(_score);
    }

    public Score(int _id, String _jeu, double _score){
        id=_id;
        setJeu(_jeu);
        setScore(_score);
    }
}
