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

    public static void main(String[] args) throws IOException {
        getFirstPageOfPullRequests("gradle/gradle");
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://github.com/gradle/gradle/pulls");
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        try {
            //System.out.println(response1.getStatusLine());

            HttpEntity entity1 = response1.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed

            InputStream is = entity1.getContent();
            //System.out.println(IOUtils.toString(is,"UTF-8"));

            String html = IOUtils.toString(is, "UTF-8");
            Document doucument = Jsoup.parse(html);
            //System.out.println(doucument);

            ArrayList<Element> issues = doucument.select(".js-issue-row");

            ArrayList<GitHubPullRequest> list = new ArrayList<GitHubPullRequest>();

            for (Element element : issues) {
                String UserNameGroup;
                System.out.println(element.attr("id"));
                System.out.println(Integer.valueOf(element.attr("id").substring(6)));

                System.out.println(element.child(0).child(1).child(0).text());
                System.out.println(element.child(0).child(1).child(0).attr("href"));
                if (element.child(0).child(1).child(2).attr("class") == "labels lh-default") {
                    UserNameGroup=element.child(0).child(1).child(3).child(0).child(1).text();
                    System.out.println(element.child(0).child(1).child(3).child(0).child(1).text());
                    System.out.println(element.child(0).child(1).child(3).child(0).child(1).attr("href"));
                } else {
                    UserNameGroup=element.child(0).child(1).child(2).child(0).child(1).text();
                    System.out.println(element.child(0).child(1).child(2).child(0).child(1).text());
                    System.out.println(element.child(0).child(1).child(2).child(0).child(1).attr("href"));
                }
                // System.out.println(element);

                GitHubPullRequest a = new GitHubPullRequest(Integer.valueOf(element.attr("id").substring(6)),element.child(0).child(1).child(0).text(), UserNameGroup);

                list.add(a);

            }
            return list;
        } finally {
            response1.close();
        }

    }
}
