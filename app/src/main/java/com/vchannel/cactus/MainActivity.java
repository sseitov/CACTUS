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

public class MainActivity extends AppCompatActivity {

    ArrayList<Issue> issues = new ArrayList<Issue>();
    IssueAdapter issueAdapter;
    String url = "https://www.youtube.com/channel/UCgxTPTFbIbCWfTR9I2-5SeQ/videos";
    ProgressDialog progressDialog;

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

        // Execute DownloadJSON AsyncTask
        new JsoupTask().execute();
    }

    // JSoup AsyncTask
    private class JsoupTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressdialog
            progressDialog = new ProgressDialog(MainActivity.this);
            // Set progressdialog message
            progressDialog.setMessage("Refresh...");
            progressDialog.setIndeterminate(false);
            // Show progressdialog
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                // Connect to the Website URL
                Document doc = Jsoup.connect(url).get();
/*
                Elements divs = doc.getElementsByClass("yt-lockup clearfix  yt-lockup-video yt-lockup-grid vve-check");
                Elements divs = doc.getElementsByAttribute("data-context-item-id");
                for (Element div : divs) {
                    String id = div.attr("data-context-item-id");
                }
                */

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
            issueAdapter = new IssueAdapter(MainActivity.this, issues);
            ListView lvMain = (ListView) findViewById(R.id.listView);
            lvMain.setAdapter(issueAdapter);

            // Close the progressdialog
            progressDialog.dismiss();
        }
    }
}
