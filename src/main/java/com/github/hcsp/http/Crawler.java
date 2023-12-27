package com.github.hcsp.http;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class Crawler {

    public static void main(String[] args) {
        try {
            getRepositoryHTML("hcsp/read-github-pull-requests");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) {
        try {
            return parsePullRequestFromHTML(getRepositoryHTML(repo));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getRepositoryHTML(String repo) throws Exception {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            ClassicHttpRequest httpGet = ClassicRequestBuilder.get("https://github.com/" + repo + "/pulls").build();
            System.out.println("Loading document...");
            ClassicHttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String html = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
            response.close();
            parsePullRequestFromHTML(html);
            return html;
        }
    }

    public static List<GitHubPullRequest> parsePullRequestFromHTML(String html) {
        Document doc = Jsoup.parse(html);
        Elements pullRequestElements = doc.select("div[id^=issue]");
        List<GitHubPullRequest> result = new ArrayList<>();
        for (Element el : pullRequestElements) {
            String openedBy = el.select(".opened-by").text();
            String[] openedByParts = openedBy.split(" ");
            String author = openedByParts[openedByParts.length - 1];
            String title = el.select("a[id^=issue]").text();
            int number = Integer.parseInt(openedByParts[0].substring(1));
            GitHubPullRequest p = new GitHubPullRequest(number, title, author);
            result.add(p);
        }
        return result;
    }
}
