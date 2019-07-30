package com.github.hcsp.http;

import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


public class Crawler {
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
        // see https://github-api.kohsuke.org/apidocs/org/kohsuke/github/GHRepository.html#getPullRequests-org.kohsuke.github.GHIssueState-
        List<GHPullRequest> pullRequests = GitHub.connectAnonymously().getRepository(repo).getPullRequests(GHIssueState.OPEN);
        List<GitHubPullRequest> resultList = pullRequests.stream().map(pr -> {
            int number = pr.getNumber();
            String title = pr.getTitle();
            String author = "";
            try {
                author = pr.getUser().getName();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new GitHubPullRequest(number, title, author);
        }).collect(Collectors.toList());
        return resultList;
    }
}
