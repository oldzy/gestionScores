package com.example.kamal.gestionscores;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class WizardActivity extends AppCompatActivity {
    /*
    * ATTRIBUTS
     */
    private LinearLayout container;
    private ArrayList<String> listeJeux = new ArrayList<String>();

    /*
    * LISTENERS
     */
    private View.OnClickListener jeuListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent_retour = new Intent();
            intent_retour.putExtra("jeu", ((Button) v).getText());
            setResult(RESULT_OK, intent_retour);
            finish();
        }
    };

    /*
    * SETTERS ET GETTERS
     */
    public void setContainer(LinearLayout container) {
        this.container = container;
    }

    public LinearLayout getContainer() {
        return container;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wizard);

        setContainer((LinearLayout) findViewById(R.id.container_wizard));

        new AsynchroneListeWizard().execute();
    }

    /*
    * METHODE showMessage
    * Elle sert à afficher un popup
     */
    public void showMessage(String m) {
        Toast.makeText(this, m, Toast.LENGTH_SHORT).show();
    }

    /*
    * METHODE showListeJeux
    * Elle sert à afficher la liste des jeux disponibles sous forme de boutton
     */
    public void showListeJeux() {
        ArrayList<Button> list = new ArrayList<Button>();
        for (String s : listeJeux) {
            Button b = new Button(this);
            b.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            b.setWidth(getContainer().getWidth());
            b.setGravity(Gravity.CENTER);
            b.setText(s.substring(0, 1).toUpperCase() + s.substring(1));
            b.setTextSize(16);
            list.add(b);
        }
        for (Button b : list) {
            b.setOnClickListener(jeuListener);
            getContainer().addView(b);
        }
    }

    /*
    * CLASSES ASYNCHRONES
     */
    public class AsynchroneListeWizard extends AsyncTask<Object, Integer, ArrayList<Object>> {

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
                                String res = json_reader.nextString();
                                json_reader.nextName();
                                json_reader.nextString();
                                list.add(res);
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
                showListeJeux();
            } else
                showMessage((String) list.get(1));
        }
    }
}
