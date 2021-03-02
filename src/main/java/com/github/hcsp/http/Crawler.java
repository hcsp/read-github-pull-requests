package com.github.hcsp.http;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
        List<GitHubPullRequest> res = new ArrayList<>();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://github.com/" + repo + "/pulls");
        CloseableHttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        InputStream is = entity.getContent();
        String html = IOUtils.toString(is, StandardCharsets.UTF_8);
        Document document = Jsoup.parse(html);
        Elements pullRequests = document.select(".js-issue-row");
        for (Element element : pullRequests) {
            int number = Integer.parseInt(element.select(".opened-by").text().substring(1, 6));
            String title = element.select(".js-navigation-open").text();
            String author = element.child(0).child(1).select(".mt-1").select(".opened-by").select(".Link--muted").text();
            GitHubPullRequest gitHubPullRequest = new GitHubPullRequest(number, title, author);
            System.out.println(number + " - " + title + " : " + author);
            res.add(gitHubPullRequest);
        }
        return res;
    }

    public static void main(String[] args) throws IOException {
        System.out.println(getFirstPageOfPullRequests("gradle/gradle").size());
    }
}
