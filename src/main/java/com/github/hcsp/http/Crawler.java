package com.github.hcsp.http;

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

        List<GitHubPullRequest> dataOfPR = new ArrayList<GitHubPullRequest>(20);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://github.com/" + repo + "/pulls");
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        HttpEntity entity1 = response1.getEntity();
        InputStream is = entity1.getContent();
        Document doc = Jsoup.parse(is, "UTF-8", "https://github.com/\"+repo+\"/pulls");
        Elements issues = doc.select(".js-issue-row");
        for (Element element : issues) {
            int number = Integer.parseInt(element.attr("id").substring(6));
            String title = element.select(".js-navigation-open").get(0).text();
            String author = element.select(".muted-link").get(0).text();
            dataOfPR.add(new GitHubPullRequest(number, title, author));
        }
        response1.close();
        return dataOfPR;
    }

    public static void main(String[] args) throws IOException {
        getFirstPageOfPullRequests("hexojs/hexo");
    }
}
