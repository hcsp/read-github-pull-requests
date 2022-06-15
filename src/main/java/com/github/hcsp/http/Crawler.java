package com.github.hcsp.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://github.com/" + repo + "/pulls");
        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        String html = EntityUtils.toString(entity);
        Document doc = Jsoup.parse(html);
        Elements linksElements = doc.select("div.js-navigation-item");
        List<GitHubPullRequest> result = new ArrayList<>();
        for (Element ele : linksElements) {
            int number = 0;
            String id = ele.selectFirst(".opened-by").text().split(" ")[0];
            if (id != null) {
                number = Integer.parseInt(id.substring(1));
            }
            String title = ele.selectFirst(".markdown-title").text();
            String author = ele.selectFirst(".Link--muted").text();
            result.add(new GitHubPullRequest(number, title, author));
        }
        return result;
    }
}
