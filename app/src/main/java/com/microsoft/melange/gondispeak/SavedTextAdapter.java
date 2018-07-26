package com.microsoft.melange.gondispeak;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class SavedTextAdapter extends RecyclerView.Adapter<SavedTextAdapter.MyViewHolder> {

    private Context mContext;
    private List<SavedText> savedTextList;
    Dashboard dashboard = Dashboard.getDashboard();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, content;
        public ImageView thumbnail;
        public ImageButton playButton, stopButton;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            content = (TextView) view.findViewById(R.id.article_content);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            playButton = (ImageButton) view.findViewById(R.id.overflow1);
            stopButton = (ImageButton) view.findViewById(R.id.overflow2);
        }
    }

    public SavedTextAdapter(Context mContext, List<SavedText> savedTextList) {
        this.mContext = mContext;
        this.savedTextList = savedTextList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_card, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        SavedText savedText = savedTextList.get(position);
        holder.title.setText("User Text");
        holder.content.setText(savedText.getText());

        // loading album cover using Glide library
        Glide.with(mContext).load(savedText.getThumbnail()).into(holder.thumbnail);

        holder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readOutArticle(holder.title, holder.content);
            }
        });
        holder.stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopReading();
            }
        });
    }

    private void readOutArticle(View title, View content) {
        // inflate menu
        // Activate TTS
        String speak_title = ((TextView) title).getText().toString();
        String speak_content = ((TextView) content).getText().toString();

        Log.d("ARTCILE CONTENT", speak_title);
        //dashboard.sayText(speak_title, TextToSpeech.QUEUE_ADD);
        dashboard.sayText(speak_content, TextToSpeech.QUEUE_ADD);
    }

    private void stopReading() {
        dashboard.stopSpeaking();
    }

    @Override
    public int getItemCount() {
        return savedTextList.size();
    }
}
