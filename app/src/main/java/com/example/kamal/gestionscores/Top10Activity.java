package com.example.kamal.gestionscores;

import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Top10Activity extends AppCompatActivity {
    private LinearLayout container_top_10;
    private EditText nom_jeu_top;
    private Button afficher_top_10;

    private View.OnClickListener afficher_top_10Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Object[] list = new Object[1];
            list[0] = getNom_jeu_top().getText();
            new AsynchroneTop().execute(list);
        }
    };

    public void setContainer_top_10(LinearLayout container_top_10) {
        this.container_top_10 = container_top_10;
    }

    public LinearLayout getContainer_top_10() {
        return container_top_10;
    }

    public void setNom_jeu_top(EditText nom_jeu_top) {
        this.nom_jeu_top = nom_jeu_top;
    }

    public EditText getNom_jeu_top() {
        return nom_jeu_top;
    }

    public void setAfficher_top_10(Button afficher_top_10) {
        this.afficher_top_10 = afficher_top_10;
    }

    public Button getAfficher_top_10() {
        return afficher_top_10;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top10);

        setContainer_top_10((LinearLayout) findViewById(R.id.container_top_10));
        setNom_jeu_top((EditText) findViewById(R.id.nom_jeu_top));
        setAfficher_top_10((Button) findViewById(R.id.afficher_top_10));

        getAfficher_top_10().setOnClickListener(afficher_top_10Listener);
    }

    public void showMessage(String m) {
        getContainer_top_10().removeAllViews();
        Toast.makeText(this, m, Toast.LENGTH_SHORT).show();
    }

    public void showTop10(Object[] list) {
        getContainer_top_10().removeAllViews();
        TextView t = new TextView(this);
        t.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        t.setGravity(Gravity.CENTER);
        t.setText(getString(R.string.tableau_score));
        t.setTextSize(16);
        t.setHeight(50);
        t.setBackgroundResource(R.color.colorPrimary);
        getContainer_top_10().addView(t);
        for (int i = 2; list[i] != null && i < 12; i++) {
            t = new TextView(this);
            t.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            t.setGravity(Gravity.CENTER);
            t.setText(list[i].toString());
            t.setTextSize(16);
            t.setHeight(50);
            if (i % 2 == 0)
                t.setBackgroundResource(R.color.colorPrimaryDark);
            else
                t.setBackgroundResource(R.color.colorPrimaryLight);
            getContainer_top_10().addView(t);
        }
    }

    //CLASSE ASYNCHRONE
    public class AsynchroneTop extends AsyncTask<Object, Integer, Object[]> {

        @Override
        protected Object[] doInBackground(Object[] params) {
            Object[] list = new Object[12];
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
                            for (int i = 2; json_reader.hasNext() && i < 12; i++) {
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
            if ((int) list[1] == 0)
                showTop10(list);
            else
                showMessage((String) list[0]);
        }
    }
}
