package com.github.hcsp.http;

import com.alibaba.fastjson.JSONArray;
import org.apache.commons.io.IOUtils;
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
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls?page=1");
        CloseableHttpResponse response = httpclient.execute(httpGet);
        InputStream inputStream = response.getEntity().getContent();

        JSONArray jsonArray = JSONArray.parseArray(IOUtils.toString(inputStream, "UTF-8"));

        ArrayList<GitHubPullRequest> list = new ArrayList<>(jsonArray.size());

        for (int i = 0; i < jsonArray.size(); i++) {
            System.out.println(jsonArray.get(i));
            String author = jsonArray.getJSONObject(i).getJSONObject("user").getString("login");
            int number = jsonArray.getJSONObject(i).getInteger("number");
            String title = jsonArray.getJSONObject(i).getString("title");
            list.add( new GitHubPullRequest(number, title, author) );
        }

        return list;
    }

}
