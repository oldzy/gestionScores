package com.example.kamal.gestionscores;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class AddScoreActivity extends AppCompatActivity {
    /*
    * ATTRIBUTS
     */
    public final static int NUM_REQUETE = 1;
    private Utilisateur user;
    private AutoCompleteTextView nom_jeu;
    private EditText score_obtenu;
    private Button ajouter;
    private Button wizard;
    private ArrayList<String> listeJeux = new ArrayList<String>();

    /*
    * LISTENERS
     */
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

    private View.OnClickListener wizardListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivityForResult(new Intent(AddScoreActivity.this, WizardActivity.class), NUM_REQUETE);
        }
    };

    /*
    * SETTERS ET GETTERS
     */
    public void setNom_jeu(AutoCompleteTextView nom_jeu) {
        this.nom_jeu = nom_jeu;
    }

    public AutoCompleteTextView getNom_jeu() {
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

    public Button getWizard() {
        return wizard;
    }

    public void setWizard(Button wizard) {
        this.wizard = wizard;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_score);

        setNom_jeu((AutoCompleteTextView) findViewById(R.id.nom_jeu));
        setScore_obtenu((EditText) findViewById(R.id.score_obtenu));
        setAjouter((Button) findViewById(R.id.ajouter));
        setWizard((Button) findViewById(R.id.wizard));

        user = (Utilisateur) getIntent().getSerializableExtra("utilisateur");

        getAjouter().setOnClickListener(ajouterListener);
        getWizard().setOnClickListener(wizardListener);
        new AsynchroneListe().execute();
    }
    /*
    * METHODE onActivityResult
    * Elle sert a récuperer le nom du jeu selectionner dans le wizard et le mettre dans l'AutoCompleteTextView nom_jeu
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NUM_REQUETE)
            if (resultCode == RESULT_OK)
                getNom_jeu().setText(data.getStringExtra("jeu").toLowerCase());
    }

    /*
    * METHODE showMessage
    * Elle sert à afficher un popup
     */
    public void showMessage(String m) {
        Toast.makeText(this, m, Toast.LENGTH_SHORT).show();
    }

    /*
    * METHODE showMenu
    * Elle sert à mettre fin a l'activity
     */
    public void showMenu() {
        finish();
    }

    /*
    * METHODE autocompletion
    * Elle sert à transmettre la liste de proposition pour l'autocompletion du champ nom_jeu
     */
    public void autocompletion() {
        getNom_jeu().setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listeJeux.toArray(new String[0])));
    }

    /*
    * CLASSES ASYNCHRONES
     */
    public class AsynchroneAjout extends AsyncTask<Object, Integer, Object[]> {

        @Override
        protected Object[] doInBackground(Object[] params) {
            Object[] list = new Object[2];
            try {
                int score = Integer.parseInt(params[0].toString());
                URL url = new URL("http://projetandroid.esy.es/RPCAndroid/ajouter_score.php?score=" + score + "&jeu=" + params[1].toString().replaceAll(" ", "%20") + "&id_pseudo=" + params[2]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStreamReader RPC = new InputStreamReader(connection.getInputStream(), "UTF-8");
                Scanner scan = new Scanner(RPC);
                scan.useDelimiter(" ");
                int code = connection.getResponseCode();
                if (code == 200) {
                    list[1] = Integer.parseInt(scan.next());
                    switch ((int) list[1]) {
                        case 0:
                            list[0] = getString(R.string.score_ok);
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
            } catch (MalformedURLException e) {
                list[0] = e.getMessage();
            } catch (IOException ex) {
                list[0] = ex.getMessage();
            } catch (NumberFormatException exc) {
                list[1] = 1;
                list[0] = exc.getMessage();
            }
            return list;
        }

        @Override
        protected void onPostExecute(Object[] list) {
            showMessage((String) list[0]);
            if ((int) list[1] == 0)
                showMenu();
        }
    }

    public class AsynchroneListe extends AsyncTask<Object, Integer, ArrayList<Object>> {

        @Override
        protected ArrayList<Object> doInBackground(Object[] params) {
            ArrayList<Object> list = new ArrayList<Object>();
            try {
                URL url = new URL("http://projetandroid.esy.es/RPCAndroid/lister_jeux.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                JsonReader json_reader = new JsonReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                json_reader.beginObject();
                int code = connection.getResponseCode();
                if (code == 200) {
                    json_reader.nextName();
                    list.add(json_reader.nextInt());
                    switch ((int) list.get(0)) {
                        case 0:
                            list.add(getString(R.string.pas_prob));
                            json_reader.nextName();
                            json_reader.beginArray();
                            while (json_reader.hasNext()) {
                                json_reader.beginObject();
                                json_reader.nextName();
                                list.add(json_reader.nextString());
                                json_reader.nextName();
                                json_reader.nextString();
                                json_reader.endObject();
                            }
                            json_reader.endArray();
                            json_reader.endObject();
                            break;
                        case 300:
                            list.add(getString(R.string.prob_nom_jeu));
                            break;
                        case 1000:
                            list.add(getString(R.string.prob_DB));
                            break;
                        default:
                            list.add(getString(R.string.prob_autre));
                            break;
                    }
                } else
                    list.add(getString(R.string.prob_autre));
            } catch (MalformedURLException e) {
                list.add(e.getMessage());
            } catch (IOException ex) {
                list.add(ex.getMessage());
            }
            return list;
        }

        @Override
        protected void onPostExecute(ArrayList<Object> list) {
            if ((int) list.get(0) == 0) {
                for (int i = 2; i < list.size(); i++) {
                    listeJeux.add(list.get(i).toString());
                }
                autocompletion();
            } else
                showMessage((String) list.get(1));
        }
    }
}
