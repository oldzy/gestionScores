package com.example.kamal.gestionscores;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Kamal on 27-12-16.
 */

public class Utilisateur implements Serializable {
    /*
    * ATTRIBUTS
     */
    private int id;
    private String pseudo;
    private String mdp;

    /*
    * SETTERS ET GETTERS
     */
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

    /*
    * CONSTRUCTEURS
     */
    public Utilisateur(int _id, String _pseudo, String _mdp) {
        id = _id;
        setPseudo(_pseudo);
        setMdp(_mdp);
    }
}
