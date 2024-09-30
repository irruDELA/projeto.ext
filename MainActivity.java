package br.com.softwareminas.quizzzz;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import br.com.softwareminas.quizzzz.thread.TConsultaAPI;
import br.com.softwareminas.quizzzz.util.ConexaoHttpClient;

public class MainActivity extends AppCompatActivity {

    Context context;
    int respostacorreta;
    int acertos;
    int numeropergunta;
    JSONObject jsonresposta;
    JSONArray respostas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preparatelainicial();
        context = this;


    }

    private void preparatelainicial() {
        setContentView(R.layout.activity_main); // Define o layout da tela
        Button btnIniciar = findViewById(R.id.btnStartQuiz);

        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iniciarquiz();
            }
        });
    }
    private void preparatelaresultado(){
        setContentView(R.layout.activity_result);
        TextView lbacertos = findViewById(R.id.fmResult_lbacertos);
        lbacertos.setText(String.valueOf(acertos)+"/10");
        TextView msgquestoes = findViewById(R.id.fmResult_msgquestoes);
        if (acertos == 10) {
            msgquestoes.setText("VOCÊ FOI PERFEITO!!! PARABÉNS!!!!! \n ACERTOU TODAS AS QUESTÕES! \n\n QI+100!!! ");
        }else {
            if (acertos >= 6) {
                msgquestoes.setText("Você acertou " + String.valueOf(acertos) + " questões! Parabéns pelo resultado, está na média!!!!");
            } else {
                if (acertos >= 3) {
                    msgquestoes.setText("Que pena você acertou só " + String.valueOf(acertos) + " questões! Volte a estudar mais um pouco!!");
                } else {
                    if (acertos == 0) {
                        msgquestoes.setText("FALA SÉRIO, VC NÃO ACERTOU NENHUMA QUESTÃO!!!!!!!!! \n\n QI MENOR QUE UM PRIMATA!!!");
                    } else {
                        if (acertos == 1) {
                            msgquestoes.setText("Você está de parabéns, conseguiu apenas 1 questão!!!!!");
                        } else {
                            msgquestoes.setText("Só 2 questões? Tá de brincadeira comigo?");
                        }
                    }
                }
            }
        }
        Button btnovoquiz = findViewById(R.id.fmResult_btNovoquiz);
        btnovoquiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preparatelainicial();
            }
        });
    }

    private void preparatelaperguntas(){
        setContentView(R.layout.activity_questoes);

        Button btcancelar = findViewById(R.id.fmQuestao_btiniciar);
        btcancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preparatelainicial();
            }
        });

    }

    private void montapergunta(){
        Random random = new Random();
        respostacorreta = random.nextInt(4) + 1;

        TextView lbnumquestao = findViewById(R.id.fmQuestao_lbnumquestao);
        TextView lbpergunta = findViewById(R.id.fmQuestao_lbpergunta);

        Button bt1 = findViewById(R.id.fmQuestao_btresp1);
        Button bt2 = findViewById(R.id.fmQuestao_btresp2);
        Button bt3 = findViewById(R.id.fmQuestao_btresp3);
        Button bt4 = findViewById(R.id.fmQuestao_btresp4);

        bt3.setVisibility(View.VISIBLE);
        bt4.setVisibility(View.VISIBLE);

        lbnumquestao.setText("Questão "+String.valueOf(numeropergunta)+"/10");

        JSONObject questao;
        try {
            questao = respostas.getJSONObject(numeropergunta-1);
            lbpergunta.setText(Html.fromHtml(questao.getString("question"), Html.FROM_HTML_MODE_LEGACY).toString());

            if (questao.getString("type").equals("boolean")){
                bt3.setVisibility(View.INVISIBLE);
                bt4.setVisibility(View.INVISIBLE);

                respostacorreta = random.nextInt(2) + 1;
            }
            Log.i("Pergunta",Html.fromHtml(questao.getString("question"), Html.FROM_HTML_MODE_LEGACY).toString());
            Log.i("Tipo",questao.getString("type"));
            Log.i("Resposta correta",String.valueOf(respostacorreta)+" - "+questao.getString("correct_answer"));


            JSONArray incorrectAnswersArray = questao.getJSONArray("incorrect_answers");


            bt1.setText(incorrectAnswersArray.getString(0));
            if (questao.getString("type").equals("multiple")) {
                bt2.setText(incorrectAnswersArray.getString(1));
                bt3.setText(incorrectAnswersArray.getString(2));
            }

            switch (respostacorreta){
                case 1:
                    if (questao.getString("type").equals("boolean")) {
                        bt2.setText(incorrectAnswersArray.getString(0));
                    }else {
                        bt4.setText(incorrectAnswersArray.getString(0));
                    }
                    bt1.setText(questao.getString("correct_answer"));
                    break;
                case 2: //so para tirar a mensagem errada de commit XD
                    bt2.setText(questao.getString("correct_answer"));
                    if (questao.getString("type").equals("boolean")) {
                        bt1.setText(incorrectAnswersArray.getString(1));
                    }else{
                        bt4.setText(incorrectAnswersArray.getString(1));
                    }

                    break;
                case 3:
                    bt4.setText(incorrectAnswersArray.getString(2));
                    bt3.setText(questao.getString("correct_answer"));
                    break;
                case 4:
                    bt4.setText(questao.getString("correct_answer"));
                    break;
            }
        }catch (JSONException e){

        }

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificaresposta(respostacorreta == 1);
            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificaresposta(respostacorreta == 2);
            }
        });
        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificaresposta(respostacorreta == 3);
            }
        });
        bt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificaresposta(respostacorreta == 4);
            }
        });

    }

    private void verificaresposta(boolean acertou){
        if (acertou){acertos++;}
        numeropergunta++;
        if (numeropergunta > 10) {
            preparatelaresultado();
        }else{
            montapergunta();
        }
    }

    private void iniciarquiz(){
        final ProgressDialog pDialog = ProgressDialog.show(this, "Aguarde", "Gerando perguntas para o quiz...");
        Handler handler = new Handler(){

            @Override
            public void handleMessage(Message msg) {

                switch (msg.arg1) {
                    case 1000:
                        pDialog.dismiss();

                        numeropergunta = 1;
                        acertos = 0;

                        try {
                            jsonresposta = new JSONObject(msg.getData().getString("resposta").toString());
                            respostas =  jsonresposta.getJSONArray("results");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        preparatelaperguntas();
                        montapergunta();
                        break;
                    default:
                        pDialog.dismiss();
                        String msgresposta = "";
                        switch (msg.arg1) {
                            case 1:msgresposta="Falha na navegação";break;
                            case 11:msgresposta="Resposta do site inválida (conteúdo json inválido)";break;
                            case 12:msgresposta="Retorno inválido do site";break;
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Não foi possível concluir a operação! \n "+msgresposta);
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();

                }

            }
        };
        TConsultaAPI thread = new TConsultaAPI(handler);
        thread.start();
    }

}