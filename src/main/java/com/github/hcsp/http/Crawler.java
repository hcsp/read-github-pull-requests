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
        int number;     //element.child(0).child(1).child(0).attr("id").substring(6,11)
        // Pull request的标题
        String title;   //element.child(0).child(1).child(0).text()
        // Pull request的作者的 GitHub 用户名
        String author;  //element.child(0).child(1).getElementsByClass("mt-1 text-small text-gray").get(0).child(0).child(1).text()

        GitHubPullRequest(int number, String title, String author) {
            this.number = number;
            this.title = title;
            this.author = author;
        }
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        List<GitHubPullRequest> list = new ArrayList<>();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://github.com/gradle/gradle/pulls");
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        try {
            System.out.println(response1.getStatusLine());
            HttpEntity entity1 = response1.getEntity();

            InputStream is = entity1.getContent();
            String s = IOUtils.toString(is, "utf-8");

            Document document = Jsoup.parse(s);
            Elements elements = document.select(".js-issue-row");
            for (Element element : elements) {
                int num = Integer.parseInt(element.child(0).child(1).child(0).attr("id").substring(6, 11));
                String title = element.child(0).child(1).child(0).text();
                String author = element.child(0).child(1).getElementsByClass("mt-1 text-small text-gray").get(0).child(0).child(1).text();

                list.add(new GitHubPullRequest(num, title, author));
            }
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity1);
        } finally {
            response1.close();
        }
        return list;
    }
}
