package com.github.hcsp.http;

import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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
        GitHub github = new GitHubBuilder().withOAuthToken("df0e2e7b133cfaf8297b9838bdd35af1e93b2b3b").build();
        GHRepository repository = github.getRepository(repo);
        List<GHPullRequest> pullRequests1 = repository.getPullRequests(GHIssueState.OPEN);
        List<GitHubPullRequest> result = pullRequests1.stream()
                .sorted(Crawler::timeComparator)
                .limit(25)
                .map(Crawler::typeToGitHubPullRequest)
                .collect(Collectors.toList());
        return result;
    }

    public static int timeComparator(GHPullRequest pr1, GHPullRequest pr2) {
        try {
            return -(pr1.getCreatedAt().compareTo(pr2.getCreatedAt()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static GitHubPullRequest typeToGitHubPullRequest(GHPullRequest pr) {
        try {
            return new GitHubPullRequest(pr.getNumber(), pr.getTitle(), pr.getUser().getLogin());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
