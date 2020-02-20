package com.github.hcsp.http;

import com.alibaba.fastjson.JSONArray;
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
import sun.nio.ch.IOUtil;

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
        String repo = "hcsp/read-github-pull-requests";
        getFirstPageOfPullRequests(repo);
    }

    // List<GitHubPullRequest>给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
   /* public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        List<GitHubPullRequest> res = new ArrayList<>();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String html = "https://github.com/" + repo + "/pulls";
        HttpGet httpGet = new HttpGet(html);
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        System.out.println(response1);

        try {
            System.out.println(response1.getStatusLine());
            HttpEntity entity1 = response1.getEntity();
            InputStream is = entity1.getContent();
            //将input stream 变成 string
            String htmlDoc = IOUtils.toString(is, "UTF-8");

            Document document = Jsoup.parse(htmlDoc);
            System.out.println(document);
            ArrayList<Element> elements = document.select(".js-issue-row");

            for (Element element :
                    elements) {
                System.out.println(element);
                String issueId = element.id();
                String id = issueId.substring(6);
                int number = Integer.parseInt(id);
                //标题
                String title = element.child(0).child(1).child(0).text();
                //作者名
                String author = element.child(0).child(1).child(3).child(0).child(1).text();
                GitHubPullRequest gitHubPullRequest = new GitHubPullRequest(number, title, author);
                res.add(gitHubPullRequest);
            }


        } finally {
            response1.close();
        }
        return res;

    }*/

    //第二种方法 访问api
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        List<GitHubPullRequest> res = new ArrayList<>();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String html = "https://api.github.com/repos/" + repo + "/pulls";
        HttpGet httpGet = new HttpGet(html);
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        System.out.println(response1);
        HttpEntity entity1 = response1.getEntity();
        InputStream is = entity1.getContent();
        //将input stream 变成 string
        String string = IOUtils.toString(is, "UTF-8");
        JSONArray jsonArray = JSONArray.parseArray(string);
        for (int i = 0; i < jsonArray.size(); i++) {
            int number = jsonArray.getJSONObject(i).getInteger("number");
            String title = jsonArray.getJSONObject(i).getString("title");
            String author = jsonArray.getJSONObject(0).getJSONObject("user").getString("login");
            GitHubPullRequest gitHubPullRequest = new GitHubPullRequest(number, title, author);
            res.add(gitHubPullRequest);
        }

        return res;


    }


}