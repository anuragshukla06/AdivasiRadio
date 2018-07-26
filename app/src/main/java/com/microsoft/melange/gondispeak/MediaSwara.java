package com.microsoft.melange.gondispeak;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MediaSwara extends FragmentActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    // WebCrawler object will be used to start crawling on root Url
    private WebCrawler crawler;
    // count variable for url crawled so far
    int crawledUrlCount;
    // state variable to check crawling status
    boolean crawlingRunning;
    // For sending message to Handler in order to stop crawling after 60000 ms
    private static final int MSG_STOP_CRAWLING = 111;
    private static final int CRAWLING_RUNNING_TIME = 60000;

    private TextView progressText;

    private String CGNET_ROOT_URL = "http://cgnetswara.org";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_swara);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        String[] dummyData = {"हम सबे महिला लोग बलवान-बलवान बलवान हैं", "आने वाली पीढ़ी उसका उपयोग कर सके, इसके लिए हमें किसी भी पेड़ पर लगे फलो को पूरा न तोड़के उसमे कुछ फल छोड़ देना चाहिए, जैसे 100 लगे है तो 60 उपयोग करे और 40 छोड़ दें, दूसरा कारण ये है कि जिस तरह इंसान बीमार पड़ते है वैसे ही जानवर भी बीमार पड़ते है, तब हम अपने पालतू पशुओ का ईलाज कर लेते है, लेकिन वन्य जीव जो जंगल में रहते हैं उनके लिए ये बचे फल ही काम आते है, और जिनका उपयोग नही हो पाता वे पौधे बनते जाते हैं, इस तरह से हम विलुप्त होते औषधियों को भी बचा सकते है"};

        // specify an adapter (see also next example)
        mAdapter = new MediaSwaraListAdapter(dummyData);
        mRecyclerView.setAdapter(mAdapter);

        progressText = (TextView) findViewById(R.id.progressText);
        crawler = new WebCrawler(this, mCallback);


        crawlingRunning = true;
        crawler.startCrawlerTask(CGNET_ROOT_URL, true);

        // Send delayed message to handler for stopping crawling
        handler.sendEmptyMessageDelayed(MSG_STOP_CRAWLING,
                CRAWLING_RUNNING_TIME);
    }

    private Handler handler;

    {
        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                stopCrawling();
            }

            ;
        };
    }

    /**
     * callback for crawling events
     */
    private WebCrawler.CrawlingCallback mCallback = new WebCrawler.CrawlingCallback() {

        @Override
        public void onPageCrawlingCompleted() {
            crawledUrlCount++;
            progressText.post(new Runnable() {

                @Override
                public void run() {
                    progressText.setText(crawledUrlCount
                            + " pages crawled so far!!");

                }
            });
        }

        @Override
        public void onPageCrawlingFailed(String Url, int errorCode) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onCrawlingCompleted() {
            stopCrawling();
        }
    };

    /**
     * API to handle post crawling events
     */
    private void stopCrawling() {
        if (crawlingRunning) {
            crawler.stopCrawlerTasks();
            //crawlingInfo.setVisibility(View.INVISIBLE);
            //startButton.setEnabled(true);
            //startButton.setVisibility(View.VISIBLE);
            crawlingRunning = false;
            if (crawledUrlCount > 0)
                Toast.makeText(getApplicationContext(),printCrawledEntriesFromDb() + "pages crawled",Toast.LENGTH_SHORT).show();

            crawledUrlCount = 0;
            progressText.setText("");
        }
    }

    /**
     * API to output crawled urls in logcat
     *
     * @return number of rows saved in crawling database
     */
    protected int printCrawledEntriesFromDb() {

        int count = 0;
        CrawlerDB mCrawlerDB = new CrawlerDB(this);
        SQLiteDatabase db = mCrawlerDB.getReadableDatabase();

        Cursor mCursor = db.query(CrawlerDB.TABLE_NAME, null, null, null, null,
                null, null);
        if (mCursor != null && mCursor.getCount() > 0) {
            count = mCursor.getCount();
            mCursor.moveToFirst();
            int columnIndex = mCursor
                    .getColumnIndex(CrawlerDB.COLUMNS_NAME.CRAWLED_URL);
            for (int i = 0; i < count; i++) {
                Log.d("AndroidSRC_Crawler",
                        "Crawled Url " + mCursor.getString(columnIndex));
                mCursor.moveToNext();
            }
        }

        return count;
    }

    public class MediaSwaraListAdapter extends RecyclerView.Adapter<MediaSwaraListAdapter.ViewHolder> {
        private String[] mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView mTextView;
            public ViewHolder(View v) {
                super(v);
                mTextView = v.findViewById(R.id.title);
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MediaSwaraListAdapter(String[] myDataset) {
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MediaSwaraListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.media_swara_textview, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.mTextView.setText(mDataset[position]);

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.length;
        }
    }
}
