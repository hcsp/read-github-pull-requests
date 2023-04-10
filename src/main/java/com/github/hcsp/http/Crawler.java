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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
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
        //获取http客户端
        CloseableHttpClient httpclient = HttpClients.createDefault();
        //创建GET请求
        HttpGet httpGet = new HttpGet("https://github.com/gradle/gradle/pulls");
        // The underlying HTTP connection is still held by the response object
        // to allow the response content to be streamed directly from the network socket.
        // In order to ensure correct deallocation of system resources
        // the user MUST call CloseableHttpResponse#close() from a finally clause.
        // Please note that if response content is not fully consumed the underlying
        // connection cannot be safely re-used and will be shut down and discarded
        // by the connection manager.
        //响应模式
        CloseableHttpResponse response1 = httpclient.execute(httpGet);

        try {
            System.out.println(response1.getStatusLine());
            HttpEntity entity1 = response1.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            //获得数据流的内容
            InputStream is = entity1.getContent();
            String html = IOUtils.toString(is, "UTF-8");
            //将html转换为JSON格式
            JSONArray pullRequest = JSON.parseArray(html);
            for (int i = 0; i < pullRequest.size(); i++) {
                JSONObject pr = pullRequest.getJSONObject(i);
                Integer number = pr.getInteger("number");
                String title = pr.getString("title");
                String author = pr.getString("user");
                gitHubPullRequests.add(new GitHubPullRequest(number, title, author));

            }
            EntityUtils.consume(entity1);


        } finally {
            response1.close();
        }

        return gitHubPullRequests;

    }

}
