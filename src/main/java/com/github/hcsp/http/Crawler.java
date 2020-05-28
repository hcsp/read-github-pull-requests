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
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://github.com/" + repo + "/pulls");
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        List<GitHubPullRequest> result = new ArrayList<>();
        try {
            System.out.println(response1.getStatusLine());
            HttpEntity entity1 = response1.getEntity();
            InputStream content = entity1.getContent();

            String html = IOUtils.toString(content, StandardCharsets.UTF_8);

            Document parse = Jsoup.parse(html);
            Elements select = parse.select(".js-issue-row");
            for (Element element : select) {
                int number = Integer.parseInt(element.select(".mt-1.text-small.text-gray").get(0).child(0).text().split(" ")[0].substring(1));
                String author = element.select(".mt-1.text-small.text-gray").get(0).child(0).child(1).text();
                String title = element.child(0).child(1).child(0).text();
                GitHubPullRequest gitHubPullRequest = new GitHubPullRequest(number, title, author);
                result.add(gitHubPullRequest);
            }
        } finally {
            response1.close();
        }
        return result;
    }
}
