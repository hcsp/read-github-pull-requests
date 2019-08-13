package com.github.hcsp.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
        // Pull request的作者的GitHub id
        String author;

        GitHubPullRequest(int number, String title, String author) {
            this.number = number;
            this.title = title;
            this.author = author;
        }
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        String url = String.format("https://api.github.com/repos/%s/pulls?page=1", repo);
        String json = getPullsFromGithubApi(url);
        JSONArray pullsArray = JSON.parseArray(json);
        ArrayList result = new ArrayList<GitHubPullRequest>();

        for (int i = 0; i < pullsArray.size(); i++) {
            JSONObject pull = pullsArray.getJSONObject(i);
            JSONObject user = (JSONObject) pull.get("user");
            Integer number = pull.getInteger("number");
            String title = pull.getString("title");
            String login = user.getString("login");
            result.add(new GitHubPullRequest(number, title, login));
        }

        return result;
    }

    private static String getPullsFromGithubApi(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }
}
