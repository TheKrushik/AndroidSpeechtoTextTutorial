package info.krushik.speechtotext;

import android.content.Intent;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.krushik.speechtotext.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private SpeechRecognizer recognizer;
    private Intent recognizeIntent;
    private ArrayList<String> resultStringArrayList;
    private TextView m_tvSpeech;
    private Button btnTalk;
    private ListView lvWord;
//    Button btn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        btn2 = (Button) findViewById(R.id.btn2);

        m_tvSpeech = (TextView) findViewById(R.id.tvSpeech);
        btnTalk = (Button) findViewById(R.id.btnTalk);
        lvWord = (ListView) findViewById(R.id.lvWord);

        //initialize the recognizer
        recognizer = SpeechRecognizer.createSpeechRecognizer(this);
        TakeAnswer();


    }

    public void TakeAnswer() {
        //initialize the intent what will be registered with the recognizer later
        recognizeIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        recognizeIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizeIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
        recognizeIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        recognizeIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
        recognizeIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        recognizeIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 20);

        //register the intent with the recognizer
        recognizer.setRecognitionListener(listener);

        //register click listener with the button and start listening when the user press the button
        btnTalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recognizer.startListening(recognizeIntent);
                btnTalk.setBackgroundResource(R.drawable.ic_microphone);
            }
        });
    }

    private RecognitionListener listener = new RecognitionListener() {

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
            viewChanger(true, "Error Occurred");
            btnTalk.setBackgroundResource(R.drawable.ic_play_pronunciation);
        }

        @Override
        public void onResults(Bundle results) {
            resultStringArrayList = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            btnTalk.setBackgroundResource(R.drawable.ic_play_pronunciation);
            if (resultStringArrayList != null) {
                viewChanger(true, resultStringArrayList.get(0));

                lvWord.setAdapter(new ArrayAdapter<String>(MainActivity.this, R.layout.item_word, resultStringArrayList));
            } else {
                viewChanger(true, "Couldn't recognize");
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
        btnTalk.setEnabled(isEnabled);
        m_tvSpeech.setText(s);
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
