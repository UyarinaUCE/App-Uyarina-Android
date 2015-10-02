package wgcv.me.audire;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class Principal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
    }

public void click_Reconocimiento(View v){
    Intent intent = new Intent(Principal.this, ReconocimientoVoz.class);
    startActivity(intent);
}
    public void click_equalizador(View v){
        Intent intent = new Intent(Principal.this, Equalizador.class);
        startActivity(intent);
    }
}
