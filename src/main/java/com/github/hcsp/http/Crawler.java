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
        List<GitHubPullRequest> result = new ArrayList<>();

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://github.com/" + repo + "/issues");
        CloseableHttpResponse response1 = httpclient.execute(httpGet);

        try {
            HttpEntity entity1 = response1.getEntity();
            InputStream is = entity1.getContent();
            String html = IOUtils.toString(is, StandardCharsets.UTF_8);
            Document document = Jsoup.parse(html);
            ArrayList<Element> issues = document.select(".js-issue-row");
            for (Element issue : issues) {
                String number = issue.attr("id").replaceAll("\\D", "");
                String title = issue.select("[data-hovercard-type=issue]").text();
                String author = issue.select("[data-hovercard-type=user]").text();
                result.add(new GitHubPullRequest(Integer.parseInt(number), title, author));
            }
        } finally {
            response1.close();
        }
        return result;
    }
}
