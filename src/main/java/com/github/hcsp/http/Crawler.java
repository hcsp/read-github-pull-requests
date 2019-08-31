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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


/**
 * This program is Simulate a Browser
 *
 * @author Mr_YU
 * @version 1.0.0 2019-7-30
 */
public class Crawler {
    static class GitHubPullRequest {
        // Pull request的编号
        int number;
        // Pull request的标题
        String title;
        // Pull request的作者的GitHub id
        String author;

        GitHubPullRequest(int number, String title, String author) {
            this.number = number;
            this.title = title;
            this.author = author;
        }
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static ArrayList<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        ArrayList<GitHubPullRequest> gitHubPullRequestIfo = new ArrayList<>();
        CloseableHttpClient aDefaultHttpClient = HttpClients.createDefault();
        HttpGet getAUrl = new HttpGet("https://github.com/" + repo + "/pulls");
        CloseableHttpResponse getResponseFromUrl = aDefaultHttpClient.execute(getAUrl);
        HttpEntity aHttpEntityFromUrl = getResponseFromUrl.getEntity();
        if (aHttpEntityFromUrl != null) {
            try {
                gitHubPullRequestIfo = getUsefulMessage(aHttpEntityFromUrl);
            } finally {
                getResponseFromUrl.close();
            }
        }
        return gitHubPullRequestIfo;
    }

    private static ArrayList<GitHubPullRequest> getUsefulMessage(HttpEntity aHttpEntityFromUrl) throws IOException {
        ArrayList<GitHubPullRequest> arr = new ArrayList<>();
        InputStream inputStream = aHttpEntityFromUrl.getContent();
        String html = IOUtils.toString(inputStream, "utf-8");
        Document document = Jsoup.parse(html);
        ArrayList<Element> elementArrayList = document.select(".js-issue-row");
        for (Element element : elementArrayList
        ) {
            String title = element.selectFirst("a").text();
            int number = Integer.parseInt(element.selectFirst("a").attr("id").replaceAll("[^\\d]", ""));
            String[] aStrFromSplitElement = element.selectFirst("span.opened-by").text().split(" ");
            String author = (aStrFromSplitElement[aStrFromSplitElement.length - 1]);
            GitHubPullRequest aTempGitHubPullRequest = new GitHubPullRequest(number, title, author);
            arr.add(aTempGitHubPullRequest);
        }
        return arr;
    }
}
