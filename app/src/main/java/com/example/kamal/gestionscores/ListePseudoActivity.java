package com.example.kamal.gestionscores;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ListePseudoActivity extends AppCompatActivity {
    private LinearLayout liste_pseudo;
    private ArrayList<String> pseudo = new ArrayList<String>();

    public void setListe_pseudo(LinearLayout liste_pseudo) {
        this.liste_pseudo = liste_pseudo;
    }

    public LinearLayout getListe_pseudo() {
        return liste_pseudo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_pseudo);

        setListe_pseudo((LinearLayout) findViewById(R.id.liste_pseudo));

        new AsynchroneListe().execute();
    }

    public void showMessage(String m) {
        Toast.makeText(this, m, Toast.LENGTH_SHORT).show();
    }

    public void showListePseudo() {
        for (int i = 0; i < pseudo.size(); i++) {
            TextView t = new TextView(this);
            t.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            t.setHeight(((ScrollView) getListe_pseudo().getParent()).getHeight() / 5);
            t.setGravity(Gravity.CENTER);
            t.setText(pseudo.get(i));
            t.setTextSize(16);
            getListe_pseudo().addView(t);
            if (i % 2 == 0)
                t.setBackgroundResource(R.color.colorPrimaryDark);
            else
                t.setBackgroundResource(R.color.colorPrimaryLight);
        }
    }

    //CLASSE ASYNCHRONE
    public class AsynchroneListe extends AsyncTask<Object, Integer, ArrayList<Object>> {

        @Override
        protected ArrayList<Object> doInBackground(Object[] params) {
            ArrayList<Object> list = new ArrayList<Object>();
            try {
                URL url = new URL("http://projetandroid.esy.es/RPCAndroid/lister_pseudos.php");
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
                    pseudo.add(list.get(i).toString());
                }
                showListePseudo();
            } else
                showMessage((String) list.get(1));
        }
    }
}
