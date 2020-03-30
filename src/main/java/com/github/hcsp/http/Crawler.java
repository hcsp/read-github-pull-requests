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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

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
        CloseableHttpClient aDefault = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("http://github.com/" + repo + "/pulls");
        CloseableHttpResponse response = aDefault.execute(httpGet);
        HttpEntity entity = response.getEntity();
        String html = EntityUtils.toString(entity, StandardCharsets.UTF_8);
        Document document = Jsoup.parse(html);
        Elements elementsByClass = document.getElementsByClass("js-issue-row");
        for (Element element : elementsByClass) {
            int number = parseInt(element.attr("id").split("_")[1]);
            String title = element.getElementById(element.attr("id") + "_link").text();
            String author = element.getElementsByClass("opened-by").get(0).getElementsByTag("a").get(0).text();
            list.add(new GitHubPullRequest(number, title, author));
        }

        return list;

    }


    public static void main(String[] args) throws IOException {
        getFirstPageOfPullRequests("/gradle/gradle");
    }


}
