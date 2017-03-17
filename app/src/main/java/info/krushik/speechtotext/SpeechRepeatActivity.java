package info.krushik.speechtotext;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TextView;

import com.krushik.speechtotext.R;

/**
 * SpeechRepeatActivity
 * - processes speech input
 * - presents user with list of suggested words
 * - when user selects a word from the list, the app speaks the word back using the TTS engine
 */
public class SpeechRepeatActivity extends Activity implements OnClickListener, OnInitListener {

    //переменная для проверки возможности распознавания голоса в телефоне
    private static final int VR_REQUEST = 999;

    //переменная для проверки данных для TTS
    private int MY_DATA_CHECK_CODE = 0;

    //Text To Speech интерфейс
    private TextToSpeech repeatTTS;

    //ListView для отображения распознанных слов
    private ListView wordList;

    //Log для вывода вспомогательной информации
    private final String LOG_TAG = "SpeechRepeatActivity";

    /**
     * Create the Activity, prepare to process speech and repeat
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        //вызов суперкласса
        super.onCreate(savedInstanceState);
        //установка контекста вывода
        setContentView(R.layout.activity_speech_repeat);

        //gain reference to speak button
        Button speechBtn = (Button) findViewById(R.id.speech_btn);
        //gain reference to word list
        wordList = (ListView) findViewById(R.id.word_list);

        //проверяем, поддерживается ли распознование речи
        PackageManager packManager = getPackageManager();
        List<ResolveInfo> intActivities = packManager.queryIntentActivities
                (new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (intActivities.size() != 0) {
            //распознавание поддерживается, будем отслеживать событие щелчка по кнопке
            speechBtn.setOnClickListener(this);
            //подготовка движка TTS для проговаривания слов
            Intent checkTTSIntent = new Intent();
            //проверка наличия TTS
            checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
            //запуск checkTTSIntent интента
            startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
        } else {
            //распознавание не работает. Заблокируем кнопку и выведем соответствующее предупреждение.
            speechBtn.setEnabled(false);
            Toast.makeText(this, "Oops - Speech recognition not supported!", Toast.LENGTH_LONG).show();
        }

        //засекаем щелчок пользователя по слову из списка
        wordList.setOnItemClickListener(new OnItemClickListener() {

            //метод вызывается в ответ на щелчок по слову
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //записываем в переменную TextView строки
                TextView wordView = (TextView) view;
                //получаем строку с текстом
                String wordChosen = (String) wordView.getText();
                //выводим ее в лог для отладки
                Log.v(LOG_TAG, "chosen: " + wordChosen);
                //speak the word using the TTS
                repeatTTS.speak("You said: " + wordChosen, TextToSpeech.QUEUE_FLUSH, null);
                //выводим Toast сообщение
                Toast.makeText(SpeechRepeatActivity.this, "You said: " + wordChosen, Toast.LENGTH_SHORT).show();//**alter for your Activity name***
            }
        });
    }

    /**
     * Called when the user presses the speak button
     */
    public void onClick(View v) {
        if (v.getId() == R.id.speech_btn) {
            //listen for results
            listenToSpeech();
        }
    }

    /**
     * Instruct the app to listen for user speech input
     */
    private void listenToSpeech() {

        //запускаем интент, распознающий речь и передаем ему требуемые данные
        Intent listenIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //указываем пакет
        listenIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
        //В процессе распознования выводим сообщение
        listenIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say a word!");
        //устанавливаем модель речи
        listenIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //указываем число результатов, которые могут быть получены
        listenIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);

        //начинаем прослушивание
        startActivityForResult(listenIntent, VR_REQUEST);
    }

    /**
     * onActivityResults handles:
     * - retrieving results of speech recognition listening
     * - retrieving result of TTS data check
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //проверяем результат распознавания речи
        if (requestCode == VR_REQUEST && resultCode == RESULT_OK) {
            //Добавляем распознанные слова в список результатов
            ArrayList<String> suggestedWords = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            //Передаем список возможных слов через ArrayAdapter компоненту ListView
            wordList.setAdapter(new ArrayAdapter<String>(this, R.layout.word, suggestedWords));
        }

        //tss код здесь
        //returned from TTS data check
        if (requestCode == MY_DATA_CHECK_CODE) {
            //все необходимые приложения установлены, создаем TTS
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS)
                repeatTTS = new TextToSpeech(this, this);
                //движок не установлен, предположим пользователю установить его
            else {
                //intent will take user to TTS download page in Google Play
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }

        //вызываем метод родительского класса
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * onInit fires when TTS initializes
     */
    public void onInit(int initStatus) {
        //if successful, set locale
        if (initStatus == TextToSpeech.SUCCESS)
            repeatTTS.setLanguage(Locale.UK);//Язык

    }
}
