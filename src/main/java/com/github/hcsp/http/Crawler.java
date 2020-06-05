package com.github.hcsp.http;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;

public class Crawler {
    private static final String GITHUB = "https://api.github.com/repos/";

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
        URL url = new URL(GITHUB + repo + "/pulls?page=1&per_page=30&state=open");
        URLConnection pr = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                pr.getInputStream()));
        Gson gson = new Gson();
        JsonObject[] jsonObjects = gson.fromJson(in, JsonObject[].class);
        List<GitHubPullRequest> gitHubPullRequests = new LinkedList<>();
        for (JsonObject jsonObject :
                jsonObjects) {
            int number = jsonObject.get("number").getAsInt();
            String title = jsonObject.get("title").getAsString();
            String author = jsonObject.getAsJsonObject("user").get("login").getAsString();
            GitHubPullRequest gitHubPullRequest = new GitHubPullRequest(number, title, author);
            gitHubPullRequests.add(gitHubPullRequest);
        }
        return gitHubPullRequests;
    }
}
