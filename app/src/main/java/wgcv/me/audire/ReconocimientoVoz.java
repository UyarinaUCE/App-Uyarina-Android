package wgcv.me.audire;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class ReconocimientoVoz extends AppCompatActivity {
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1;
    private ImageButton bt_start;
    private TextView txtVoz;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reconocimiento_voz);
        bt_start = (ImageButton)findViewById(R.id.btnMic);
        txtVoz = (TextView)findViewById(R.id.txtVoz);
        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Lanzamos el reconoimiento de voz
                startVoiceRecognitionActivity();
            }
        });
    }

    private void startVoiceRecognitionActivity() {
        // Definición del intent para realizar en análisis del mensaje
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // Indicamos el modelo de lenguaje para el intent
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Definimos el mensaje que aparecerá
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Escuchando...");
        // Lanzamos la actividad esperando resultados
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }


    @Override
    //Recogemos los resultados del reconocimiento de voz
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Si el reconocimiento a sido bueno
        if(requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK){
            //El intent nos envia un ArrayList aunque en este caso solo
            //utilizaremos la pos.0
            ArrayList<String> matches = data.getStringArrayListExtra
                    (RecognizerIntent.EXTRA_RESULTS);
            //Separo el texto en palabras.
            String [ ] palabras = matches.get(0).toString().split(" ");
            //Si la primera palabra es LLAMAR
            String oracion="";
            if(palabras[0].toLowerCase().equals("salir")){
                ReconocimientoVoz.this.finish();

                }
            else if(palabras[0].toLowerCase().equals("ecualizador")){
                Intent intent = new Intent(ReconocimientoVoz.this, Equalizador.class);
                startActivity(intent);
            }
            else {
            for (int i=0; i<palabras.length;i++){
                oracion=oracion+palabras[i]+" ";

            }
                txtVoz.setText(oracion);


            }
        }

    }
}