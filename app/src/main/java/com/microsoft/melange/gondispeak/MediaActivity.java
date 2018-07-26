package com.microsoft.melange.gondispeak;

/**
 * Ref: https://www.androidhive.info/2016/05/android-working-with-card-view-and-recycler-view/
 * http://androidsrc.net/android-web-crawler-example-multithreaded-implementation/
 */

import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

public class MediaActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArticlesAdapter adapter;
    private List<Article> articleList;
    private ProgressBar pgsBar;

    // WebCrawler object will be used to start crawling on root Url
    private WebCrawler crawler;
    // count variable for url crawled so far
    int crawledUrlCount;
    // state variable to check crawling status
    boolean crawlingRunning;
    // For sending message to Handler in order to stop crawling after 60000 ms
    private static final int MSG_STOP_CRAWLING = 111;
    private static final int CRAWLING_RUNNING_TIME = 10000;

    public static String CGNET_ROOT_URL = "http://cgnetswara.org";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_media);


        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        initCollapsingToolbar();

        pgsBar = (ProgressBar) findViewById(R.id.pBar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        articleList = new ArrayList<>();
        adapter = new ArticlesAdapter(this, articleList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        crawler = new WebCrawler(this, mCallback);
        crawlingRunning = true;
        pgsBar.setVisibility(View.VISIBLE);
        crawler.startCrawlerTask(CGNET_ROOT_URL, true);

        // Send delayed message to handler for stopping crawling
        handler.sendEmptyMessageDelayed(MSG_STOP_CRAWLING,
                CRAWLING_RUNNING_TIME);

        try {
            Glide.with(this).load(R.drawable.cover_new).into((ImageView) findViewById(R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            //Toast.makeText(getApplicationContext(),crawledUrlCount + "pages crawled",Toast.LENGTH_SHORT).show();
            /*progressText.post(new Runnable() {

                @Override
                public void run() {
                    progressText.setText(crawledUrlCount
                            + " pages crawled so far!!");

                }
            });
            */
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
            pgsBar.setVisibility(View.GONE);
            if (crawledUrlCount > 0)
                Toast.makeText(getApplicationContext(),prepareArticlesFromDb() + "pages crawled",Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(),"Failed to download articles from CGNet!",Toast.LENGTH_SHORT).show();

            crawledUrlCount = 0;
            //progressText.setText("");
        }
    }

    private int prepareArticlesFromDb() {

        int[] covers = new int[]{
                R.drawable.album1,
                R.drawable.album2,
                R.drawable.album3,
                R.drawable.album4,
                R.drawable.album5,
                R.drawable.album6,
                R.drawable.album7,
                R.drawable.album8,
                R.drawable.album9,
                R.drawable.album10,
                R.drawable.album11};

        int count = 0;
        CrawlerDB mCrawlerDB = new CrawlerDB(this);
        SQLiteDatabase db = mCrawlerDB.getReadableDatabase();

        Document document;

        Cursor mCursor = db.query(CrawlerDB.TABLE_NAME, null, null, null, null,
                null, null);
        if (mCursor != null && mCursor.getCount() > 0) {
            count = mCursor.getCount();
            mCursor.moveToFirst();
            int url_columnIndex = mCursor
                    .getColumnIndex(CrawlerDB.COLUMNS_NAME.CRAWLED_URL);
            int content_columnIndex = mCursor
                    .getColumnIndex(CrawlerDB.COLUMNS_NAME.CRAWLED_PAGE_CONTENT);
            for (int i = 0; i < count; i++) {
                document = Jsoup.parse(mCursor.getString(content_columnIndex));
                String title_text = document.select("div.report h3").first().text();
                String content_text = document.select("div.report p").first().text();
                String url_text = mCursor.getString(url_columnIndex);
                if (url_text != CGNET_ROOT_URL) {
                    Log.d("Datsbase entry:", url_text);
                    articleList.add(new Article(title_text, content_text, covers[i], url_text));
                }
                mCursor.moveToNext();
            }
        }

        adapter.notifyDataSetChanged();
        mCrawlerDB.close();

        return count;
    }

    /**
     * Adding few albums for testing
     */
    private void prepareAlbums() {
        int[] covers = new int[]{
                R.drawable.album1,
                R.drawable.album2,
                R.drawable.album3,
                R.drawable.album4,
                R.drawable.album5,
                R.drawable.album6,
                R.drawable.album7,
                R.drawable.album8,
                R.drawable.album9,
                R.drawable.album10,
                R.drawable.album11};

        Article a = new Article("True Romance", "testing", covers[0], "");
        articleList.add(a);

        a = new Article("Xscpae", "testing", covers[1], "");
        articleList.add(a);

        a = new Article("Maroon 5", "testing", covers[2], "");
        articleList.add(a);

        a = new Article("Born to Die", "testing", covers[3], "");
        articleList.add(a);

        a = new Article("Honeymoon", "testing", covers[4], "");
        articleList.add(a);

        a = new Article("I Need a Doctor", "testing", covers[5],"");
        articleList.add(a);

        a = new Article("Loud", "testing", covers[6], "");
        articleList.add(a);

        a = new Article("Legend", "testing", covers[7], "");
        articleList.add(a);

        a = new Article("Hello", "testing", covers[8], "");
        articleList.add(a);

        a = new Article("Greatest Hits", "testing", covers[9], "");
        articleList.add(a);

        adapter.notifyDataSetChanged();
    }

    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
