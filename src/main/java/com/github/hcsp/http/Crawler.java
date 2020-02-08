package com.github.hcsp.http;

import com.alibaba.fastjson.JSONArray;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;

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
            int pullRequestNumber = 5;
            ArrayList<GitHubPullRequest> list = new ArrayList<>();
            String url = "https://api.github.com/repos/" + repo + "/pulls?page=1&per_page=5";
            String json = Jsoup.connect(url).ignoreContentType(true).execute().body();
        System.out.println(json);
            JSONArray jsonArray = JSONArray.parseArray(json);
            for (int i = 0; i < pullRequestNumber; i++) {
                int number = jsonArray.getJSONObject(i).getInteger("number");
                String title = jsonArray.getJSONObject(i).getString("title");
                String author = jsonArray.getJSONObject(i).getJSONObject("user").getString("login");
                GitHubPullRequest githubpullrequest = new GitHubPullRequest(number, title, author);
                list.add(githubpullrequest);
            }
            return list;
    }

    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/gradle/gradle/pulls?page=1&per_page=5");
        CloseableHttpResponse res = httpClient.execute(httpGet);
        try{
            HttpEntity entity = res.getEntity();
            InputStream is = entity.getContent();
            System.out.println(is);
        } catch (Exception e){
            System.out.println(e);
        }
    }
}

