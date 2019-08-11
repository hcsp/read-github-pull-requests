package com.github.hcsp.http;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Crawler {
    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        Document doc = Jsoup.connect("https://github.com/" + repo + "/pulls").get();
        Element issues = doc.getElementsByAttributeValue("aria-label", "Issues").get(1).child(0);

        List<GitHubPullRequest> gitHubPullRequestList = new ArrayList<>();

        for (Element issue : issues.children()) {
            int number = Integer.parseInt(issue.id().replace("issue_", ""));
            String title = issue.child(0).child(1).child(0).text();
            String author = issue.child(0).child(1).getElementsByClass("text-small").get(0)
                    .getElementsByClass("opened-by")
                    .get(0).getElementsByTag("a").get(0).text();
            GitHubPullRequest gitHubPullRequest = new GitHubPullRequest(number, title, author);
            gitHubPullRequestList.add(gitHubPullRequest);
        }

        return gitHubPullRequestList;
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
}
