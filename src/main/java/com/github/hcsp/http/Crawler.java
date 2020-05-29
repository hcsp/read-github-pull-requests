package com.github.hcsp.http;

import org.jsoup.Connection;
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
        String url = "https://github.com/" + repo + "/pulls";
        Connection conn = Jsoup.connect(url); // 建立与url中页面的连接
        Document doc = conn.get(); // 解析页面
        Elements links = doc.select("div[class=Box-row Box-row--focus-gray p-0 mt-0 js-navigation-item js-issue-row]");
        for (Element link : links) {
            String textSpan = link.select("span[class=opened-by]").text();
            String title = link.select("a[class=link-gray-dark v-align-middle no-underline h4 js-navigation-open]").text();
            String numberString = textSpan.substring(1, 6);
            int number = Integer.parseInt(numberString);
            String author = textSpan.substring(30, textSpan.length());
            list.add(new GitHubPullRequest(number, title, author));
        }
        return list;
    }
}
