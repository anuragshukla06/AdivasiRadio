package com.microsoft.melange.gondispeak;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Locale;

import edu.cmu.cs.speech.tts.flite.CheckVoiceData;
import edu.cmu.cs.speech.tts.flite.DownloadVoiceData;
import edu.cmu.cs.speech.tts.flite.Voice;

public class Dashboard extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private final static String LOG_TAG = "Flite_Java_" + Dashboard.class.getSimpleName();

    public static Dashboard dashboard_activity;
    public static final int WRITE_REQUEST_CODE = 1;

    public static Dashboard getDashboard() {
        return dashboard_activity;
    }

    // Data variables
    private ArrayList<Voice> mVoices;

    private TextToSpeech mTts;
    private int mSelectedVoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET};
        requestPermissions(permissions, WRITE_REQUEST_CODE);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        // Click events to Navigate to other activities
        dashboard_activity = this;
        Button typeGondi = (Button) findViewById(R.id.imageButton2);
        typeGondi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(dashboard_activity, TypeGondi.class);
                startActivity(intent);
            }
        });
        Button swaraButton = (Button) findViewById(R.id.imageButton3);
        swaraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(dashboard_activity, MediaActivity.class);
                startActivity(intent);
            }
        });
        Button historyButton = (Button) findViewById(R.id.imageButton4);
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(dashboard_activity, History.class);
                startActivity(intent);
            }
        });


        // Initialize voices ONLY WHEN WE GET PERMISSION
        //initVoices();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case WRITE_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initVoices();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    public void initVoices() {
        ArrayList<Voice> allVoices = CheckVoiceData.getVoices();
        mVoices = new ArrayList<Voice>();
        for(Voice vox:allVoices) {
            if (vox.isAvailable()) {
                mVoices.add(vox);
                System.out.println(vox.getVariant());
            }
        }

        if (mVoices.isEmpty()) {
            // We can't demo anything if there are no voices installed.
            /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Flite voices not installed. Please add voices in order to run the demo");
            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    finish();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();*/
            Snackbar.make(findViewById(R.id.dashboard_layout), "Flite voices not installed. Please add voices in order to run the demo", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        else {
            // Initialize the TTS
            if (android.os.Build.VERSION.SDK_INT >=
                    android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                mTts = new TextToSpeech(dashboard_activity, dashboard_activity, "edu.cmu.cs.speech.tts.flite");
            }
            else {
                mTts = new TextToSpeech(dashboard_activity, dashboard_activity);
            }
            mSelectedVoice = 0;

        }
    }

    public void sendText(String text) {
        //text = mUserText.getText().toString();
        /*
        if (text.isEmpty())
            return;
        mAdapter.add(text);
        mUserText.setText(null);
        sayText(text);
        */
    }

    public void sayText(String text, int mode) {
        Log.v(LOG_TAG, "Speaking: " + text);


        //TODO: GET FROM SETTINGS LATER
        //int currentVoiceID = mVoiceSpinner.getSelectedItemPosition();
        int currentVoiceID = 0;
        if (currentVoiceID != mSelectedVoice) {
            mSelectedVoice = currentVoiceID;
            Voice v = mVoices.get(currentVoiceID);
            mTts.setLanguage(v.getLocale());
        }

        //int currentRate = mRateSpinner.getSelectedItemPosition();
        int currentRate = 2;
        mTts.setSpeechRate((float)(currentRate + 1)/3);

        mTts.speak(text, mode, null);
    }

    public void stopSpeaking() {
        mTts.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.voices_list) {
            Intent intent = new Intent(dashboard_activity, DownloadVoiceData.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_settings) {
            //Intent intent = new Intent(dashboard_activity, DownloadVoiceData.class);
            //startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTts != null)
            mTts.shutdown();
    }

    @Override
    public void onInit(int status) {
        boolean success = true;
        if (status == TextToSpeech.ERROR) {
            success = false;
        }

        if (success &&
                (android.os.Build.VERSION.SDK_INT >=
                        android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH)) {
            status = mTts.setEngineByPackageName("edu.cmu.cs.speech.tts.flite");
        }

        if (status == TextToSpeech.ERROR) {
            success = false;
        }

        // REALLY check that it is flite engine that has been initialized
        // This is done using a hack, for now, since for API < 14
        // there seems to be no way to check which engine is being used.

        if (mTts.isLanguageAvailable(new Locale("eng", "USA", "is_flite_available"))
                != TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE) {
            success = false;
        }

        if (!success) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Flite TTS Engine could not be initialized. Check that Flite is enabled on your phone!. In some cases, you may have to select flite as the default engine.");
            builder.setNegativeButton("Open TTS Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.TextToSpeechSettings"));
                    startActivity(intent);
                    finish();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else {
            //buildUI();
        }
    }
}
