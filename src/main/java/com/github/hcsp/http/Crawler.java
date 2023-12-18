package com.github.hcsp.http;

import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
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
        List<GitHubPullRequest> ans = new ArrayList<>();
        String link = "https://github.com/".concat(repo).concat("/pulls");
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            ClassicHttpRequest httpGet = ClassicRequestBuilder.get(link)
                    .build();
            httpclient.execute(httpGet, response -> {
                final HttpEntity entity1 = response.getEntity();
                InputStream is = entity1.getContent();
                String str = IOUtils.toString(is, "utf-8");
                Document doc = Jsoup.parse(str);
                ArrayList<Element> elements = doc.select(".js-issue-row");
                for (Element elm : elements) {
                    String title = elm.select(".markdown-title").text();
                    String s = elm.select(".opened-by").text();
                    String strNumber = s.substring(s.indexOf("#") + 1, s.indexOf(" "));
                    int number = Integer.parseInt(strNumber);
                    String author = s.substring(s.indexOf("by") + 3);
                    ans.add(new GitHubPullRequest(number, title, author));
                }
                return null;
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ans;
    }
}
