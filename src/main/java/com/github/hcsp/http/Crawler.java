package com.github.hcsp.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;


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
        List<GitHubPullRequest> gitHubPullRequests = new ArrayList<>();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls");
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            InputStream inputStream = response.getEntity().getContent();
            String html = IOUtils.toString(inputStream, "UTF-8");
            JSONArray jsonArray = JSON.parseArray(html);

            for (Object o : jsonArray) {
                JSONObject jsonObject = (JSONObject) o;
                int id = jsonObject.getInteger("number");
                String title = jsonObject.getString("title");
                String author = jsonObject.getJSONObject("user").getString("login");
                gitHubPullRequests.add(new GitHubPullRequest(id, title, author));
            }
        }
        return gitHubPullRequests;


    }

    public static void main(String[] args) throws IOException {
        new Crawler();
        Crawler.getFirstPageOfPullRequests("gradle/gradle");

    }
}
