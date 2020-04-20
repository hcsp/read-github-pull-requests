package com.github.hcsp.http;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.LinkedList;
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
        CloseableHttpClient httpClient = HttpClients.createDefault();

        //https://github.com/grdle/gradle/pulls
        String git = "https://github.com/";
        String url = git + repo + "/pulls";

        HttpGet get = new HttpGet(url);
        HttpEntity entity = httpClient.execute(get).getEntity();
        //伪装浏览器
        get.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.113 Safari/537.36");
        String html = EntityUtils.toString(entity);

        Document doc = Jsoup.parse(html);
        Elements issues = doc.getElementsByClass("Box-row Box-row--focus-gray p-0 mt-0 js-navigation-item js-issue-row");

        List<GitHubPullRequest> list = new LinkedList<>();
        for (Element issue : issues) {
            String block = issue.getElementsByClass("Box-row Box-row--focus-gray p-0 mt-0 js-navigation-item js-issue-row").attr("id");
            int number = Integer.parseInt(block.substring(block.indexOf("_") + 1, block.length()));

            String title = issue.getElementsByAttributeValue("data-hovercard-type", "pull_request").text();

            String author = issue.getElementsByAttributeValue("data-hovercard-type", "user").text();

            list.add(new GitHubPullRequest(number, title, author));
        }
        return list;
    }
}
