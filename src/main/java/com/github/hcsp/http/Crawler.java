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

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://github.com/" + repo + "/pulls");
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        List<GitHubPullRequest> res = new ArrayList<>();

        try {
            System.out.println(response1.getStatusLine());
            HttpEntity entity1 = response1.getEntity();
            InputStream stream = entity1.getContent();
            String html = IOUtils.toString(stream, "UTF-8");
            Document doc = Jsoup.parse(html);
            Elements elements = doc.select(".lh-condensed");

            for (Element ele : elements) {
                String title = ele.select(".js-navigation-open").text();
                String author = ele.select(".muted-link").text();
                int number = Integer.parseInt(ele.select(".text-small").text().split(" ")[0].substring(1));
//                System.out.println("title" + title);
//                System.out.println("author" + author);
//                System.out.println("number" + number);
                res.add(new GitHubPullRequest(number, title, author));
            }
                    EntityUtils.consume(entity1);
        } finally {
            response1.close();
        }
        return res;
    }
}
