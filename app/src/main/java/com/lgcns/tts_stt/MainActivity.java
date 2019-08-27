package com.lgcns.tts_stt;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSION = 1;

    private Intent intent;

    private SpeechRecognizer mRecognizer;
    private boolean isListen = false;

    private TextToSpeech tts;

    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            System.out.println("onReadyForSpeech");
            Toast.makeText(getApplicationContext(), "말해주세요.", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onBeginningOfSpeech() {
            System.out.println("onBeginningOfSpeech");
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            System.out.println("onRmsChanged");
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            System.out.println("onBufferReceived");
        }

        @Override
        public void onEndOfSpeech() {
            System.out.println("onEndOfSpeech");
        }

        @Override
        public void onError(int error) {
            System.out.println("onError");
            Toast.makeText(getApplicationContext(), "다시 말해주세요.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            System.out.println("onPartialResults");
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            System.out.println("onEvent");
        }

        @Override
        public void onResults(Bundle results) {
            String key= "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult = results.getStringArrayList(key);
            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);
            ((EditText)findViewById(R.id.text_stt)).setText(rs[0]);
            mRecognizer.stopListening();
            ((Button)findViewById(R.id.btn_stt)).setText("말하기시작");
        }
    };

    Button.OnClickListener sttBtnListener = new View.OnClickListener() {
        public void onClick(View v) {

            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSION);
            } else {
                try {
                    isListen = !isListen;

                    if(isListen) {
                        mRecognizer.startListening(intent);
                        ((Button)findViewById(R.id.btn_stt)).setText("말하기종료");
                    } else {
                        mRecognizer.stopListening();
                        ((Button)findViewById(R.id.btn_stt)).setText("말하기시작");
                    }

                } catch(SecurityException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    Button.OnClickListener ttsBtnListener = new View.OnClickListener() {
        public void onClick(View v) {

            tts.setSpeechRate(1.0f);
            tts.speak(((EditText)findViewById(R.id.text_tts)).getText(), TextToSpeech.QUEUE_FLUSH, null, null);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_stt).setOnClickListener(sttBtnListener);
        findViewById(R.id.btn_tts).setOnClickListener(ttsBtnListener);

        //STT 초기화
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getApplicationContext().getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR");
        mRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        mRecognizer.setRecognitionListener(listener);

        //TTS 초기화
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status!=android.speech.tts.TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });
    }
}
