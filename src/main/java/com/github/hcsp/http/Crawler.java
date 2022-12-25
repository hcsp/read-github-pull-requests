package com.github.hcsp.http;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.*;

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
        String tempRepo = "https://api.github.com/repos/" + repo + "/pulls?page=1";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(tempRepo);
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        try {
//            System.out.println(response1.getStatusLine());
            HttpEntity entity1 = response1.getEntity();
            String result = EntityUtils.toString(entity1);
            JSONArray jsonArray = JSONArray.parseArray(result);
            System.out.println(jsonArray.getJSONObject(0).get("issue_url"));
            List<GitHubPullRequest> list = new ArrayList<GitHubPullRequest>();
            for (int i = 0; i < jsonArray.size(); i++) {
                int number = (int) jsonArray.getJSONObject(i).get("number");
                String title = (String) jsonArray.getJSONObject(i).get("title");
                String author = (String) JSONObject.parseObject(jsonArray.getJSONObject(i).get("user").toString()).get("login");
                System.out.println(number);
                System.out.println(title);
                System.out.println(author);
                GitHubPullRequest g = new GitHubPullRequest(number, title, author);
                list.add(g);
            }
            return list;
        } finally {
            response1.close();
        }
    }

    public static void main(String[] args) throws IOException {
        Crawler.getFirstPageOfPullRequests("gradle/gradle");
    }
}
