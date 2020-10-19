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
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) {
        // 第一种方法 爬到页面
        List plList = new ArrayList<GitHubPullRequest>();
        String requestUrl = "https://github.com/" + repo + "/pulls?page=1";
        Document doc = null;
        try {
            doc = Jsoup.connect(requestUrl).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements plElement = doc.select(".js-navigation-container .Box-row--drag-hide");
        for (Element el: plElement) {
            String author = el.selectFirst("a.muted-link").text();
            String title = el.selectFirst("a.link-gray-dark.v-align-middle").text();
            String[] parseLinkAttr = el.selectFirst("a.link-gray-dark.v-align-middle").attr("href").split("/");
            int number = Integer.parseInt(parseLinkAttr[parseLinkAttr.length - 1]);
            plList.add(new GitHubPullRequest(number, title, author));
        }
        return plList;

    }

    public static void main(String[] args) {
        getFirstPageOfPullRequests("torvalds/linux");
    }
}
