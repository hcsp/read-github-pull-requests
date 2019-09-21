package com.github.hcsp.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

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
        List<GitHubPullRequest> pullsList = new ArrayList<GitHubPullRequest>();
        String url = "https://api.github.com/repos/" + repo + "/pulls?page=1&per_page=2";
        HttpClient httpClient = new HttpClient();
        GetMethod getMethod = new GetMethod(url);
        httpClient.executeMethod(getMethod);
        JSONArray reponseList = JSON.parseArray(getMethod.getResponseBodyAsString());
        for (Object obj : reponseList) {
            int number = ((JSONObject) obj).getInteger("number");
            String title = ((JSONObject) obj).getString("title");
            JSONObject userObj = ((JSONObject) obj).getJSONObject("user");
            String author = ((JSONObject) userObj).getString("login");
            GitHubPullRequest pull = new GitHubPullRequest(number, title, author);
            pullsList.add(pull);
        }

        return pullsList;
    }
}

