package com.github.hcsp.http;

import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.service.PullRequestService;
import org.eclipse.egit.github.core.service.RepositoryService;

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
    public static void main(String[] args) throws IOException {
        getFirstPageOfPullRequests("gradle/gradle");
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        String[] url = repo.split("/");
        RepositoryService service = new RepositoryService();
        PullRequestService pullRequestService = new PullRequestService();
        List<GitHubPullRequest> list = new ArrayList<>();
        List<PullRequest> pullRequests = pullRequestService.getPullRequests(service.getRepository(url[0], url[1]), "ALL");
        PullRequest pr = null;
        int i = 0;
        while (i < 10) {
            pr = pullRequests.get(i);
            list.add(new GitHubPullRequest(pr.getNumber(), pr.getTitle(), pr.getBase().getUser().getLogin()));
            i++;
        }
        return list;
    }
}
