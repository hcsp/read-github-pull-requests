package com.github.hcsp.http;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
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
        List<GitHubPullRequest> pullRequest = new LinkedList<>();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://github.com/" + repo + "/pulls");
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        try {
            HttpEntity entity1 = response1.getEntity();
            InputStream content = entity1.getContent();
            String text = IOUtils.toString(content);
            Document document = Jsoup.parse(text);
            ArrayList<Element> issue = new Elements(document).select("div[class=\"float-left col-8 lh-condensed p-2\"]");
            for (Element element : issue) {
                String pullTitle = (element.select("a[data-hovercard-type=\"pull_request\"]").text());
                int pullNumber = Integer.parseInt(element.select("span[class=\"opened-by\"]").text().substring(1, 6));
                String pullName = element.select("span[class=\"opened-by\"]").select("a").text();
                pullRequest.add(new GitHubPullRequest(pullNumber, pullTitle, pullName));
            }
            EntityUtils.consume(entity1);
        } finally {
            response1.close();
        }
        return pullRequest;
    }
}


