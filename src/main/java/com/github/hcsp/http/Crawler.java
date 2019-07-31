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

    public static void main(String[] args) throws IOException {
        getFirstPageOfPullRequests("gradle/gradle");
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        List<GitHubPullRequest> pullRequests = new ArrayList<>();
        final String RepoURL = "https://github.com/" + repo + "/pulls";
        String content = getHtmlContent(RepoURL);
        parasToDocumentAndAddToPullRequests(content, pullRequests);
        return pullRequests;
    }

    private static String getHtmlContent(String Uri) throws IOException {
        String content;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(Uri);
        CloseableHttpResponse response = httpclient.execute(httpGet);
        try {
            HttpEntity entity = response.getEntity();
            content = IOUtils.toString(entity.getContent(), "UTF-8");
        } finally {
            response.close();
        }
        return content;

    }

    private static void parasToDocumentAndAddToPullRequests(String content, List<GitHubPullRequest> pullRequests) {
        Document document = Jsoup.parse(content);
        for (Element doc : document.select(".js-issue-row")) {
            String title = " " + doc.selectFirst("a").text();

            // 格式: #10119 opened 2 hours ago by lacasseio
            Element openedByElement = doc.selectFirst("span.opened-by");
            String[] openedByInfo = openedByElement.text().split(" ");

            // 字符串#10119=> 数字10119
            int num = Integer.parseInt(openedByInfo[0].substring(1));
            String author = openedByInfo[openedByInfo.length - 1];

            pullRequests.add(new GitHubPullRequest(num, title, author));
        }
    }
}
