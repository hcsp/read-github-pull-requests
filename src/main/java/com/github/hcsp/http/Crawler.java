package com.github.hcsp.http;

import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    static class GitHubPullRequest {
        // Pull request的编号
        int number;
        // Pull request的标题
        String title;
        // Pull request的作者的 GitHub 用户名
        String author;

        GitHubPullRequest(int number, String title, String author) {
            this.number = number;
            this.title = title;
            this.author = author;
        }
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        ArrayList<GitHubPullRequest> gitHubPullRequests = new ArrayList<>();
        List<GHPullRequest> pullRequests = GitHub.connectAnonymously().getRepository(repo).getPullRequests(GHIssueState.OPEN);
        for (GHPullRequest pullRequest : pullRequests) {
            int number = pullRequest.getNumber();
            String title = pullRequest.getTitle();
            String author = pullRequest.getUser().getLogin();
            gitHubPullRequests.add(new GitHubPullRequest(number, title, author));
        }
        return gitHubPullRequests;
    }
}
