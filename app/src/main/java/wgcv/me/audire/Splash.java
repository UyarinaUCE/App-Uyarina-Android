package wgcv.me.audire;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Thread splash = new Thread(){
            public  void run(){
                try{
                    sleep(2500);

                }catch ( InterruptedException e){
                    e.printStackTrace();

                }finally {
                    Intent intent = new Intent(Splash.this, Principal.class);
                    startActivity(intent);
                }
            }
        };
        splash.start();
    }


}
