package com.example.kamal.gestionscores;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.Scanner;

public class LoginActivity extends AppCompatActivity {
    private boolean flag = false;
    private LinearLayout container;
    private EditText pseudo_log;
    private EditText mdp_log;
    private Button connexion;
    private Button form_toggle;
    private EditText pseudo_reg;
    private EditText mdp_reg;
    private EditText mdp_reg_conf;
    private Button inscription;

    private View.OnClickListener connexionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Object[] list = new Object[2];
            list[0] = getPseudo_log().getText();
            list[1] = getMdp_log().getText();
            new AsynchroneConnexion().execute(list);
        }
    };

    private View.OnClickListener form_toggleListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(flag)
            {
                getForm_toggle().setText(R.string.aff_form);
                getPseudo_reg().setVisibility(View.INVISIBLE);
                getMdp_reg().setVisibility(View.INVISIBLE);
                getMdp_reg_conf().setVisibility(View.INVISIBLE);
                getInscription().setVisibility(View.INVISIBLE);
                flag = false;
            }
            else
            {
                getForm_toggle().setText(R.string.masq_form);
                getPseudo_reg().setVisibility(View.VISIBLE);
                getMdp_reg().setVisibility(View.VISIBLE);
                getMdp_reg_conf().setVisibility(View.VISIBLE);
                getInscription().setVisibility(View.VISIBLE);
                flag = true;
            }
        }
    };

    private View.OnClickListener inscriptionListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if((getMdp_reg().getText().toString()).equals(getMdp_reg().getText().toString())){
                Object[] list = new Object[2];
                list[0] = getPseudo_reg().getText();
                list[1] = getMdp_reg().getText();
                new AsynchroneInscription().execute(list);
            }
            else
            {
                showMessage(getString(R.string.mdp_correspond_pas));
            }
        }
    };

    public void setContainer(LinearLayout container) {
        this.container = container;
    }

    public LinearLayout getContainer() {
        return container;
    }

    public void setPseudo_log(EditText pseudo_log) {
        this.pseudo_log = pseudo_log;
    }

    public EditText getPseudo_log() {
        return pseudo_log;
    }

    public void setMdp_log(EditText mdp_log) {
        this.mdp_log = mdp_log;
    }

    public EditText getMdp_log() {
        return mdp_log;
    }

    public void setConnexion(Button connexion) {
        this.connexion = connexion;
    }

    public Button getConnexion() {
        return connexion;
    }

    public void setForm_toggle(Button form_toggle) {
        this.form_toggle = form_toggle;
    }

    public Button getForm_toggle() {
        return form_toggle;
    }

    public void setPseudo_reg(EditText pseudo_reg) {
        this.pseudo_reg = pseudo_reg;
    }

    public EditText getPseudo_reg() {
        return pseudo_reg;
    }

    public void setMdp_reg(EditText mdp_reg) {
        this.mdp_reg = mdp_reg;
    }

    public EditText getMdp_reg() {
        return mdp_reg;
    }

    public void setMdp_reg_conf(EditText mdp_reg_conf) {
        this.mdp_reg_conf = mdp_reg_conf;
    }

    public EditText getMdp_reg_conf() {
        return mdp_reg_conf;
    }

    public void setInscription(Button insciprtion) {
        this.inscription = insciprtion;
    }

    public Button getInscription() {
        return inscription;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setContainer((LinearLayout)findViewById(R.id.container));
        setPseudo_log((EditText)findViewById(R.id.pseudo_log));
        setMdp_log((EditText)findViewById(R.id.mdp_log));
        setConnexion((Button)findViewById(R.id.connexion));
        setForm_toggle((Button)findViewById(R.id.form_toggle));
        setPseudo_reg((EditText)findViewById(R.id.pseudo_reg));
        setMdp_reg((EditText)findViewById(R.id.mdp_reg));
        setMdp_reg_conf((EditText)findViewById(R.id.mdp_reg_conf));
        setInscription((Button)findViewById(R.id.inscription));

        getConnexion().setOnClickListener(connexionListener);
        getForm_toggle().setOnClickListener(form_toggleListener);
        getInscription().setOnClickListener(inscriptionListener);
    }

    public void showMessage(String m){
        Toast.makeText(this, m, Toast.LENGTH_SHORT).show();
    }

    public void showMenu(Utilisateur u){
        startActivity(new Intent(LoginActivity.this, MenuActivity.class).putExtra("utilisateur", u));
    }
    //CLASSE ASYNCHRONE
    public class AsynchroneConnexion extends AsyncTask<Object, Integer, Object[]>
    {

        @Override
        protected Object[] doInBackground(Object[] params) {
            Object[] list = new Object[2];
            try{
                URL url = new URL("http://skurt.16mb.com/projetAndroid/se_connecter.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                OutputStream OS = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
                String parametres = "pseudo="+params[0]+"&mdp="+params[1];
                writer.write(parametres);
                writer.flush();
                writer.close();
                OS.close();
                InputStreamReader RPC = new InputStreamReader(connection.getInputStream(), "UTF-8");
                Scanner scan = new Scanner(RPC);
                scan.useDelimiter(";");
                int code = connection.getResponseCode();
                if(code == 200){
                    switch (Integer.parseInt(scan.next())){
                        case 0:
                            list[0] = getString(R.string.connexion_ok);
                            list[1] = new Utilisateur(Integer.parseInt(scan.next()), params[0].toString(), params[1].toString());
                            break;
                        case 100:
                            list[0] = getString(R.string.prob_pseudo);
                            list[1] = null;
                            break;
                        case 110:
                            list[0] = getString(R.string.prob_mdp);
                            list[1] = null;
                            break;
                        case 200:
                            list[0] = getString(R.string.prob_combinaison);
                            list[1] = null;
                            break;
                        case 1000:
                            list[0] = getString(R.string.prob_DB);
                            list[1] = null;
                            break;
                        default:
                            list[0] = getString(R.string.prob_autre);
                            list[1] = null;
                            break;
                    }
                } else {
                    list[0] = getString(R.string.prob_autre);
                    list[1] = null;
                }
            }catch (MalformedURLException e){
                list[0] = e.getMessage();
                list[1] = null;
            }
            catch(IOException ex) {
                list[0] = ex.getMessage();
                list[1] = null;
            }
            return list;
        }
        @Override
        protected void onPostExecute(Object[] list)
        {
            if(list[1] == null)
            {
                showMessage((String)list[0]);
            }else{
                showMenu((Utilisateur)list[1]);
            }
        }
    }
    public class AsynchroneInscription extends AsyncTask<Object, Integer, String>
    {

        @Override
        protected String doInBackground(Object[] params) {
            String res = "";
            try{
                URL url = new URL("http://skurt.16mb.com/projetAndroid/creer_compte.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                OutputStream OS = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));
                String parametres = "pseudo="+params[0]+"&mdp="+params[1];
                writer.write(parametres);
                writer.flush();
                writer.close();
                OS.close();
                JsonReader json_reader = new JsonReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                json_reader.beginObject();
                int code = connection.getResponseCode();
                if(code == 200){
                    json_reader.nextName();
                    switch (json_reader.nextInt()){
                        case 0:
                            json_reader.nextName();
                            res = getString(R.string.inscription_ok) + json_reader.nextInt();
                            break;
                        case 100:
                            res = getString(R.string.prob_pseudo);
                            break;
                        case 110:
                            res = getString(R.string.prob_mdp);
                            break;
                        case 200:
                            res = getString(R.string.prob_pseudo_exist);
                            break;
                        case 1000:
                            res = getString(R.string.prob_DB);
                            break;
                        default:
                            res = getString(R.string.prob_autre);
                            break;
                    }
                } else
                    res = getString(R.string.prob_autre);
            }catch (MalformedURLException e){
                res = e.getMessage();
            }
            catch(IOException ex) {
                res = ex.getMessage();
            }
            return res;
        }
        @Override
        protected void onPostExecute(String res)
        {
            if(res.contains("Sauvegarde")){
                getPseudo_reg().setText("");
                getMdp_reg().setText("");
                getMdp_reg_conf().setText("");
                getForm_toggle().callOnClick();
            }
            showMessage(res);
        }
    }
}
