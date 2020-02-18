package com.github.hcsp.http;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.commons.io.IOUtils;
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
    }

    private static int GetNumber(String string) {
        int first = string.indexOf("#");
        int last = string.indexOf(" ");
        return Integer.parseInt(string.substring(first + 1, last));
    }

    private static String GetAuthor(String string) {
        int first = string.indexOf(" by ");
        return string.substring(first + 4);
    }


    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://github.com/" + repo + "/issues");
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        List<GitHubPullRequest> gpr = new ArrayList<>();
        try {
            System.out.println(response1.getStatusLine());
            HttpEntity entity1 = response1.getEntity();
            InputStream inputStream = entity1.getContent();
            String str = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            Document doc = Jsoup.parse(str);
            ArrayList<Element> issues = doc.select(".js-issue-row");
            for (Element issue : issues
            ) {
                String title = issue.child(0).child(1).text();
                String authorandid = issue.child(0).child(1).child(2).child(0).text();
                int number = GetNumber(authorandid);
                String author = GetAuthor(authorandid);
                GitHubPullRequest element = new GitHubPullRequest(number, title, author);
                gpr.add(element);
            }
        } finally {
            response1.close();
        }
        return gpr;
    }
}
