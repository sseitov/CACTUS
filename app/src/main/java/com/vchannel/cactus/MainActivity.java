package com.vchannel.cactus;

import android.content.Intent;
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
import android.widget.Toast;

import com.commit451.youtubeextractor.YouTubeExtractionResult;
import com.commit451.youtubeextractor.YouTubeExtractor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, IssuesListener {

    private final YouTubeExtractor extractor = YouTubeExtractor.create();

    private Callback<YouTubeExtractionResult> extractionResultCallback = new Callback<YouTubeExtractionResult>() {
        @Override
        public void onResponse(Call<YouTubeExtractionResult> call, Response<YouTubeExtractionResult> response) {
            // Close the progressdialog
            progressDialog.dismiss();
            bindVideoResult(response.body());
        }

        @Override
        public void onFailure(Call<YouTubeExtractionResult> call, Throwable t) {
            // Close the progressdialog
            progressDialog.dismiss();
            onError(t);
        }
    };
    private void onError(Throwable t) {
        t.printStackTrace();
        Toast.makeText(MainActivity.this, "It failed to extract. So sad", Toast.LENGTH_SHORT).show();
    }


    private void bindVideoResult(YouTubeExtractionResult result) {
        Intent myIntent = new Intent(MainActivity.this, VideoActivity.class);
        Bundle b = new Bundle();
        b.putString("url", result.getSd360VideoUri().toString());
        myIntent.putExtras(b);
        startActivity(myIntent);
    }

    private ArrayList<Issue> issues = new ArrayList<Issue>();
    private IssueAdapter issueAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog progressDialog;

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
        swipeRefreshLayout.setOnRefreshListener(this);

        issueAdapter = new IssueAdapter(MainActivity.this, issues);

        ListView lvMain = (ListView) findViewById(R.id.listView);
        lvMain.setAdapter(issueAdapter);

        refresh();
    }

    public void showIssue(String youtubeID) {
        // Create a progressdialog
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle(R.string.app_title);
        progressDialog.setMessage("Load...");
        progressDialog.setIndeterminate(false);
        // Show progressdialog
        progressDialog.show();

        extractor.extract(youtubeID).enqueue(extractionResultCallback);
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    private void refresh() {
        swipeRefreshLayout.setRefreshing(true);
        // Execute DownloadJSON AsyncTask
        JsoupTask refreshTask = new JsoupTask(this);
        refreshTask.execute("https://www.youtube.com/channel/UCgxTPTFbIbCWfTR9I2-5SeQ/videos");
    }

    public void onAddIssue(Issue issue) {
        issues.add(issue);
    }

    public void onTaskCompleted() {
        issueAdapter.updateList(issues);
        // stopping swipe refresh
        swipeRefreshLayout.setRefreshing(false);
    }

    // JSoup AsyncTask
    private static class JsoupTask extends AsyncTask<String, Void, Void> {
        private IssuesListener listener;

        public JsoupTask(IssuesListener listener){
            this.listener=listener;
        }

        @Override
        protected Void doInBackground(String... params) {
            // Connect to the Website URL
            String url = params[0];

            try {
                Document doc = Jsoup.connect(url)
                        .header("Accept-Encoding", "gzip, deflate")
                        .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                        .maxBodySize(0)
                        .timeout(600000)
                        .get();
                for (Element div : doc.select("div[data-context-item-id]")) {
                    String id = div.attr("data-context-item-id");
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
                    Issue issue = new Issue(id, thumb, title, meta);
                    listener.onAddIssue(issue);
                }
            } catch (IOException ex) {

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            listener.onTaskCompleted();
            super.onPostExecute(result);
        }
    }
}
