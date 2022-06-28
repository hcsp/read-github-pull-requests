package com.github.hcsp.http;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

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

    public static void main(String[] args) throws IOException {
        getFirstPageOfPullRequests("gradle/gradle");
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        List<GitHubPullRequest> pullRequestInfo = new ArrayList();
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet("https://api.github.com/repos/gradle/gradle/pulls");
            // The underlying HTTP connection is still held by the response object
            // to allow the response content to be streamed directly from the network socket.
            // In order to ensure correct deallocation of system resources
            // the user MUST call CloseableHttpResponse#close() from a finally clause.
            // Please note that if response content is not fully consumed the underlying
            // connection cannot be safely re-used and will be shut down and discarded
            // by the connection manager.
            try (CloseableHttpResponse response1 = httpclient.execute(httpGet)) {
                System.out.println(response1.getStatusLine());
//                System.out.println(response1);
                HttpEntity entity1 = response1.getEntity();
                InputStream is = entity1.getContent();
                String contentString = IOUtils.toString(is, "UTF-8");
                JSONArray contentJson = (JSONArray) JSONValue.parse(contentString);

                for (int i = 0; i < contentJson.size(); i++) {

                    JSONObject pullRequest = (JSONObject) contentJson.get(i);
                    String title = (String) pullRequest.get("title");
                    JSONObject user = (JSONObject) (pullRequest.get("user"));
                    String userName = (String) user.get("login");
                    int number = ((Long) pullRequest.get("number")).intValue();
                    GitHubPullRequest gitHubPullrequest = new GitHubPullRequest(number, title, userName);
                    pullRequestInfo.add(gitHubPullrequest);
                }

                // do something useful with the response body
                // and ensure it is fully consumed
                EntityUtils.consume(entity1);
            }

//            HttpPost httpPost = new HttpPost("http://httpbin.org/post");
//            List<NameValuePair> nvps = new ArrayList<>();
//            nvps.add(new BasicNameValuePair("username", "vip"));
//            nvps.add(new BasicNameValuePair("password", "secret"));
//            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
//
//            try (CloseableHttpResponse response2 = httpclient.execute(httpPost)) {
//                System.out.println(response2.getCode() + " " + response2.getReasonPhrase());
//                HttpEntity entity2 = response2.getEntity();
//                // do something useful with the response body
//                // and ensure it is fully consumed
//                EntityUtils.consume(entity2);
//            }
        }
        return pullRequestInfo;
    }
}
