package com.github.hcsp.http;

import com.alibaba.fastjson.JSONArray;
import org.jsoup.Jsoup;

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
        int pullRequestNumber = 5;
        ArrayList<GitHubPullRequest> list = new ArrayList<>();
        String url = "https://api.github.com/repos/" + repo + "/pulls?page=1&per_page=5";
        String json = Jsoup.connect(url).ignoreContentType(true).execute().body();
        JSONArray jsonArray = JSONArray.parseArray(json);

        for (int i = 0; i < pullRequestNumber; i++) {
            int number = Integer.parseInt(jsonArray.getJSONObject(i).getString("number"));
            String title = jsonArray.getJSONObject(i).getString("title");
            String author = jsonArray.getJSONObject(i).getJSONObject("user").getString("login");
            GitHubPullRequest githubpullrequest = new GitHubPullRequest(number, title, author);
            list.add(githubpullrequest);
        }
        return list;
    }
}
