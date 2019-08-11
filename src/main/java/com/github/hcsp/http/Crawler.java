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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

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
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        ArrayList<GitHubPullRequest> result = new ArrayList<>();
        String url = "https://github.com/" + repo + "/pulls";
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        InputStream inputStream = entity.getContent();

        String html = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        Document document = Jsoup.parse(html);

        ArrayList<Element> elements = document.select(".js-issue-row");

        for (Element element : elements) {
            String title = element.selectFirst("a").text();
            int number = Integer.parseInt(element.selectFirst("a").attr("id").replaceAll("[^\\d]", ""));
            String[] openBy = element.selectFirst("span.opened-by").text().split(" ");
            String author = (openBy[openBy.length - 1]);
            GitHubPullRequest aTempGitHubPullRequest = new GitHubPullRequest(number, title, author);
            result.add(aTempGitHubPullRequest);
        }
        return result;
    }
}
