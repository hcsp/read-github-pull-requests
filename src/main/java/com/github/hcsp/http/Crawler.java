package com.github.hcsp.http;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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

        @Override
        public String toString() {
            return "GitHubPullRequest{" +
                    "number=" + number +
                    ", title='" + title + '\'' +
                    ", author='" + author + '\'' +
                    '}';
        }
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        List<GitHubPullRequest> list = new ArrayList<>(30);

        Document doc = getHtmlDocument(repo);

        Elements issueRows = doc.select(".js-issue-row");
        for (Element issueRow : issueRows) {
            int seq = Integer.parseInt(issueRow.id().replaceAll("\\D", ""));

            Elements issueRowChildren = issueRow.child(0).children();
            String title = issueRowChildren.select("[id^=issue]").first().text();

            String author = issueRowChildren.select(".muted-link").first().text();

            GitHubPullRequest gitHubPullRequest = new GitHubPullRequest(seq, title, author);
            list.add(gitHubPullRequest);
        }
        return list;
    }

    private static Document getHtmlDocument(String url) throws IOException {
        HttpClient hc = HttpClients.createDefault();
        HttpGet get = new HttpGet("https://github.com/" + url + "/pulls");
        HttpResponse response = hc.execute(get);
        InputStream inputStream = response.getEntity().getContent();
        String content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        return Jsoup.parse(content);
    }

    public static void main(String[] args) throws IOException {
        getFirstPageOfPullRequests("gradle/gradle");
    }
}
