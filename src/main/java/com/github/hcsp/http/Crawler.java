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
        List<GitHubPullRequest> list = new ArrayList<>();

        CloseableHttpClient httpclient = createDefault();
        HttpGet httpGet = new HttpGet("https://github.com/" + repo + "/pulls");
        CloseableHttpResponse response = httpclient.execute(httpGet);

        HttpEntity entity = response.getEntity();
        InputStream is = entity.getContent();
        String html = IOUtils.toString(is, "UTF-8");

        JSONArray objects = JSONArray.parseArray(html);
        for (Object obj : objects) {
            jsonObject = (JSONObject) obj;

            int id = jsonObject.getIntValue("number");
            String title = jsonObject.getString("title");
            String userID = jsonObject.getJSONObject("user").getString("login");
            list.add(new GitHubPullRequest(id, title, userID));
        }
        return list;
    }

    public static void main(String[] args) throws IOException {
        getFirstPageOfPullRequests("gradle/gradle");
    }
}
