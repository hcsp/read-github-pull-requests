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
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) {

        List<GitHubPullRequest> list = new ArrayList<>();

        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet("https://api.github.com/repos/"+repo+"/pulls");

            try (CloseableHttpResponse response1 = httpclient.execute(httpGet)) {
                HttpEntity entity1 = response1.getEntity();

                InputStream content = entity1.getContent();
                String jsonString = IOUtils.toString(content, "UTF-8");
                JSONArray jsonArray = JSON.parseArray(jsonString);

                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int number = jsonObject.getInteger("number");
                    String title = jsonObject.getString("title");
                    String author = jsonObject.getJSONObject("user").getString("login");;
                    GitHubPullRequest pullRequest = new GitHubPullRequest(number, title, author);
                    list.add(pullRequest);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return list;
    }


}
