package com.github.hcsp.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    public static class GitHubPullRequest {
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
//            return parsePullRequestFromHTML(getRepositoryPullRequestHTML(repo));
            return parsePullRequestFromJSON(getRepositoryPullRequestJSON(repo));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getRepositoryPullRequestHTML(String repo) throws Exception {
        return httpGet("https://github.com/" + repo + "/pulls");
    }

    public static String getRepositoryPullRequestJSON(String repo) throws Exception {
        return httpGet("https://api.github.com/repos/" + repo + "/pulls");
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

    public static List<GitHubPullRequest> parsePullRequestFromJSON(String json) {
        Gson gson = new Gson();
        TypeToken<List<GithubPullsItem>> type = new TypeToken<List<GithubPullsItem>>() {
        };
        List<GithubPullsItem> pulls = gson.fromJson(json, type);
        List<GitHubPullRequest> result = new ArrayList<>();
        for (GithubPullsItem pull : pulls) {
            result.add(new GitHubPullRequest(pull.getNumber(), pull.getTitle(), pull.getUser().getLogin()));
        }
        return result;
    }

    private static String httpGet(String url) throws Exception {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            ClassicHttpRequest httpGet = ClassicRequestBuilder.get(url).build();
            System.out.println("Loading resource...");
            ClassicHttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String resource = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
            response.close();
            return resource;
        }
    }
}
