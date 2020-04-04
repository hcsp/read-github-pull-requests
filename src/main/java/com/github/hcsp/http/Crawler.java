package com.github.hcsp.http;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

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

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpget = new HttpGet(String.format("https://api.github.com/repos/%s/pulls", repo));
        HttpResponse response = httpClient.execute(httpget);

        List<GitHubPullRequest> list = new ArrayList<>();
        int status = response.getStatusLine().getStatusCode();
        if (status == 200) {
            String result = EntityUtils.toString(response.getEntity(), "UTF-8");
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject pull = jsonArray.getJSONObject(i);
                JSONObject user = pull.getJSONObject("user");
                GitHubPullRequest pullRequest = new GitHubPullRequest(pull.getInt("number"), pull.getString("title"), user.getString("login"));
                list.add(pullRequest);
            }
        }
        return list;
    }
}
