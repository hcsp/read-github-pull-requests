package com.github.hcsp.http;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


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
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://github.com/" + repo + "/issues");
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        List<GitHubPullRequest> gpr = new ArrayList<>();
        System.out.println(response1.getStatusLine());
        HttpEntity entity1 = response1.getEntity();
        InputStream inputStream = entity1.getContent();
        String str = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        Document doc = Jsoup.parse(str);
        ArrayList<Element> issues = doc.select(".js-issue-row");
        for (Element issue : issues
        ) {
            String title = issue.select(".js-navigation-open").get(0).text();
            int number = Integer.parseInt(issue.attr("id").substring(6));
            String author = issue.select(".muted-link").get(0).text();
            GitHubPullRequest element = new GitHubPullRequest(number, title, author);
            gpr.add(element);
        }
        response1.close();
        return gpr;
    }
}
