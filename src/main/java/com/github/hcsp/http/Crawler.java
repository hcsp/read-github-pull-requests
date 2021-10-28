package com.github.hcsp.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
        List<GitHubPullRequest> result = new ArrayList<>();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        RequestConfig build = RequestConfig.custom().setConnectTimeout(10000).setConnectionRequestTimeout(10000).build();
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls");
        System.out.println("https://api.github.com/repos/" + repo + "/pulls");
        httpGet.setConfig(build);
        try (CloseableHttpResponse response1 = httpclient.execute(httpGet)) {
            System.out.println(response1.getStatusLine());
            HttpEntity entity1 = response1.getEntity();
            System.out.println(entity1);
            InputStream content = entity1.getContent();
            String s = IOUtils.toString(content, StandardCharsets.UTF_8);
            JSONArray objects;
            objects = JSON.parseArray(s);
            for (int i = 0; i < objects.size(); i++) {
                JSONObject jsonObject = objects.getJSONObject(i);
                Integer id = jsonObject.getInteger("number");
                String author = jsonObject.getJSONObject("user").getString("login");
                String title = jsonObject.getString("title");
                result.add(new GitHubPullRequest(id, title, author));
            }
            EntityUtils.consume(entity1);
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        String targetHost = "gradle/gradle";
        List<GitHubPullRequest> firstPageOfPullRequests = getFirstPageOfPullRequests(targetHost);
        for (GitHubPullRequest firstPageOfPullRequest : firstPageOfPullRequests) {
            System.out.println(firstPageOfPullRequest);
        }
    }
}
