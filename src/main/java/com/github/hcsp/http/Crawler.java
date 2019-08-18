package com.github.hcsp.http;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class Crawler {
    private static final String DOMAIN = "https://api.github.com";
    private static final String METHOD_GET = "GET";
    private static final String CATEGORY = "pulls";

    static class GitHubPullRequest {
        // Pull request的编号
        int number;
        // Pull request的标题
        String title;
        // Pull request的作者的GitHub id
        String author;

        GitHubPullRequest(int number, String title, String author) {
            this.number = number;
            this.title = title;
            this.author = author;
        }
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        String result = HTTPGet(repo);
        return analyzeResultString(result);
    }

    /**
     * 发送请求获得结果并转换成字符串
     * @param repo 仓库名称
     * @return 返回结果字符串
     * @throws IOException 异常
     */
    public static String HTTPGet(String repo) throws IOException {
        String urlString = DOMAIN + "/repos/" + repo + "/" + CATEGORY;
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(METHOD_GET);

        InputStream resultStream = connection.getInputStream();

        Scanner s = new Scanner(resultStream).useDelimiter("\\A");
        String result = s.hasNext() ? s.next() : "";
        return result;
    }

    /**
     * 解析字符串
     * @param result 获取到的结果字符串
     * @return 返回一个GitHubPullRequest的List
     */
    public static List<GitHubPullRequest> analyzeResultString(String result) {
        List<GitHubPullRequest> list = new ArrayList<>();
        JSONArray array = JSONArray.parseArray(result);
        for (Iterator it = array.iterator(); it.hasNext(); ) {
            JSONObject pull = (JSONObject) it.next();
            JSONObject user = (JSONObject) pull.get("user");
            String title = pull.get("title").toString();
            int number = Integer.parseInt(pull.get("number").toString());
            String author = user.get("login").toString();
            list.add(new GitHubPullRequest(number, title, author));
        }

        return list;
    }
}
