package com.vchannel.cactus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.AsyncTask;
import android.app.ProgressDialog;
import java.io.IOException;

import android.support.v4.widget.SwipeRefreshLayout;

import android.util.SparseArray;
import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    final String LOG_TAG = "myLogs";

    private ArrayList<Issue> issues = new ArrayList<Issue>();
    private IssueAdapter issueAdapter;
    private String url = "https://www.youtube.com/channel/UCgxTPTFbIbCWfTR9I2-5SeQ/videos";
    private ProgressDialog progressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_title);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        issueAdapter = new IssueAdapter(MainActivity.this, issues);

        ListView lvMain = (ListView) findViewById(R.id.listView);
        lvMain.setAdapter(issueAdapter);
/*
        lvMain.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Log.d(LOG_TAG, "itemClick: position = " + position + ", id = " + id);            }
        });
        lvMain.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Log.d(LOG_TAG, "itemSelect: position = " + position + ", id = " + id);
            }

            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(LOG_TAG, "itemSelect: nothing");
            }
        });
        */
/*
        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Issue issue = issueAdapter.getIssue(position);

                // Create a progressdialog
                progressDialog = new ProgressDialog(MainActivity.this);
                // Set progressdialog message
                progressDialog.setMessage("Refresh...");
                progressDialog.setIndeterminate(false);
                // Show progressdialog
                progressDialog.show();

                new YouTubeExtractor(MainActivity.this) {
                    @Override
                    public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                        if (ytFiles != null) {
                            int itag = 22;
                            String videoUrl = ytFiles.get(itag).getUrl();

                        }
                    }
                }.extract(issue.URL, true, true);
            }
        });
        */
/*
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                Intent intent = new Intent(context, SendMessage.class);
                String message = "abc";
                intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
            }
        });
*/
        swipeRefreshLayout.setOnRefreshListener(this);

        refresh();
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    private void refresh() {
        // Execute DownloadJSON AsyncTask
        new JsoupTask().execute();
    }

    // JSoup AsyncTask
    private class JsoupTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // showing refresh animation before making http call
            swipeRefreshLayout.setRefreshing(true);
/*
            */
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // Connect to the Website URL
                Document doc = Jsoup.connect(url).get();

                for (Element div : doc.select("div[data-context-item-id]")) {
                    String id = div.attr("data-context-item-id");
                    String urlString = "https://www.youtube.com/watch?v=" + id;
                    String title = "";
                    String thumb = "";
                    String meta = "";
                    Element titleNode = div.getElementsByClass("yt-uix-sessionlink yt-uix-tile-link  spf-link  yt-ui-ellipsis yt-ui-ellipsis-2").first();
                    if (titleNode != null) {
                        title = titleNode.attr("title");
                    }
                    for (Element thumbNode : div.select("div.yt-lockup-thumbnail")) {
                        for (Element clip : div.select("span.yt-thumb-clip")) {
                            Elements imgSrc = clip.select("img[src]");
                            thumb = imgSrc.attr("src");
                        }
                    }
                    Element metaNode = div.getElementsByClass("yt-lockup-meta-info").first();
                    if (metaNode != null) {
                        for (Element child : metaNode.children()) {
                            meta += child.text() + " ";
                        }
                    }
                    Issue issue = new Issue(urlString, thumb, title, meta);
                    issues.add(issue);
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            issueAdapter.updateList(issues);

            // stopping swipe refresh
            swipeRefreshLayout.setRefreshing(false);

            // Close the progressdialog
//            progressDialog.dismiss();
        }
    }
}
