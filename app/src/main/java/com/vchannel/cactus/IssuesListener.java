package com.vchannel.cactus;

/**
 * Created by sseitov on 25.11.2017.
 */

public interface IssuesListener {
    void onTaskCompleted();
    void onAddIssue(Issue issue);
}
