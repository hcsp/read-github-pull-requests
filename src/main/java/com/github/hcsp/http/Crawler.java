package com.github.hcsp.http;

import com.alibaba.fastjson.JSON;
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
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls?page=1");
        CloseableHttpResponse response = httpclient.execute(httpGet);
        HttpEntity httpEntity = response.getEntity();
        String res = EntityUtils.toString(httpEntity);
        response.close();
        List<GitHubPullRequest> gitHubPullRequests = null;
        if (res != null) {
            List<LinkedHashMap> result = JSON.parseArray(res, LinkedHashMap.class);
            if (result != null) {
                gitHubPullRequests = new ArrayList<>();
                for (LinkedHashMap map : result) {
                    int number = (int) map.get("number");
                    String title = map.get("title").toString();
                    String author = ((Map) map.get("user")).get("login").toString();
                    GitHubPullRequest request = new GitHubPullRequest(number, title, author);
                    gitHubPullRequests.add(request);
                }
            }
        }
        return gitHubPullRequests;
    }
}
