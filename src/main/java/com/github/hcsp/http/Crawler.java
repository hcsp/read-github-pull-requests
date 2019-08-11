package com.github.hcsp.http;

import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    public static void main(String[] args) throws IOException {
        String repo = "gradle/gradle";
        Crawler.GitHubPullRequest firstPull = Crawler.getFirstPageOfPullRequests(repo).get(0);

        System.out.println("firstPull = " + firstPull);
    }
    static class GitHubPullRequest {
        // Pull request的编号
        int number;
        // Pull request的标题
        String title;
        // Pull request的作者的GitHub id
        String author;

        GitHubPullRequest(int number, String title, String author) {
            this.number = number;
            this.title = title;
            this.author = author;
        }
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        GitHub github = GitHub.connectAnonymously();
        GHRepository rp = github.getRepository(repo);
        List<GHPullRequest> prs = rp.getPullRequests(GHIssueState.OPEN);
        final int LIMIT = 25;
        List<GitHubPullRequest> finalPRs = new ArrayList<>();
        for (int i = 0; i < Math.min(prs.size(), LIMIT); i++) {
            GHPullRequest pr = prs.get(i);
            finalPRs.add(new GitHubPullRequest(pr.getNumber(), pr.getTitle(), pr.getUser().getLogin()));
        }
        return finalPRs;

    }
}
