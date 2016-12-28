package com.example.kamal.gestionscores;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Kamal on 27-12-16.
 */

public class Utilisateur implements Serializable{
    private int id;
    private String pseudo;
    private String mdp;
    private ArrayList<Score> listeScore = new ArrayList<Score>();

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

    public String getMdp() {
        return mdp;
    }

    public void setListeScore(ArrayList<Score> listeScore) {
        this.listeScore = listeScore;
    }

    public ArrayList<Score> getListeScore() {
        return listeScore;
    }

    public Utilisateur(){

    }

    public Utilisateur(int _id, String _pseudo, String _mdp){
        id=_id;
        setPseudo(_pseudo);
        setMdp(_mdp);
    }

    public void addScore(Score s){
        getListeScore().add(s);
    }
}
