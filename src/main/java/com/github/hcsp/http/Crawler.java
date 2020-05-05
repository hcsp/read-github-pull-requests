package com.github.hcsp.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

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
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        List<GitHubPullRequest> gitHubPullRequests = new ArrayList<>();
        try {
            HttpEntity entity1 = response1.getEntity();
            InputStream inputStream = entity1.getContent();
            String isString = IOUtils.toString(inputStream, "UTF-8");
            Document doc = Jsoup.parse(isString);
            ArrayList<Element> elements = doc.select(".Box-row--focus-gray");
            for (Element element : elements) {
                int number = Integer.parseInt(element.id().split("_")[1]);
                String title = element.child(0).child(1).child(0).text();
                String author = element.child(0).select(".mt-1").select(".muted-link").text().split(" ")[0];
                GitHubPullRequest gitHubPullRequest = new GitHubPullRequest(number, title, author);
                gitHubPullRequests.add(gitHubPullRequest);
            }
        } finally {
            response1.close();
        }
        return gitHubPullRequests;
    }
}
