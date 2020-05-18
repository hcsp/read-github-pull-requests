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
        List<GitHubPullRequest> list = new ArrayList<>();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://github.com/" + repo + "/pulls");
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        try {
            System.out.println(response1.getStatusLine());
            HttpEntity entity1 = response1.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed'
            InputStream is = entity1.getContent();
            String html = IOUtils.toString(is, "UTF-8");
            Document document = Jsoup.parse(html);
            ArrayList<Element> issue = document.select(".js-issue-row");
            for (Element element : issue) {
                String allStr = element.child(0).child(1).child(3).child(0).text();
                String title = element.child(0).child(1).child(0).text();
                int num = Integer.parseInt(allStr.split(" ")[0].substring(1));
                String author = allStr.split(" ")[allStr.split(" ").length - 1];
                GitHubPullRequest pullRequest = new GitHubPullRequest(num, title, author);
                list.add(pullRequest);
            }
            EntityUtils.consume(entity1);
        } finally {
            response1.close();
        }
        return list;
    }

    public static void main(String[] args) throws IOException {
        try {
            getFirstPageOfPullRequests("gradle/gradle");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
