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
        // 创建一个default 客户端
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 发起了一个http Get请求

        StringBuilder target = new StringBuilder("https://api.github.com/repos/" + repo + "/issues");

        System.out.println(target);

        HttpGet httpGet = new HttpGet(String.valueOf(target));

//        https://api.github.com/repos/hcsp/read-github-pull-requests/issues
        // 执行这个请求拿到response
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.6)");
        // 传输的类型
        httpGet.addHeader("Content-Type", "application/x-www-form-urlencoded");

        CloseableHttpResponse response = httpclient.execute(httpGet);
        try {
            HttpEntity entity1 = response.getEntity();
            InputStream is = entity1.getContent();
            String html = IOUtils.toString(is, "UTF-8");
            JSONArray JSONArray = JSON.parseArray(html);
            return traverse(JSONArray);
        } finally {
            response.close();
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println(
                getFirstPageOfPullRequests("gradle/gradle")
        );
    }

    public static List<GitHubPullRequest> traverse(JSONArray issuesInfoList) {
        List<GitHubPullRequest> pullRequestsList =
                new ArrayList<GitHubPullRequest>();
        for (int i = 0; i < issuesInfoList.size(); i++) {
            JSONObject account = (JSONObject) issuesInfoList.getJSONObject(i).get("user");
            if (issuesInfoList.getJSONObject(i).get("pull_request") != null) {
                Integer number = (Integer) issuesInfoList.getJSONObject(i).get("number");
                String title = (String) issuesInfoList.getJSONObject(i).get("title");
                String user = account.getString("login");
                GitHubPullRequest gp = new GitHubPullRequest(number, title, user);
                pullRequestsList.add(gp);
            }
        }
        return pullRequestsList;
    }
}
