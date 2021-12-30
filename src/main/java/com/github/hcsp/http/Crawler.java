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
        List<GitHubPullRequest> list = new ArrayList<>();
        Document doc = Jsoup.connect("https://github.com/" + repo + "/pulls").userAgent("Mozilla/5.0").timeout(10 * 1000).get();
        Elements issues = doc.select(".js-issue-row");
        for (Element issue : issues) {
            int number = Integer.parseInt(issue.select(".opened-by").text().substring(1, 6));
            String title = issue.select(".markdown-title").text();
            String author = issue.select(".opened-by .Link--muted").text();
            list.add(new GitHubPullRequest(number, title, author));
        }
        return list;
    }
}
