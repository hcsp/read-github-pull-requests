package com.github.hcsp.http;

import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.service.PullRequestService;

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
        String owner = repo.split("/")[0];
        String name = repo.split("/")[1];
        PullRequestService service = new PullRequestService();
        RepositoryId repositoryId = new RepositoryId(owner, name);
        List<PullRequest> pullRequestsList = service.getPullRequests(repositoryId, "");
        List<GitHubPullRequest> result = new ArrayList<>();
        for (PullRequest pullRequest : pullRequestsList) {
            GitHubPullRequest pr = new GitHubPullRequest(pullRequest.getNumber(), pullRequest.getTitle(), pullRequest.getUser().getLogin());
            result.add(pr);
        }
        return result;
    }
}
