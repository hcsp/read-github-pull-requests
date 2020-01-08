package com.github.hcsp.http;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
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
        String url = "https://api.github.com/repos/" + repo + "/pulls";
        URL net_url = new URL(url);
        HttpsURLConnection conn = (HttpsURLConnection) net_url.openConnection();
        conn.setRequestMethod("GET");
        conn.setUseCaches(false);
        // 设置HTTP头:
        conn.setRequestProperty("Accept", "*/*");
        conn.connect();
        // 判断HTTP响应是否200:
        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("bad response");
        }
        // 获取响应内容:
        InputStream input = conn.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        StringBuilder sb = new StringBuilder();
        String temp;
        while ((temp = reader.readLine()) != null) {
            sb.append(temp);
        }
        Gson gson = new Gson();
        JsonParser jsonParser = new JsonParser();
        JsonArray jsonElements = jsonParser.parse(sb.toString()).getAsJsonArray(); //获取JsonArray对象
        List<GitHubPullRequest> beans = new ArrayList<>();
        for (JsonElement bean : jsonElements) {
            APIBean bean1 = gson.fromJson(bean, APIBean.class); //解析
            GitHubPullRequest gitHubPullRequest =
                    new GitHubPullRequest(
                            bean1.getNumber(), bean1.getTitle(), bean1.getUser().getLogin());

            beans.add(gitHubPullRequest);
        }
        reader.close();
        return beans;

    }

    static class APIBean {
        private int number;
        private String title;
        private User user;

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
}
