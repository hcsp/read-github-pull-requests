package com.github.hcsp.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

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
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls");
        CloseableHttpResponse response = httpclient.execute(httpGet);
        System.out.println(response.getStatusLine());
        HttpEntity entity1 = response.getEntity();
        InputStream is = entity1.getContent();
        String result = CharStreams.toString(new InputStreamReader(is, Charsets.UTF_8));
        JSONArray jsonArray = JSON.parseArray(result);
        List<GitHubPullRequest> gitHubPullRequests = new ArrayList<>();
        for (Object o : jsonArray) {
            JSONObject jsonObject = (JSONObject) o;
            int id = jsonObject.getInteger("number");
            String title = jsonObject.getString("title");
            String author = jsonObject.getJSONObject("user").getString("login");
            gitHubPullRequests.add(new GitHubPullRequest(id, title, author));
        }
        return gitHubPullRequests;
//第二种方法
//        List<GitHubPullRequest> list = new ArrayList<>();
//        Document doc = (Document) Jsoup.connect("https://github.com/" + repo + "/pulls").userAgent("Mozilla/5.0").timeout(10 * 1000).get();
//        Elements issues = doc.select(".js-issue-row");
//        for (Element issue : issues) {
//            int number = Integer.parseInt(issue.select(".opened-by").text().substring(1, 6));
//            String title = issue.select(".markdown-title").text();
//            String author = issue.select(".opened-by .Link--muted").text();
//            list.add(new GitHubPullRequest(number, title, author));
//        }
//        return list;

    }


    public static void main(String[] args) throws IOException {
        new Crawler();
        getFirstPageOfPullRequests("gradle/gradle");
    }


}
