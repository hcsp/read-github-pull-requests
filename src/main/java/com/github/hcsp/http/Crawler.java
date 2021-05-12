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

        List<GitHubPullRequest> gitHubPullRequests = new ArrayList<>();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls");
        CloseableHttpResponse response1 = httpclient.execute(httpGet);

        try {
            System.out.println(response1.getStatusLine());
            HttpEntity entity1 = response1.getEntity();
            InputStream content = entity1.getContent();
            String html = IOUtils.toString(content, "UTF-8");
            JSONArray pullRequests = JSON.parseArray(html);

            for (int i = 0; i <pullRequests .size(); i++) {
                JSONObject pr = pullRequests.getJSONObject(i);
                Integer number = pr.getInteger("number");
                String title = pr.getString("title");
                String author = pr.getJSONObject("user").getString("login");
                gitHubPullRequests.add(new GitHubPullRequest(number, title, author));
            }
            EntityUtils.consume(entity1);
        }
        finally {
            response1.close();
        }
        return gitHubPullRequests;
    }

}












