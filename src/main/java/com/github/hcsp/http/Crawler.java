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
        getFirstPageOfPullRequests("golang/go");
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        ArrayList<GitHubPullRequest> gitHubPullRequests = new ArrayList<GitHubPullRequest>();

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.github.com" + "/repos/" + repo + "/pulls");
        CloseableHttpResponse response = httpclient.execute(httpGet);

        try {
            HttpEntity entity = response.getEntity();

            String requestString = IOUtils.toString(entity.getContent(), "utf-8");
            JSONArray pullRequestObject = (JSONArray) JSON.parse(requestString);

            if (pullRequestObject != null) {
                int len = pullRequestObject.size();
                for (int i = 0; i < len; i++) {
                    JSONObject jsonObject = pullRequestObject.getJSONObject(i);
                    int number = (int) jsonObject.get("number");
                    String title = (String) jsonObject.get("title");
                    JSONObject user = (JSONObject) jsonObject.get("user");
                    String author = (String) user.get("login");
                    gitHubPullRequests.add(new GitHubPullRequest(number, title, author));
                }
            }
            EntityUtils.consume(entity);
        } finally {
            response.close();
        }

        return gitHubPullRequests;
    }
}
