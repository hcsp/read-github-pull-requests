package com.github.hcsp.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    private static final int PULL_REQUESTS_PER_PAGE = 25;

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
        List<GitHubPullRequest> list = new ArrayList<>();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls");
        CloseableHttpResponse response = httpclient.execute(httpGet);
        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            String body = IOUtils.toString(is, "UTF-8");
            JSONArray jsonArray = JSON.parseArray(body);
            for (int i = 0; i < PULL_REQUESTS_PER_PAGE; i++) {
                list.add(getPullRequest(jsonArray.getJSONObject(i)));
            }
        } finally {
            response.close();
        }
        return list;
    }

    private static GitHubPullRequest getPullRequest(JSONObject jsonObject) {
        int number = jsonObject.getIntValue("number");
        String title = String.valueOf(jsonObject.get("title"));
        String url = String.valueOf(((JSONObject) jsonObject.get("user")).get("html_url"));
        String[] segments = url.split("/");
        String author = segments[segments.length - 1];
        return new GitHubPullRequest(number, title, author);
    }

}
