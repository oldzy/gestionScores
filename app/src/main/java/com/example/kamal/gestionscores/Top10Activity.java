package com.example.kamal.gestionscores;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Top10Activity extends AppCompatActivity {
    /*
    * ATTRIBUTS
     */
    public final static int NUM_REQUETE = 1;
    private LinearLayout container_top_10;
    private AutoCompleteTextView nom_jeu_top;
    private Button afficher_top_10;
    private Button wizard_top;
    private ArrayList<String> listeJeux = new ArrayList<String>();

    /*
    * LISTENERS
     */
    private View.OnClickListener afficher_top_10Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Object[] list = new Object[1];
            list[0] = getNom_jeu_top().getText();
            new AsynchroneTop().execute(list);
        }
    };

    private View.OnClickListener wizardListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivityForResult(new Intent(Top10Activity.this, WizardActivity.class), NUM_REQUETE);
        }
    };

    /*
    * SETTERS ET GETTERS
     */
    public void setContainer_top_10(LinearLayout container_top_10) {
        this.container_top_10 = container_top_10;
    }

    public LinearLayout getContainer_top_10() {
        return container_top_10;
    }

    public void setNom_jeu_top(AutoCompleteTextView nom_jeu_top) {
        this.nom_jeu_top = nom_jeu_top;
    }

    public AutoCompleteTextView getNom_jeu_top() {
        return nom_jeu_top;
    }

    public void setAfficher_top_10(Button afficher_top_10) {
        this.afficher_top_10 = afficher_top_10;
    }

    public Button getAfficher_top_10() {
        return afficher_top_10;
    }

    public Button getWizard_top() {
        return wizard_top;
    }

    public void setWizard_top(Button wizard_top) {
        this.wizard_top = wizard_top;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top10);

        setContainer_top_10((LinearLayout) findViewById(R.id.container_top_10));
        setNom_jeu_top((AutoCompleteTextView) findViewById(R.id.nom_jeu_top));
        setAfficher_top_10((Button) findViewById(R.id.afficher_top_10));
        setWizard_top((Button) findViewById(R.id.wizard_top));

        getAfficher_top_10().setOnClickListener(afficher_top_10Listener);
        getWizard_top().setOnClickListener(wizardListener);
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
                getNom_jeu_top().setText(data.getStringExtra("jeu").toLowerCase());
    }

    /*
    * METHODE showMessage
    * Elle sert à afficher un popup
     */
    public void showMessage(String m) {
        getContainer_top_10().removeAllViews();
        Toast.makeText(this, m, Toast.LENGTH_SHORT).show();
    }

    /*
    * METHODE showTop10
    * Elle sert à afficher une liste de jeu
     */
    public void showTop10(ArrayList<String> list) {
        getContainer_top_10().removeAllViews();
        TextView t = new TextView(this);
        t.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        t.setGravity(Gravity.CENTER);
        t.setText(getString(R.string.tableau_score));
        t.setTextSize(16);
        t.setHeight(50);
        t.setBackgroundResource(R.color.colorPrimary);
        getContainer_top_10().addView(t);
        for (int i = 0; i < list.size(); i++) {
            t = new TextView(this);
            t.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            t.setGravity(Gravity.CENTER);
            t.setText(list.get(i));
            t.setTextSize(16);
            t.setHeight(50);
            if (i % 2 == 0)
                t.setBackgroundResource(R.color.colorPrimaryDark);
            else
                t.setBackgroundResource(R.color.colorPrimaryLight);
            getContainer_top_10().addView(t);
        }
    }

    /*
    * METHODE autocompletion
    * Elle sert à transmettre la liste de proposition pour l'autocompletion du champ nom_jeu
     */
    public void autocompletion() {
        getNom_jeu_top().setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listeJeux.toArray(new String[0])));
    }

    /*
    * CLASSES ASYNCHRONES
     */
    public class AsynchroneTop extends AsyncTask<Object, Integer, Object[]> {

        @Override
        protected Object[] doInBackground(Object[] params) {
            Object[] list = new Object[13];
            try {
                URL url = new URL("http://projetandroid.esy.es/RPCAndroid/afficher_top.php?jeu=" + params[0].toString().replaceAll(" ", "%20"));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                JsonReader json_reader = new JsonReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                json_reader.beginObject();
                int code = connection.getResponseCode();
                if (code == 200) {
                    json_reader.nextName();
                    list[1] = json_reader.nextInt();
                    switch ((int) list[1]) {
                        case 0:
                            json_reader.nextName();
                            json_reader.beginArray();
                            for (int i = 2; json_reader.hasNext(); i++) {
                                json_reader.beginObject();
                                json_reader.nextName();
                                String res = json_reader.nextString();
                                json_reader.nextName();
                                res += " : " + json_reader.nextInt();
                                list[i] = res;
                                json_reader.endObject();
                            }
                            json_reader.endArray();
                            json_reader.endObject();
                            list[0] = getString(R.string.pas_prob);
                            break;
                        case 100:
                            list[0] = getString(R.string.prob_nom_jeu);
                            break;
                        case 300:
                            list[0] = getString(R.string.prob_score_top);
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
            }
            return list;
        }

        @Override
        protected void onPostExecute(Object[] list) {
            if ((int) list[1] == 0) {
                ArrayList<String> listeTop = new ArrayList<String>();
                for (int i = 2; list[i] != null; i++) {
                    listeTop.add(list[i].toString());
                }
                showTop10(listeTop);
            } else
                showMessage((String) list[0]);
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
