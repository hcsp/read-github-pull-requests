package com.github.hcsp.http;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
        ArrayList<GitHubPullRequest> gitHubPullRequestArrayList = new ArrayList<>();
        Document doc = Jsoup.connect("https://github.com/" + repo + "/pulls").get();
        Elements issues = doc.select("div.Box-row");
        for (Element issue : issues) {
            gitHubPullRequestArrayList.add(issueElementToGitHubPullRequest(issue));
        }
        return gitHubPullRequestArrayList;
    }

    // 解析issue
    public static GitHubPullRequest issueElementToGitHubPullRequest(Element issueElement) {
        Element idAndAuthorElement = issueElement.selectFirst("span.opened-by");
        String title = issueElement.select("[data-hovercard-type=\"pull_request\"]").text();
        String author = idAndAuthorElement.selectFirst("span.opened-by > a").text();
        String idString = issueElement.attributes().get("id");
        String[] splitArr = idString.split("_");
        String idStr = splitArr[1];
        return new GitHubPullRequest(Integer.parseInt(idStr), title, author);
    }
}
