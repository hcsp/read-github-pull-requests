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
        HttpGet httpGet = new HttpGet("https://github.com/" + repo + "/pulls");
        CloseableHttpResponse response = httpclient.execute(httpGet);
        ArrayList<GitHubPullRequest> list = new ArrayList<>();
        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity1 = response.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            InputStream is = entity1.getContent();
            String html = IOUtils.toString(is, StandardCharsets.UTF_8);
            Document doc = Jsoup.parse(html);
            ArrayList<Element> elements = doc.select(".js-issue-row");
            for (Element element : elements) {
                int number = Integer.parseInt(element.child(0).child(1).child(0).attr("href").substring(20));
                String title = element.child(0).child(1).child(0).text();
                String author = element.select("[data-hovercard-type=user]").text();
                GitHubPullRequest gitHubPullRequest = new GitHubPullRequest(number, title, author);
                list.add(gitHubPullRequest);
            }
        } finally {
            response.close();
        }
        return list;
    }
}
