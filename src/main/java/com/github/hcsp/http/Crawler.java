package com.github.hcsp.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.kevinsawicki.http.HttpRequest;

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
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) {
        List<GitHubPullRequest> gitHubPullRequestList = new ArrayList<>();

        String url = String.format("https://api.github.com/repos/%s/pulls?page=1", repo);
        String response = HttpRequest.get(url).body();
        JSONArray dataArray = JSON.parseArray(response);

        for (int i = 0; i < dataArray.size(); i++) {
            JSONObject dataItem = dataArray.getJSONObject(i);
            int number = dataItem.getIntValue("number");
            String title = dataItem.getString("title");
            String author = dataItem.getJSONObject("user").getString("login");

            GitHubPullRequest gitHubPullRequest = new GitHubPullRequest(number, title, author);
            gitHubPullRequestList.add(gitHubPullRequest);
        }

        return gitHubPullRequestList;
    }
}
