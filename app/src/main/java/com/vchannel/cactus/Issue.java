package com.vchannel.cactus;

/**
 * Created by sseitov on 01.07.17.
 */

public class Issue {
    String URL;
    String Thumb;
    String Title;
    String Meta;

    Issue(String url, String thumb, String title, String meta) {
        URL = url;
        Thumb = thumb;
        Title = title;
        Meta = meta;
    }
}
