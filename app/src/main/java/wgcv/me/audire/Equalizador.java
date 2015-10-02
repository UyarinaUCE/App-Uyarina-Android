package wgcv.me.audire;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class Equalizador extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener,
        CompoundButton.OnCheckedChangeListener,
        View.OnClickListener {
    static final int bufferSize = 200000;
    final short[] buffer = new short[bufferSize];
    short[] readBuffer = new short[bufferSize];
    private AudioRecord arec = null;
    private Boolean isRecording;
    private Boolean eco=false;
    boolean ns =false;
    AcousticEchoCanceler aec;
    AudioTrack atrack;
    AudioManager localAudioManager;

    //EQUILIZADOR
    TextView bass_boost_label = null;
    SeekBar bass_boost = null;
    CheckBox enabled = null;
    Button flat = null;

    Equalizer eq = null;
    BassBoost bb = null;

    int min_level = 0;
    int max_level = 100;

    static final int MAX_SLIDERS = 8; // Must match the XML layout
    SeekBar sliders[] = new SeekBar[MAX_SLIDERS];
    TextView slider_labels[] = new TextView[MAX_SLIDERS];
    int num_sliders = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equalizador);


    }
    public void equalizador(View v){
        //equalizador

        enabled = (CheckBox)findViewById(R.id.enabled);
        enabled.setOnCheckedChangeListener (Equalizador.this);

        flat = (Button)findViewById(R.id.flat);
        flat.setOnClickListener(Equalizador.this);

        bass_boost = (SeekBar)findViewById(R.id.bass_boost);
        bass_boost.setOnSeekBarChangeListener(Equalizador.this);
        bass_boost_label = (TextView) findViewById (R.id.bass_boost_label);

        sliders[0] = (SeekBar)findViewById(R.id.slider_1);
        slider_labels[0] = (TextView)findViewById(R.id.slider_label_1);
        sliders[1] = (SeekBar)findViewById(R.id.slider_2);
        slider_labels[1] = (TextView)findViewById(R.id.slider_label_2);
        sliders[2] = (SeekBar)findViewById(R.id.slider_3);
        slider_labels[2] = (TextView)findViewById(R.id.slider_label_3);
        sliders[3] = (SeekBar)findViewById(R.id.slider_4);
        slider_labels[3] = (TextView)findViewById(R.id.slider_label_4);
        sliders[4] = (SeekBar)findViewById(R.id.slider_5);
        slider_labels[4] = (TextView)findViewById(R.id.slider_label_5);
        sliders[5] = (SeekBar)findViewById(R.id.slider_6);
        slider_labels[5] = (TextView)findViewById(R.id.slider_label_6);
        sliders[6] = (SeekBar)findViewById(R.id.slider_7);
        slider_labels[6] = (TextView)findViewById(R.id.slider_label_7);
        sliders[7] = (SeekBar)findViewById(R.id.slider_8);
        slider_labels[7] = (TextView)findViewById(R.id.slider_label_8);

        eq = new Equalizer (0,atrack.getAudioSessionId() );
        if (eq != null)
        {
            eq.setEnabled (true);
            int num_bands = eq.getNumberOfBands();
            num_sliders = num_bands;
            short r[] = eq.getBandLevelRange();
            min_level = r[0];
            max_level = r[1];
            for (int i = 0; i < num_sliders && i < MAX_SLIDERS; i++)
            {
                int[] freq_range = eq.getBandFreqRange((short)i);
                sliders[i].setOnSeekBarChangeListener(Equalizador.this);
                slider_labels[i].setText (formatBandLabel (freq_range));
            }
        }
        for (int i = num_sliders ; i < MAX_SLIDERS; i++)
        {
            sliders[i].setVisibility(View.GONE);
            slider_labels[i].setVisibility(View.GONE);
        }

        bb = new BassBoost (0, 0);
        if (bb != null)
        {
        }
        else
        {
            bass_boost.setVisibility(View.GONE);
            bass_boost_label.setVisibility(View.GONE);
        }

        updateUI();
    }
