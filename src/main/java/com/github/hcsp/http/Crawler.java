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
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Crawler {

    static class GitHubPullRequest {

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

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

        try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
            HttpEntity httpEntity = response.getEntity();
            InputStream inputStream = httpEntity.getContent();
            String result = IOUtils.toString(inputStream, "UTF-8");

            Document doc = Jsoup.parse(result);
            Elements issues = doc.select(".js-issue-row");
            for (Element issueItem : issues) {
                String title = issueItem.select(".js-navigation-open").get(0).text();
                String[] strings = issueItem.select(".opened-by").get(0).text().split(" ");
                String name = strings[strings.length - 1];
                int id = Integer.parseInt(strings[0].substring(1));
                list.add(new GitHubPullRequest(id, title, name));

            }
            EntityUtils.consume(httpEntity);
        }
        return list;
    }
}


