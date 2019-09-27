package com.github.hcsp.http;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


import java.io.IOException;
import java.io.InputStream;
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
        List<GitHubPullRequest> PullRequest = new ArrayList<>();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String url = "https://api.github.com/repos/" + repo + "/pulls";
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response1 = httpclient.execute(httpGet);

        try {
            HttpEntity entity1 = response1.getEntity();

            InputStream is = entity1.getContent();

            String str = IOUtils.toString(is, "utf-8");

            JSONArray jsonArray = JSONArray.parseArray(str);

            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                int number = jsonObject.getInteger("number");
                String title = jsonObject.getString("title");
                String login = jsonObject.getJSONObject("user").getString("login");
                PullRequest.add(new GitHubPullRequest(number, title, login));
            }
            EntityUtils.consume(entity1);
        } finally {
            response1.close();
        }
        return PullRequest;
    }
}
