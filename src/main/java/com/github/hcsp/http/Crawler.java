package com.github.hcsp.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;

public class Crawler {
    static class GitHubPullRequest {
        // Pull request的编号
        int number;
        // Pull request的标题
        String title;
        User user;

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        @Override
        public String toString() {
            return "GitHubPullRequest{" +
                    "number=" + number +
                    ", title='" + title + '\'' +
                    ", user=" + user +
                    '}';
        }
    }

    static class User {
        private String login;

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        @Override
        public String toString() {
            return "User{" +
                    "login='" + login + '\'' +
                    '}';
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println(getFirstPageOfPullRequests("gradle/gradle"));
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet("https://api.github.com/repos/" + repo + "/pulls?page=1&per_page=10");
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                HttpEntity entity = response.getEntity();
                // return it as a String
                String result = EntityUtils.toString(entity);

                return JSON.parseObject(result, new TypeReference<List<GitHubPullRequest>>() {
                });
            }
        }
    }
}
