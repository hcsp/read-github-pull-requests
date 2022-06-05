package com.github.hcsp.http;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
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

        @Override
        public String toString() {
            return "GitHubPullRequest{" +
                    "number=" + number +
                    ", title='" + title + '\'' +
                    ", author='" + author + '\'' +
                    '}';
        }
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        //设置CookieSpecs.STANDARD的cookie解析模式，下面为源码，对应解析格式我给出了备注
        CloseableHttpClient httpClient= HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setCookieSpec(CookieSpecs.STANDARD).build())
                .build();

        HttpGet httpGet = new HttpGet("https://github.com/"+repo+"/pulls");
        CloseableHttpResponse response = httpClient.execute(httpGet);
        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity1 = response.getEntity();
            // do something useful wi th the response body
            // and ensure it is fully consumed
            InputStream is=entity1.getContent();
            String s = IOUtils.toString(is, "UTF-8");

            Document document = Jsoup.parse(s);
            Elements select = document.select(".js-issue-row");
            List<GitHubPullRequest> gitHubPullRequestList=new ArrayList<>();
            for (Element e:select
            ) {

                String text = e.child(0).child(1).child(0).text();
                String id = e.child(0).child(1).child(0).attr("id").split("_")[1];
                String name = e.getElementsByClass("Link--muted").text();


                gitHubPullRequestList.add(new GitHubPullRequest(Integer.parseInt(id),text,name));

            }

            return gitHubPullRequestList;
        } finally {
            response.close();
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println(getFirstPageOfPullRequests("hcsp/read-github-pull-requests"));
    }
}
