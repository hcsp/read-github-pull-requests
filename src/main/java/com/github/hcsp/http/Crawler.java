package com.github.hcsp.http;

import com.alibaba.fastjson.JSON;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
    }

    static class GitHubPullResponse {
        int number;
        String title;
        User user;

        public int getNumber() {
            return number;
        }

        public String getTitle() {
            return title;
        }

        public User getUser() {
            return user;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setUser(User user) {
            this.user = user;
        }

        static class User {
            String login;

            public String getLogin() {
                return login;
            }

            public void setLogin(String login) {
                this.login = login;
            }
        }
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        List<GitHubPullRequest> result = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.github.com/repos/" + repo + "/pulls")
                .build();
        try (Response response = client.newCall(request).execute()) {
            List<GitHubPullResponse> gitHubPullResponses = JSON.parseArray(response.body().string(), GitHubPullResponse.class);
            for (GitHubPullResponse item :
                    gitHubPullResponses) {
                result.add(new GitHubPullRequest(item.getNumber(), item.getTitle(), item.getUser().getLogin()));
            }
        }
        return result;
    }
}
