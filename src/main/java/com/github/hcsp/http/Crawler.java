package com.github.hcsp.http;

import org.apache.http.HttpEntity;
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
import java.util.Arrays;
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
        HttpGet httpGet = new HttpGet("https://github.com/" + repo + "/pulls?page=1");
        CloseableHttpResponse response = httpclient.execute(httpGet);

        List<GitHubPullRequest> list = null;
        Document document;

        try {
            HttpEntity entity = response.getEntity();
            document = Jsoup.parse(EntityUtils.toString(entity));
        } finally {
            response.close();
        }
        if (document != null) {
            list = new ArrayList<>();
            Elements elements = document.select(".Box-row");

            for (Element element : elements) {
                String title = element.child(0).child(1).child(0).text();
                List<String> arr = Arrays.asList(element.child(0).child(1).child(3).child(0).text().split(" "));
                int number = Integer.parseInt(arr.get(0).replaceAll("#", ""));
                String author = arr.get(arr.size() - 1);
                list.add(new GitHubPullRequest(number, title, author));
            }
        }
        return list;
    }
}
