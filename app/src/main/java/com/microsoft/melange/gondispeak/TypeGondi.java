package com.microsoft.melange.gondispeak;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class TypeGondi extends AppCompatActivity {

    private Dashboard dashboard_activity;
    private HistoryDB historyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_gondi);

        dashboard_activity = Dashboard.getDashboard();
        historyDB = new HistoryDB(this);

        final EditText editText = (EditText) findViewById(R.id.editText);
        Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String saythis = editText.getText().toString();
                dashboard_activity.sayText(saythis, TextToSpeech.QUEUE_FLUSH);
                insertIntoHistoryDB(saythis, 0);
            }
        });
    }

    public void insertIntoHistoryDB(String text, int rating) {

        if (TextUtils.isEmpty(text)){
            return;
        }

        SQLiteDatabase db = historyDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(HistoryDB.COLUMNS_NAME.TEXT, text);
        values.put(HistoryDB.COLUMNS_NAME.RATING, rating);

        db.insert(HistoryDB.TABLE_NAME, null, values);
    }
}
