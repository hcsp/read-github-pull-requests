package com.github.hcsp.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        Crawler crawler = new Crawler();
        List<GitHubPullRequest> Reslist = new ArrayList<>();
        JSONArray objects = JSON.parseArray(crawler.run("https://api.github.com/repos/" + repo + "/pulls"));
        for (Object object : objects) {
            String title = (String) ((JSONObject) object).get("title");
            int number = (int) ((JSONObject) object).get("number");
            JSONObject user = (JSONObject) ((JSONObject) object).get("user");
            String author = (String) user.get("login");
            Reslist.add(new GitHubPullRequest(number, title, author));
        }
        return  Reslist;
    }

    public static void main(String[] args) throws IOException {
        System.out.println( getFirstPageOfPullRequests("gradle/gradle"));
    }

    OkHttpClient client = new OkHttpClient();

    String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return Objects.requireNonNull(response.body()).string();
        }
    }
}
