package com.github.hcsp.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;

import java.net.URL;
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

        URL url = new URL ("https://api.github.com/repos/gradle/gradle/pulls");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection ();
        connection.setRequestMethod ("GET");
        connection.setRequestProperty ("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
        connection.connect ();

        int code = connection.getResponseCode ();
        InputStream inputStream = connection.getInputStream ();


        StringWriter writer = new StringWriter ();
        IOUtils.copy (inputStream, writer, "UTF-8");

        String html = writer.toString ();
        JSONArray arrays = JSON.parseArray (html);
        ArrayList<GitHubPullRequest> lists = new ArrayList<GitHubPullRequest> ();
        for (int i = 0; i < arrays.size (); i++) {
            JSONObject json = arrays.getJSONObject (i);
            int number =   json.getIntValue ("number");
            String title = json.getString ("title");
            String author = json.getJSONObject ("user").getString ("login");// 获取json串中json串最的方法。获取一个之后，在获取另外一个。
            GitHubPullRequest gitHubPullRequest = new GitHubPullRequest (number,title,author);
            lists.add (gitHubPullRequest);
        }
        return lists;
    }
}