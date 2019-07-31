package com.github.hcsp.http;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
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
        // Pull request的作者的GitHub id
        String author;

        GitHubPullRequest(int number, String title, String author) {
            this.number = number;
            this.title = title;
            this.author = author;
        }
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        String url = "https://github.com/" + repo + "/pulls";
        String html = Crawler.getUrlContent(url, "UTF-8");
        Document doc = Jsoup.parse(html);
        Elements issueRow = doc.select(".js-navigation-container .js-navigation-item.js-issue-row");
        List<GitHubPullRequest> result = new ArrayList<>();
        for (Element row : issueRow) {
            int number = Integer.parseInt(row.select(".js-navigation-open").attr("id").replace("issue_", "").replace("_link", ""));
            String title = row.select(".js-navigation-open").text();
            String author = row.select(".opened-by .muted-link").text();
            result.add(new GitHubPullRequest(number, title, author));
        }
        return result;
    }

    private static String getUrlContent(String url, String charset) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        return EntityUtils.toString(response1.getEntity(), charset);
    }

    public static void main(String[] args) throws IOException {
        getFirstPageOfPullRequests("gradle/gradle");
    }
}
