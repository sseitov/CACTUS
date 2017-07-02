package com.vchannel.cactus;

/**
 * Created by sseitov on 01.07.17.
 */

public class Issue {
    String ID;
    String Thumb;
    String Title;
    String Meta;

    Issue(String id, String thumb, String title, String meta) {
        ID = id;
        Thumb = thumb;
        Title = title;
        Meta = meta;
    }
}
