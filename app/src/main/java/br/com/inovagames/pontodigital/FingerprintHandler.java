package br.com.inovagames.pontodigital;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static android.content.Context.MODE_APPEND;

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

    private Context context;

    public FingerprintHandler(Context context){
        this.context = context;
    }

    public void startAuth(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject){
        CancellationSignal cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        this.exibeTexto("A autenticação já tinha falhado. " + errString, false);
    }

    @Override
    public void onAuthenticationFailed() {
        this.exibeTexto("Autenticação Falhou. ", false);
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        this.exibeTexto("Erro: " + helpString, false);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        this.exibeTexto("Ponto Registrado com sucesso!.", true);

        // Digital aceita montar data
        Date dataHoraAtual = new Date();
        String data = new SimpleDateFormat("dd/MM/yyyy").format(dataHoraAtual);
        String hora = new SimpleDateFormat("HH:mm:ss").format(dataHoraAtual);

        int dia = Integer.parseInt(new SimpleDateFormat("dd").format(dataHoraAtual));
        int mes = Integer.parseInt(new SimpleDateFormat("MM").format(dataHoraAtual));
        int ano = Integer.parseInt(new SimpleDateFormat("yyyy").format(dataHoraAtual));

        // Montando o registro para salvar (montando com o dia da semana)
        String dataRegistroPonto = new String(this.retornarDiaSemana(ano, mes, dia) + " " + data +" "+ hora + "");

        // Atualizando lista e salvando em txt
        this.updateList(dataRegistroPonto);
    }
    
    
    public String retornarDiaSemana(int ano, int mes, int dia) {
        Calendar calendario = new GregorianCalendar(ano, mes - 1, dia);
        int diaSemana = calendario.get(Calendar.DAY_OF_WEEK);
        return pesquisarDiaSemana(diaSemana);
    }

    public String pesquisarDiaSemana(int _diaSemana) {
        String diaSemana = null;

        switch (_diaSemana) {

            case 1:
            {
              diaSemana = "Domingo";
              break;
            }
            case 2:
            {
              diaSemana = "Segunda";
              break;
            }
            case 3:
            {
              diaSemana = "Terça";
              break;
            }
            case 4:
            {
              diaSemana = "Quarta";
              break;
            }
            case 5:
            {
              diaSemana = "Quinta";
              break;
            }
            case 6:
            {
              diaSemana = "Sexta";
              break;
            }
            case 7:
            {
              diaSemana = "Sábado";
              break;
            }
        }

        return diaSemana;
    }


    public void reload() {
        Intent intent = ((Activity)context).getIntent();
        ((Activity)context).overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        ((Activity)context).finish();
        ((Activity)context).overridePendingTransition(0, 0);
        ((Activity)context).startActivity(intent);
    }

    private void updateList(String s) {
        // Salvar registro
        this.salvar(s);

        // Recarregando app (opcional basta comentar linha abaixo)
        this.reload();
    }


    public void salvar(String texto) {
        // Salvando no arquivo de texto
        try {
            FileOutputStream fileout = ((Activity)context).openFileOutput("ponto4.txt", MODE_APPEND);
            OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);

            // pega tamanha da data montada - o tamanho max dos chars
            int tamanho = 30 - texto.length( );

            // completa o resto da linha com espaços em branco (para nao quebrar o texto em outras linhas)
            for ( ; tamanho > 0; tamanho--) {
                texto += " ";
            }
            // gravando no texto e fechando stream com o txt
            outputWriter.write(texto);
            outputWriter.close();

            // Feedback para o usuário mensagem FLUTUANTE
            Toast.makeText(((Activity)context).getBaseContext(), "Ponto Registrado com Sucesso!",
                    Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            // Feedback para o usuário
            Toast.makeText(((Activity)context).getBaseContext(), "Falha ao Registrar o Ponto!",
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    private void exibeTexto(String s, boolean b) {
        TextView paraLabel = (TextView) ((Activity)context).findViewById(R.id.paraLabel);
        ImageView imageView = (ImageView) ((Activity)context).findViewById(R.id.fingerprintImage);
        paraLabel.setText(s);

        if(b == false){
            paraLabel.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        } else {
            paraLabel.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            imageView.setImageResource(R.mipmap.action_done);
        }
    }
}
