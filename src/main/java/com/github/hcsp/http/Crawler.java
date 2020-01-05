package com.github.hcsp.http;

import com.alibaba.fastjson.JSON;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Crawler {
    public static final String GITHUB_PULL_REQUEST_URL_HEAD = "https://api.github.com/repos/";
    public static final String GITHUB_PULL_REQUEST_URL_FOOT = "/pulls";

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
        //设置代理IP、端口、协议（请分别替换）
        //HttpHost proxy = new HttpHost("127.0.0.1", 1080, "http");
        //把代理设置到请求配置
        /*RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setProxy(proxy)
                .build();*/
        CloseableHttpClient httpclient = HttpClients.createDefault();
        //CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).build();
        String url = GITHUB_PULL_REQUEST_URL_HEAD + repo + GITHUB_PULL_REQUEST_URL_FOOT;
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = httpclient.execute(httpGet);
        List<GitHubPullRequest> gitHubPullRequestList = new ArrayList<>();
        try {
            HttpEntity entity = response.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            InputStream inputStream = entity.getContent();
            String html = IOUtils.toString(inputStream, "UTF-8");
            List<Map<String, Object>> pulls = (List) JSON.parse(html);
            for (Map<String, Object> pull :
                    pulls) {
                int number = (int) pull.get("number");
                String title = pull.get("title").toString();
                Map<String, Object> user = (Map<String, Object>) pull.get("user");
                String login = user.get("login").toString();
                GitHubPullRequest gitHubPullRequest = new GitHubPullRequest(number, title, login);
                gitHubPullRequestList.add(gitHubPullRequest);
            }
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }
        return gitHubPullRequestList;
    }

    public static void main(String[] args) throws IOException {
        getFirstPageOfPullRequests("gradle/gradle");
    }
}
