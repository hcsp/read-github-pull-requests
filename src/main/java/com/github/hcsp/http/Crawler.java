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
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException, IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String url = "https://github.com/" + repo + "/pulls";
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = httpclient.execute(httpGet);
        try {
            HttpEntity entity = response.getEntity();
            InputStream inputStream = entity.getContent();
            String s = IOUtils.toString(inputStream, "UTF-8");
            Document document = Jsoup.parse(s);
            ArrayList<Element> elements = document.select(".js-issue-row");
            List<GitHubPullRequest> list = new ArrayList<>();
            for (Element element:elements) {
                String title = element.select("a[data-hovercard-type=\"pull_request\"]").text();
                String author = element.select("a[data-hovercard-type=\"user\"]").text();
                int number = Integer.parseInt(element.select(".opened-by").text().split(" ")[0].split("#")[1]);
                list.add(new GitHubPullRequest(number, title, author));
            }
            return list;
        } finally {
            response.close();
        }
    }

}
