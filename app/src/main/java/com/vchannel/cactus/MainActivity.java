package com.vchannel.cactus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<Issue> issues = new ArrayList<Issue>();
    IssueAdapter issueAdapter;

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

        issueAdapter = new IssueAdapter(this, issues);

        ListView lvMain = (ListView) findViewById(R.id.listView);
        lvMain.setAdapter(issueAdapter);

        String youtubeLink = "https://www.youtube.com/channel/UCgxTPTFbIbCWfTR9I2-5SeQ/videos";
    }
}
