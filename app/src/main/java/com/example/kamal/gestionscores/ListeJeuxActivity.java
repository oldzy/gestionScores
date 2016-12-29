package com.example.kamal.gestionscores;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.JsonReader;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ListeJeuxActivity extends AppCompatActivity {
    static final int MIN_DISTANCE = 150;
    private LinearLayout liste_jeux;
    private double x1, x2;
    private int nav = 1, nbNav;
    private ArrayList<String> listeJeux;

    private View.OnClickListener jeuxListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    public void setListe_jeux(LinearLayout liste_jeux) {
        this.liste_jeux = liste_jeux;
    }

    public LinearLayout getListe_jeux() {
        return liste_jeux;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_jeux);

        setListe_jeux((LinearLayout)findViewById(R.id.liste_jeux));

        new AsynchroneListe().execute();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                x1=event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2=event.getX();
                double deltaX = x2-x1;
                if(Math.abs(deltaX) > MIN_DISTANCE){
                    if(x2 > x1){
                        if(nav > 1){
                            nav--;
                            showListeJeux();
                        }
                    }else {
                        if(nav < nbNav){
                            nav++;
                            showListeJeux();
                        }
                    }
                }else{

                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    public void showMessage(String m){
        Toast.makeText(this, m, Toast.LENGTH_SHORT).show();
    }
    public void showListeJeux(){
        int height = findViewById(R.id.screen).getHeight() / 5;
        int width = findViewById(R.id.screen).getWidth() / 4;
        getListe_jeux().removeAllViews();
        for (int i = (nav-1)*5; i < listeJeux.size() && i < nav*5; i++){
            LinearLayout l = new LinearLayout(this);
            l.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            l.setOrientation(LinearLayout.HORIZONTAL);
            TextView t = new TextView(this);
            t.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            t.setHeight(height);
            t.setWidth(width*3);
            t.setGravity(Gravity.CENTER);
            t.setText(listeJeux.get(i).substring(0,1).toUpperCase() + listeJeux.get(i).substring(1));
            t.setTextSize(16);
            if(i%2 == 0)
                l.setBackgroundResource(R.color.colorPrimaryDark);
            else
                l.setBackgroundResource(R.color.colorPrimaryLight);
            Button b = new Button(this);
            b.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            b.setText(getString(R.string.top));
            l.addView(t);
            l.addView(b);
            getListe_jeux().addView(l);
        }
    }
    //CLASSE ASYNCHRONE
    public class AsynchroneListe extends AsyncTask<Object, Integer, ArrayList<Object>>
    {

        @Override
        protected ArrayList<Object> doInBackground(Object[] params) {
            ArrayList<Object> list = new ArrayList<Object>();
            try{
                URL url = new URL("http://skurt.16mb.com/projetAndroid/lister_jeux.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                JsonReader json_reader = new JsonReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                json_reader.beginObject();
                int code = connection.getResponseCode();
                if(code == 200){
                    json_reader.nextName();
                    list.add(json_reader.nextInt());
                    switch ((int)list.get(0)){
                        case 0:
                            list.add(getString(R.string.pas_prob));
                            json_reader.nextName();
                            json_reader.beginArray();
                            while(json_reader.hasNext()){
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
            }catch (MalformedURLException e){
                list.add(e.getMessage());
            }
            catch(IOException ex) {
                list.add(ex.getMessage());
            }
            return list;
        }
        @Override
        protected void onPostExecute(ArrayList<Object> list)
        {
            if((int)list.get(0) == 0){
                ArrayList<String> listeJeuxTemp = new ArrayList<String>();
                for (int i=2; i<list.size(); i++){
                    listeJeuxTemp.add(list.get(i).toString());
                }
                listeJeux = listeJeuxTemp;
                nbNav = listeJeux.size()/5;
                if(listeJeux.size() != nbNav*5)
                    nbNav++;
                showListeJeux();
            }
            else
                showMessage((String)list.get(1));
        }
    }
}
