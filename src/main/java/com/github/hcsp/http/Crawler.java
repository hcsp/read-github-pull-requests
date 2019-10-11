package com.github.hcsp.http;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
        long number;
        String title;
        String author;
        List<GitHubPullRequest> res = new ArrayList<>();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls");
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        try {
            HttpEntity entity1 = response1.getEntity();
            InputStream is = entity1.getContent();
            JSONParser jsonParser = new JSONParser();

            JSONArray jsonArray = (JSONArray) jsonParser.parse(
                    new InputStreamReader(is, "UTF-8"));
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                title = (String) jsonObject.get("title");
                number = (long) jsonObject.get("number");
                JSONObject user = (JSONObject) jsonObject.get("user");
                author = (String) user.get("login");
                GitHubPullRequest pr = new GitHubPullRequest((int) number, title, author);
                res.add(pr);
            }
            EntityUtils.consume(entity1);
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            response1.close();
        }
        return res;
    }
}
