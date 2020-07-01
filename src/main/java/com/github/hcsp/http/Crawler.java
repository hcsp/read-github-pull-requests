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
        String url = "https://github.com/" + repo + "/pulls";
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = httpclient.execute(httpGet);
        List<GitHubPullRequest> result = new ArrayList<>();

        try {
            HttpEntity entity = response.getEntity();
            String html = IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8);
            Document doc = Jsoup.parse(html);
            Elements elements = doc.select(".pr-md-2");
            for (Element e : elements) {
                String obtainPreNum = e.child(0).attr("href");
                int newNum = Integer.parseInt(obtainPreNum.replace("/" + repo + "/pull/", ""));
                String author = e.getElementsByClass("opened-by").get(0).child(1).text();
                //存在结构不一致的代码，author为e.child(3).child(0).child(1).text()会报错
                GitHubPullRequest gitHubPullRequest = new GitHubPullRequest(newNum, e.child(0).text(), author);
                result.add(gitHubPullRequest);
            }
        } finally {
            response.close();
        }
        return result;
    }
}
