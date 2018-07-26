package com.microsoft.melange.gondispeak;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class TypeGondi extends AppCompatActivity {

    private Dashboard dashboard_activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type_gondi);

        dashboard_activity = Dashboard.getDashboard();

        final EditText editText = (EditText) findViewById(R.id.editText);
        Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dashboard_activity.sayText(editText.getText().toString());
            }
        });
    }
}
