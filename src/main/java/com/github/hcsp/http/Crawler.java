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
        //使用HttpClient获取html
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://github.com/" + repo + "/pulls");
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        List<GitHubPullRequest> list = new ArrayList<>();
        try {
            HttpEntity entity1 = response1.getEntity();
            //获取Input stream
            InputStream is = entity1.getContent();
            String html = IOUtils.toString(is, "UTF-8");
            Document document = Jsoup.parse(html);
            ArrayList<Element> pr = document.select(".js-issue-row");

            for (Element element : pr) {
                String numberStr = element.attr("id");
                int number = Integer.parseInt(numberStr.substring(6));
                String title = element.child(0).child(1).child(0).text();
                String classTitle = element.child(0).child(1).child(2).attr("class");
                String author;
                if (classTitle.equals("labels lh-default d-block d-md-inline")) {
                    author = element.child(0).child(1).child(3).child(0).child(1).text();
                } else {
                    author = element.child(0).child(1).child(2).child(0).child(1).text();
                }
                list.add(new GitHubPullRequest(number, title, author));
            }
            //关闭实体
            EntityUtils.consume(entity1);
        } finally {
            response1.close();
        }
        return list;
    }

    public static void main(String[] args) throws IOException {
        getFirstPageOfPullRequests("gradle/gradle");
    }
}
