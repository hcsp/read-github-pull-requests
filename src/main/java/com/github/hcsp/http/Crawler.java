package com.github.hcsp.http;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.apache.http.impl.client.HttpClients.createDefault;


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
        JSONObject jsonObject;
        List<GitHubPullRequest> pullRequests = new ArrayList<>();

        CloseableHttpClient httpclient = createDefault();
        // System.out.println("https://api.github.com/repos/" + repo + "/pulls");
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls");
        CloseableHttpResponse response1 = httpclient.execute(httpGet);

        System.out.println(response1.getStatusLine());
        HttpEntity entity1 = response1.getEntity();
        InputStream inputStream = entity1.getContent();
        String httpContent = IOUtils.toString(inputStream, "UTF-8");

        JSONArray apiResponseArray = JSONArray.parseArray(httpContent);

        for (Object o : apiResponseArray) {
            jsonObject = (JSONObject) o;

            int id = jsonObject.getIntValue("number");
            String userName = jsonObject.getJSONObject("user").getString("login");
            String title = jsonObject.getString("title");
            pullRequests.add(new GitHubPullRequest(id, title, userName));
        }

        return pullRequests;

    }
}
