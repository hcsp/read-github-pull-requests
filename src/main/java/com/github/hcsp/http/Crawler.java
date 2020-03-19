package com.github.hcsp.http;

import org.apache.commons.io.IOUtils;
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
        List<GitHubPullRequest> pullRequests = new ArrayList<>();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://github.com/" + repo + "/pulls");
        CloseableHttpResponse response1 = null;
        try {
            response1 = httpclient.execute(httpGet);
            InputStream content = response1.getEntity().getContent();
            String body = IOUtils.toString(content, "UTF-8");

            Document doc = Jsoup.parse(body);
            Elements allElements = doc.select(".Box-row");
            for (Element element : allElements) {
                String title = element.child(0).child(1).child(0).childNodes().get(0).attributes().get("#text");
                String num = element.child(0).child(1).select(".opened-by").get(0).ownText();
                int number = Integer.parseInt(num.substring(1, 6));
                String auther = element.select("[data-hovercard-type=user]").text();
                GitHubPullRequest gpr = new GitHubPullRequest(number, title, auther);
                pullRequests.add(gpr);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pullRequests;
    }

}
