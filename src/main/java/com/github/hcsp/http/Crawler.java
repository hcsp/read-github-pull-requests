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
        // Pull request的作者的 GitHub 用户名
        String author;

        GitHubPullRequest(int number, String title, String author) {
            this.number = number;
            this.title = title;
            this.author = author;
        }
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://github.com/" + repo + "/pulls");

        List<GitHubPullRequest> res = new ArrayList<>();

        try (CloseableHttpResponse response1 = httpclient.execute(httpGet)) {
            System.out.println(response1.getStatusLine());
            HttpEntity entity1 = response1.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            InputStream is = entity1.getContent();
            String html = IOUtils.toString(is, "UTF-8");
            Document doc = Jsoup.parse(html);
            Elements selectresults = doc.select(".js-issue-row");
            Elements authorresults = doc.select("[title*=created]");

            for (int i = 0; i < selectresults.size(); i++) {
                Element element = selectresults.get(i);
                Element authorelement = authorresults.get(i);
                String title = element.child(0).child(1).child(0).text();
                String author = authorelement.text();
                int number = Integer.parseInt(element.attr("id").split("_")[1]);
                res.add(new GitHubPullRequest(number, title, author));
            }
            EntityUtils.consume(entity1);
        }
        return res;
    }

    public static void main(String[] args) throws IOException {
        String repo = "gradle/gradle";
        Crawler.GitHubPullRequest firstPull = getFirstPageOfPullRequests(repo).get(0);
        System.out.println(firstPull.toString());
    }
}

