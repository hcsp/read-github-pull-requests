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
        ArrayList<GitHubPullRequest> result = new ArrayList<>();
        Document document = Jsoup.connect("https://github.com/" + repo + "/pulls").get();
        Elements pullsElements = document.select(".js-issue-row");
        for (Element pull : pullsElements
        ) {
            Element title = (Element) pull.childNode(1).childNode(5).childNode(1);
            Element authorInfo = (Element) pull.childNode(1).childNode(5).childNode(7).childNode(1);
            String number = authorInfo.select(".opened-by").first().text().split(" opened")[0].substring(1);
            String author = authorInfo.select(".muted-link").text();
            result.add(new GitHubPullRequest(Integer.parseInt(number), title.text(), author));
        }
        return result;
    }


}
