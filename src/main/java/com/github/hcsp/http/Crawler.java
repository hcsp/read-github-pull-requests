package com.github.hcsp.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    static class GitHubPullRequest {
        // Pull request的编号
        @Expose
        int number;
        // Pull request的标题
        @Expose
        String title;
        // Pull request的作者的 GitHub 用户名
        @Expose
        String author;

        GitHubPullRequest(int number, String title, String author) {
            this.number = number;
            this.title = title;
            this.author = author;
        }
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) {
        final String URL = "https://api.github.com/repos/" + repo + "/pulls?per_page=25&page=1";
        final OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URL)
                .build();
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            String result = response.body().string();
//            System.out.println(gson.fromJson(result, ArrayList.class));
            GithubPullFormat[] list = gson.fromJson(result, GithubPullFormat[].class);
            List<GitHubPullRequest> gprList = new ArrayList<>();
            for (GithubPullFormat gp : list) {
                GitHubPullRequest gpr = new GitHubPullRequest(gp.number, gp.title, gp.user.user);
                gprList.add(gpr);
            }
            return gprList;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class GithubPullFormat {
        @Expose
        String title;

        @Expose
        int number;

        @Expose
        User user;
    }

    static class User {
        @SerializedName("login")
        @Expose
        String user;
    }

    public static void main(String[] args) {
        getFirstPageOfPullRequests("gradle/gradle");
    }
}
