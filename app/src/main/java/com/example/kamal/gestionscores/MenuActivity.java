package com.example.kamal.gestionscores;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {
    private Button ajouter_score;
    private Button afficher_top;
    private Button afficher_liste_jeu;
    private Button afficher_liste_utilisateur;

    private View.OnClickListener ajouter_scoreListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };
    private View.OnClickListener afficher_topListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };
    private View.OnClickListener afficher_liste_jeuListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };
    private View.OnClickListener afficher_liste_utilisateurListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    public void setAjouter_score(Button ajouter_score) {
        this.ajouter_score = ajouter_score;
    }

    public Button getAjouter_score() {
        return ajouter_score;
    }

    public void setAfficher_top(Button afficher_top) {
        this.afficher_top = afficher_top;
    }

    public Button getAfficher_top() {
        return afficher_top;
    }

    public void setAfficher_liste_jeu(Button afficher_liste_jeu) {
        this.afficher_liste_jeu = afficher_liste_jeu;
    }

    public Button getAfficher_liste_jeu() {
        return afficher_liste_jeu;
    }

    public void setAfficher_liste_utilisateur(Button afficher_liste_utilisateur) {
        this.afficher_liste_utilisateur = afficher_liste_utilisateur;
    }

    public Button getAfficher_liste_utilisateur() {
        return afficher_liste_utilisateur;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        setAjouter_score((Button)findViewById(R.id.ajouter_score));
        setAfficher_top((Button)findViewById(R.id.afficher_top));
        setAfficher_liste_jeu((Button)findViewById(R.id.afficher_liste_jeu));
        setAfficher_liste_utilisateur((Button)findViewById(R.id.afficher_liste_utlisateur));

        getAjouter_score().setOnClickListener(ajouter_scoreListener);
        getAfficher_top().setOnClickListener(afficher_topListener);
        getAfficher_liste_jeu().setOnClickListener(afficher_liste_jeuListener);
        getAfficher_liste_utilisateur().setOnClickListener(afficher_liste_utilisateurListener);
    }
}
