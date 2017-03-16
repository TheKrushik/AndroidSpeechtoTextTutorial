package info.krushik.speechtotext;

import android.content.Intent;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.krushik.speechtotext.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    SpeechRecognizer recognizer;
    Intent recognizeIntent;
    ArrayList<String> resultStringArrayList;
    TextView tv;
    Button bTalk;
    Button btn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btn2 = (Button) findViewById(R.id.btn2);

        bTalk = (Button) findViewById(R.id.bTalk);
        tv = (TextView) findViewById(R.id.tvSpeech);

        //initialize the recognizer
        recognizer = SpeechRecognizer.createSpeechRecognizer(this);

        //initialize the intent what will be registered with the recognizer later
        recognizeIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        recognizeIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizeIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
        recognizeIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        recognizeIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "com.androidtutorialpoint.androidspeechtotexttutorial");
        recognizeIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        recognizeIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 20);

        //register the intent with the recognizer
        recognizer.setRecognitionListener(listener);

        //register click listener with the button and start listening when the user press the button
        bTalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recognizer.startListening(recognizeIntent);
                bTalk.setBackgroundResource(R.drawable.ic_play);
            }
        });
    }

    RecognitionListener listener = new RecognitionListener() {

        @Override
        public void onReadyForSpeech(Bundle params) {

        }

        @Override
        public void onBeginningOfSpeech() {
            viewChanger(false, "Listening…");
        }

        @Override
        public void onRmsChanged(float rmsdB) {

        }

        @Override
        public void onBufferReceived(byte[] buffer) {

        }

        @Override
        public void onEndOfSpeech() {
            viewChanger(true, "Loading…");
        }

        @Override
        public void onError(int error) {
            viewChanger(true, "Error Ocurred");
            bTalk.setBackgroundResource(R.drawable.ic_microphone);
        }

        @Override
        public void onResults(Bundle results) {
            resultStringArrayList = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            bTalk.setBackgroundResource(R.drawable.ic_microphone);
            if (resultStringArrayList != null) {
                viewChanger(true, resultStringArrayList.get(0));
            } else {
                viewChanger(true, "Couldn’t recognize");
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {

        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }
    };


    private void viewChanger(boolean isEnabled, String s) {

        bTalk.setEnabled(isEnabled);
        tv.setText(s);

    }

    public void OnClick(View v) {
        switch (v.getId()) {
            case R.id.btn2:
                Intent intent = new Intent(this, SpeechRepeatActivity.class);
                startActivity(intent);
                break;
        }
    }

}
