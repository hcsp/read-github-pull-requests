package com.github.hcsp.http;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
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
        List<GitHubPullRequest> pullRequests = new ArrayList<>();
        Document doc = Jsoup.connect("https://github.com/" + repo + "/pulls").get();
        Elements newsHeadlines = doc.select(".flex-auto.min-width-0.lh-condensed.p-2.pr-3.pr-md-2");
        for (Element headline : newsHeadlines) {
            Node node = headline.childNodes().get(7).childNodes().get(1).childNodes().get(0);
            String pullRequestNumber = ((TextNode) node).toString().trim().replaceAll("[^0-9]", "");
            String title = headline.childNodes().get(1).childNodes().get(0).toString();
            StringBuffer author = new StringBuffer(headline.childNodes().get(7).childNodes().get(2).parentNode().childNodes().get(1).childNodes().get(3).childNodes().get(0).toString());
            pullRequests.add(new GitHubPullRequest(new Integer(pullRequestNumber), title, author.toString().trim()));
        }
        return pullRequests;
    }

    public static void main(String[] args) throws IOException {
        Crawler.getFirstPageOfPullRequests("test");
    }
}
