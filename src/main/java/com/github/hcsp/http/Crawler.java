package com.github.hcsp.http;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
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
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        List<GitHubPullRequest> results = new ArrayList<>();

        CloseableHttpClient httpClient = HttpClients.createDefault();
        String url = "https://api.github.com/repos/" + repo +"/pulls";
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = httpClient.execute(httpGet);

        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            String text = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            List<GHPullRequest> pullRequests = JSON.parseArray(text, GHPullRequest.class);
            for (GHPullRequest pullRequest : pullRequests) {
                results.add(new GitHubPullRequest(
                        pullRequest.getNumber(),
                        pullRequest.getTitle(),
                        pullRequest.getUser().getLogin()
                ));
            }
        } finally {
            response.close();
        }
        return results;
    }

    private static class GHPullRequest {
        private int number;
        private String title;
        private GHUser user;

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

        public GHUser getUser() {
            return user;
        }

        public void setUser(GHUser user) {
            this.user = user;
        }

        private class GHUser {
            private String login;
            private String url;

            public String getLogin() {
                return login;
            }

            public void setLogin(String login) {
                this.login = login;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }
    }
}
