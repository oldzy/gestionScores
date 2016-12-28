package com.example.kamal.gestionscores;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class AddScoreActivity extends AppCompatActivity {
    private Utilisateur user;
    private EditText nom_jeu;
    private EditText score_obtenu;
    private Button ajouter;

    private View.OnClickListener ajouterListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Object[] list = new Object[3];
            list[0] = getScore_obtenu().getText();
            list[1] = getNom_jeu().getText();
            list[2] = user.getId();
            new AsynchroneAjout().execute(list);
        }
    };

    public void setNom_jeu(EditText nom_jeu) {
        this.nom_jeu = nom_jeu;
    }

    public EditText getNom_jeu() {
        return nom_jeu;
    }

    public void setScore_obtenu(EditText score_obtenu) {
        this.score_obtenu = score_obtenu;
    }

    public EditText getScore_obtenu() {
        return score_obtenu;
    }

    public void setAjouter(Button ajouter) {
        this.ajouter = ajouter;
    }

    public Button getAjouter() {
        return ajouter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_score);

        setNom_jeu((EditText)findViewById(R.id.nom_jeu));
        setScore_obtenu((EditText)findViewById(R.id.score_obtenu));
        setAjouter((Button) findViewById(R.id.ajouter));

        user = (Utilisateur)getIntent().getSerializableExtra("utilisateur");

        getAjouter().setOnClickListener(ajouterListener);
    }

    public void showMessage(String m){
        Toast.makeText(this, m, Toast.LENGTH_SHORT).show();
    }

    public void showMenu(){
        finish();
    }

    //CLASSE ASYNCHRONE
    public class AsynchroneAjout extends AsyncTask<Object, Integer, Object[]> {

        @Override
        protected Object[] doInBackground(Object[] params) {
            Object[] list = new Object[2];
            try{
                URL url = new URL("http://skurt.16mb.com/projetAndroid/ajouter_score.php?score="+params[0]+"&jeu="+params[1]+"&id_pseudo="+params[2]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStreamReader RPC = new InputStreamReader(connection.getInputStream(), "UTF-8");
                Scanner scan = new Scanner(RPC);
                scan.useDelimiter(" ");
                int code = connection.getResponseCode();
                if(code == 200){
                    list[1] = Integer.parseInt(scan.next());
                    switch ((int)list[1]){
                        case 0:
                            list[0] = getString(R.string.score_ok);
                            user.addScore(new Score((String)params[1], (double)params[0]));
                            break;
                        case 100:
                            list[0] = getString(R.string.prob_score);
                            break;
                        case 110:
                            list[0] = getString(R.string.prob_nom_jeu);
                            break;
                        case 120:
                            list[0] = getString(R.string.prob_id);
                            break;
                        case 1000:
                            list[0] = getString(R.string.prob_DB);
                            break;
                        default:
                            list[0] = getString(R.string.prob_autre);
                            break;
                    }
                } else
                    list[0] = getString(R.string.prob_autre);
            }catch (MalformedURLException e){
                list[0] = e.getMessage();
            }
            catch(IOException ex) {
                list[0] = ex.getMessage();
            }
            return list;
        }
        @Override
        protected void onPostExecute(Object[] list)
        {
            if((int)list[1] == 0)
                showMenu();
            showMessage((String)list[0]);
        }
    }
}
