package com.github.hcsp.http;

import com.alibaba.fastjson.JSONArray;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
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

    /*方法一：直接获取页面的HTML，解析之。但是爬取了三个以后出现了不一致的结构，从而导致报错，所以这个方法很脆弱，事倍功半
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        List<GitHubPullRequest> pr = new ArrayList<GitHubPullRequest>();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://github.com/" + repo + "/pulls");
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        try {
            System.out.println(response1);
            HttpEntity entity1 = response1.getEntity();
            String theString = IOUtils.toString(entity1.getContent(), "UTF-8");
            Document doc = Jsoup.parse(theString);
            ArrayList<Element> pull = doc.select(".Box-row");
            for (Element i : pull) {
                pr.add(new GitHubPullRequest(Integer.parseInt(i.child(0).child(1).child(3).child(0).text().substring(1, 5))
                        , i.child(0).child(1).child(0).text()
                        , i.child(0).child(1).child(3).child(0).child(1).text()));
            }
        } finally {
            response1.close();
        }
        return pr;
    }
    */

    //方法二：调用github的api,利用fastjson进行解析
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        List<GitHubPullRequest> pr = new ArrayList<GitHubPullRequest>();

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls");
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        HttpEntity entity1 = response1.getEntity();
        String theString = IOUtils.toString(entity1.getContent(), "UTF-8");
        JSONArray jsonArray = JSONArray.parseArray(theString);
        for (int i = 0; i < jsonArray.size(); i++) {
            pr.add(new GitHubPullRequest(jsonArray.getJSONObject(i).getInteger("number"), jsonArray.getJSONObject(i).getString("title"), jsonArray.getJSONObject(0).getJSONObject("user").getString("login")));
        }
        return pr;
    }
}
