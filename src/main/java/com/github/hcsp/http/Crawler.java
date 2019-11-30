package com.github.hcsp.http;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

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

        public String getTitle() {
            return this.title;
        }

        public String getAuthor() {
            return this.author;
        }

        public int getNumber() {
            return this.number;
        }

        @Override
        public String toString() {
            return getNumber() + " ### " + getTitle() + " ### " + getAuthor();
        }
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息

    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {

        List<GitHubPullRequest> pullRequests = new ArrayList<>();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls?state=all&page=1");
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            String author;
            int number;
            String title;
            String str = EntityUtils.toString(entity);
            JSONArray jsonArray = JSONArray.parseArray(str);
            for (Object object : jsonArray) {
                JSONObject jsonObject = (JSONObject) object;
                title = jsonObject.getString("title");
                number = jsonObject.getInteger("number");
                author = jsonObject.getJSONObject("user").getString("login");
                pullRequests.add(new GitHubPullRequest(number, title, author));
            }
            return pullRequests;
        } finally {
            httpClient.close();
            response.close();
        }
    }
}