public void empezar(View v){
    new Thread(new capturarAudio()).start();

}
    public void ns(View V){
                if(ns){
            localAudioManager.setParameters("noise_suppression=off");
                    Toast.makeText(Equalizador.this, "Reducción de ruido activado", Toast.LENGTH_SHORT).show();
                    ns=!ns;
        }else{
            localAudioManager.setParameters("noise_suppression=auto");
                    Toast.makeText(Equalizador.this, "Reducción de ruido desactivado", Toast.LENGTH_SHORT).show();
                    ns=!ns;
        }
    }
    public void echo(View V){
        try {

          aec = AcousticEchoCanceler.create(arec.getAudioSessionId());
if(aec!=null){
    Toast.makeText(Equalizador.this, "Se cancelo el eco", Toast.LENGTH_SHORT).show();
}else{
    Toast.makeText(Equalizador.this, "No es soportado por este dispositivo", Toast.LENGTH_SHORT).show();

}

    }catch (Exception e){

        }
    }
    class capturarAudio implements Runnable {

        @Override
        public void run() {
            isRecording = true;
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
            int buffersize = AudioRecord.getMinBufferSize(11025, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
            arec = new AudioRecord(MediaRecorder.AudioSource.MIC, 11025, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, buffersize);
            //////NS reducer https://code.google.com/p/android-source-browsing/source/browse/tests/tests/media/src/android/media/cts/AudioPreProcessingTest.java?repo=platform--cts&r=e012e2abe7d829daace218e2a284766eea5e9613

            atrack = new AudioTrack(AudioManager.STREAM_MUSIC, 11025, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, buffersize, AudioTrack.MODE_STREAM);
            atrack.setPlaybackRate(11025);
            byte[] buffer = new byte[buffersize];
            arec.startRecording();
            atrack.play();
            Context context = getApplicationContext();
            localAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
          //  localAudioManager.setSpeakerphoneOn(true);

            while(isRecording) {
                arec.read(buffer, 0, buffersize);
                atrack.write(buffer, 0, buffer.length);
            }
        }
    }
    @Override
    public void onProgressChanged (SeekBar seekBar, int level,
                                   boolean fromTouch)
    {
        if (seekBar == bass_boost)
        {
            bb.setEnabled (level > 0 ? true : false);
            bb.setStrength ((short)level); // Already in the right range 0-1000
        }
        else if (eq != null)
        {
            int new_level = min_level + (max_level - min_level) * level / 100;

            for (int i = 0; i < num_sliders; i++)
            {
                if (sliders[i] == seekBar)
                {
                    eq.setBandLevel ((short)i, (short)new_level);
                    break;
                }
            }
        }
    }

    /*=============================================================================
        onStartTrackingTouch
    =============================================================================*/
    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {
    }

    /*=============================================================================
        onStopTrackingTouch
    =============================================================================*/
    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {
    }

    /*=============================================================================
        formatBandLabel
    =============================================================================*/
    public String formatBandLabel (int[] band)
    {
        return milliHzToString(band[0]) + "-" + milliHzToString(band[1]);
    }

    /*=============================================================================
        milliHzToString
    =============================================================================*/
    public String milliHzToString (int milliHz)
    {
        if (milliHz < 1000) return "";
        if (milliHz < 1000000)
            return "" + (milliHz / 1000) + "Hz";
        else
            return "" + (milliHz / 1000000) + "kHz";
    }

    /*=============================================================================
        updateSliders
    =============================================================================*/
    public void updateSliders ()
    {
        for (int i = 0; i < num_sliders; i++)
        {
            int level;
            if (eq != null)
                level = eq.getBandLevel ((short)i);
            else
                level = 0;
            int pos = 100 * level / (max_level - min_level) + 50;
            sliders[i].setProgress (pos);
        }
    }

    /*=============================================================================
        updateBassBoost
    =============================================================================*/
    public void updateBassBoost ()
    {
        if (bb != null)
            bass_boost.setProgress (bb.getRoundedStrength());
        else
            bass_boost.setProgress (0);
    }

    /*=============================================================================
        onCheckedChange
    =============================================================================*/
    @Override
    public void onCheckedChanged (CompoundButton view, boolean isChecked)
    {
        if (view == (View) enabled)
        {
            eq.setEnabled (isChecked);
        }
    }

    /*=============================================================================
        onClick
    =============================================================================*/
    @Override
    public void onClick (View view)
    {
        if (view == (View) flat)
        {
            setFlat();
        }
    }

    /*=============================================================================
        updateUI
    =============================================================================*/
    public void updateUI ()
    {
        updateSliders();
        updateBassBoost();
        enabled.setChecked (eq.getEnabled());
    }

    /*=============================================================================
        setFlat
    =============================================================================*/
    public void setFlat ()
    {
        if (eq != null)
        {
            for (int i = 0; i < num_sliders; i++)
            {
                eq.setBandLevel ((short)i, (short)0);
            }
        }

        if (bb != null)
        {
            bb.setEnabled (false);
            bb.setStrength ((short)0);
        }

        updateUI();
    }

}
