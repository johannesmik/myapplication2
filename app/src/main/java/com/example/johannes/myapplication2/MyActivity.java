package com.example.johannes.myapplication2;

import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;

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
        // Populate the data into the template view using the data object
        tvsentence.setText(sentence);
        tvbutton.setTag(sentence);
        // Return the completed view to render on screen
        return convertView;
    }
}

public class MyActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.example.johannes.myapplication2.MESSAGE";

    SentencesAdapter adapter;
    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        adapter = new SentencesAdapter(this);
        readData();

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
    }

    public void speakMessage(View view) {
        EditText editText = (EditText) findViewById(R.id.edit_message);
        tts.speak(editText.getText().toString(), TextToSpeech.QUEUE_ADD, null);
    }

    public void speakMessageFromList(View view) {
        String message = view.getTag().toString();
        tts.speak(message, TextToSpeech.QUEUE_ADD, null);
    }

    void readData() {

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

    void saveData() {

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
        saveData();
    }

    public void onDestroy() {
        super.onDestroy();
        tts.shutdown();
    }
}
