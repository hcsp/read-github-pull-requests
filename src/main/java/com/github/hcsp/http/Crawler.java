package com.github.hcsp.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import org.jsoup.Jsoup;

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
        ArrayList<GitHubPullRequest> list = new ArrayList<>();
        String url = "https://api.github.com/repos/" + repo + "/pulls?page=1&per_page=1";
        String json = Jsoup.connect(url).ignoreContentType(true).execute().body();
        JSONArray jsonArray = JSONArray.parseArray(json);
        int number = jsonArray.getJSONObject(0).getInteger("number");
        String title = jsonArray.getJSONObject(0).getString("title");
        String auther = jsonArray.getJSONObject(0).getJSONObject("user").getString("login");
        GitHubPullRequest gitHubPullRequest = new GitHubPullRequest(number, title, auther);
        list.add(gitHubPullRequest);
        return list;

    }

}
