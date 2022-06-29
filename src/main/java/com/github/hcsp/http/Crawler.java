package com.github.hcsp.http;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    
    static String HOST = "https://github.com/";

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        List<GitHubPullRequest> result = new ArrayList<>();
        try (final CloseableHttpClient httpclient = HttpClients.createDefault()) {
            final HttpGet httpGet = new HttpGet(HOST + repo + "/pulls");
            try (final CloseableHttpResponse response = httpclient.execute(httpGet)) {
                System.out.println(response.getCode() + " " + response.getReasonPhrase());
                final HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent();
                String html = CharStreams.toString(new InputStreamReader(is, Charsets.UTF_8));
//                System.out.println("html = " + html);
                Document doc = Jsoup.parse(html);
                Elements issues = doc.select(".js-issue-row");
//                System.out.println("issues = " + issues);
                for (Element issue : issues) {
                    int number = Integer.parseInt(issue.select(".opened-by").text().substring(1, 6));
                    String title = issue.select(".markdown-title").text();
                    String author = issue.select(".opened-by .Link--muted").text();
                    result.add(new GitHubPullRequest(number, title, author));
                }
                EntityUtils.consume(entity);
            }
        }
        return result;
    }
}
