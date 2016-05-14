package com.example.johannes.speakforme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Locale;

class SentencesAdapter extends ArrayAdapter<String> {
    public SentencesAdapter(Context context) {
        super(context, R.layout.list_item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        String sentence = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        // Lookup view for data population
        TextView tvsentence = (TextView) convertView.findViewById(R.id.list_sentence);
        TextView tvbutton = (TextView) convertView.findViewById(R.id.list_button_speak);
        TextView tvbutton_delete = (TextView) convertView.findViewById(R.id.list_button_delete);
        // Populate the data into the template view using the data object
        tvsentence.setText(sentence);
        tvbutton.setTag(position);
        tvbutton_delete.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
}

public class MainScreen extends AppCompatActivity {

    SentencesAdapter adapter;
    TextToSpeech tts;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                break;

        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        adapter = new SentencesAdapter(this);
        loadSavedSentences();

        ListView listview = (ListView) findViewById(R.id.message_list);
        listview.setAdapter(adapter);

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

            }
        });
    }

    public void sendMessage(View view) {
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        adapter.add(message);

        // Speak message (only if corresponding setting is set)
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean speak_as_send = sharedPref.getBoolean("pref_speak_as_send", false);

        if (speak_as_send) {
            speak(message);
        }

    }

    public void speak(String message) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String language = sharedPref.getString("pref_language", "");

        Locale locale = new Locale(language);
        tts.setLanguage(locale);

        tts.speak(message, TextToSpeech.QUEUE_ADD, null);
    }

    public void speakMessageFromTextentry(View view) {
        EditText editText = (EditText) findViewById(R.id.edit_message);
        speak(editText.getText().toString());
    }

    public void speakMessageFromList(View view) {
        int position = Integer.parseInt(view.getTag().toString());
        String message = adapter.getItem(position);
        speak(message);
    }

    public void deleteMessageFromList(View view) {
        int position = Integer.parseInt(view.getTag().toString());
        String message = adapter.getItem(position);
        adapter.remove(message);
    }

    void loadSavedSentences() {
        // Loads saved sentences and adds them to the list at the main screen

        File f = new File(this.getFilesDir(), getString(R.string.user_sentences_file));

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(f));
            String line;
            while ((line = reader.readLine()) != null) {
                adapter.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void saveUserSentences() {

        File file = new File(this.getFilesDir(), getString(R.string.user_sentences_file));

        try {
            PrintWriter writer = new PrintWriter(file);
            for(int i=0 ; i<adapter.getCount() ; i++){
                String current_sentence = adapter.getItem(i);
                writer.println(current_sentence);
            }
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void onStop() {
        super.onStop();
        saveUserSentences();
    }

    public void onDestroy() {
        super.onDestroy();
        tts.shutdown();
    }
}
