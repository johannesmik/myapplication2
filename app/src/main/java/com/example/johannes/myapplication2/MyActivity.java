package com.example.johannes.myapplication2;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class MyActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.example.johannes.myapplication2.MESSAGE";

    ArrayAdapter<String> adapter;
    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        ListView listview = (ListView) findViewById(R.id.message_list);
        listview.setAdapter(adapter);

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

            }
        });
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);

        adapter.add(message);
    }

    public void speakMessage(View view) {
        EditText editText = (EditText) findViewById(R.id.edit_message);
        tts.speak(editText.getText().toString(), TextToSpeech.QUEUE_ADD, null);
    }

    public void onDestroy() {
        super.onDestroy();
        tts.shutdown();
    }
}
