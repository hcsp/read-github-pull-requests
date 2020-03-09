package com.github.hcsp.http;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
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
        //创建请求客户端
        CloseableHttpClient httpclient = HttpClients.createDefault();
        //创建get一个请求体
        HttpGet httpGet = new HttpGet("https://github.com/" + repo + "/pulls");
        //通过客户端发送get请求
        CloseableHttpResponse response = httpclient.execute(httpGet);
        List<GitHubPullRequest> gitHubPullRequests = new ArrayList<>();
        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            String html = IOUtils.toString(is, StandardCharsets.UTF_8);
            Document parseDoc = Jsoup.parse(html);
            ArrayList<Element> elements = parseDoc.select(".js-issue-row");
            int i=0;
            for (Element element : elements) {
                // title yes
                String title = element.child(0).child(1).child(0).text();
                // link num
                String href = element.child(0).child(1).child(0).attr("href");
                href = href.substring(href.lastIndexOf("/") + 1);
                int num = Integer.valueOf(href);
                // autor
                String author = element.child(0).child(1).child(3).child(0).select(".muted-link").attr("title").replace("Open pull requests created by ", "");
                gitHubPullRequests.add(new GitHubPullRequest(num, title, author));
                break;
            }
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }
        return gitHubPullRequests;
    }

}
