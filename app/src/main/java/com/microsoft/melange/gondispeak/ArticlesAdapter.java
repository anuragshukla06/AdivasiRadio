package com.microsoft.melange.gondispeak;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.MyViewHolder> {

    private Context mContext;
    private List<Article> articleList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, content;
        public ImageView thumbnail, overflow;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            content = (TextView) view.findViewById(R.id.article_content);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
        }
    }

    public ArticlesAdapter(Context mContext, List<Article> articleList) {
        this.mContext = mContext;
        this.articleList = articleList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.article_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Article article = articleList.get(position);
        holder.title.setText(article.getTitle());
        holder.content.setText(article.getContent());

        // loading album cover using Glide library
        Glide.with(mContext).load(article.getThumbnail()).into(holder.thumbnail);

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readOutArticle(holder.content);
            }
        });
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void readOutArticle(View view) {
        // inflate menu
        // Activate TTS
        Log.d("ARTCILE CONTENT", ((TextView) view).getText().toString());
    }


    @Override
    public int getItemCount() {
        return articleList.size();
    }
}
