package com.github.hcsp.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

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

    public static void main(String[] args) throws IOException {

        getFirstPageOfPullRequests("gradle/gradle");
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        String res = null;
        CloseableHttpResponse response = null;
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls?page=1");
            response = httpclient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            res = EntityUtils.toString(httpEntity);
        } finally {
            if (response != null) {
                response.close();
            }
        }
        List<GitHubPullRequest> gitHubPullRequests = null;
        if (res != null) {
            JSONArray jsonArray = JSON.parseArray(res);
            if (jsonArray != null) {
                gitHubPullRequests = new ArrayList<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int number = jsonObject.getInteger("number");
                    String title = jsonObject.getString("title");
                    String login = jsonObject.getJSONObject("user").getString("login");
                    gitHubPullRequests.add(new GitHubPullRequest(number, title, login));
                }
            }
        }
        return gitHubPullRequests;
    }

}

