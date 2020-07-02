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
import java.nio.charset.StandardCharsets;
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
        CloseableHttpClient httpclient = HttpClients.createDefault();
//        https://api.github.com/repos/gradle/gradle https://api.github.com/repos/gradle/gradle/pulls
        String url = "https://api.github.com/repos/" + repo + "/pulls";
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = httpclient.execute(httpGet);
        List<GitHubPullRequest> result = new ArrayList<>();

        try {
            HttpEntity entity = response.getEntity();
            //将InputStream to String
            String jsonStr = IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8);
            //将String转成JSONArray
            JSONArray jsonArray = JSON.parseArray(jsonStr);
            for (Object object : jsonArray) {
                int number = Integer.parseInt(((JSONObject) object).get("number").toString());
                String title = ((JSONObject) object).get("title").toString();
                Object user = ((JSONObject) object).get("user");
                String author = ((JSONObject) user).get("login").toString();

                GitHubPullRequest gitHubPullRequest = new GitHubPullRequest(number, title, author);
                result.add(gitHubPullRequest);
            }
        } finally {
            response.close();
        }
        return result;
    }
}
