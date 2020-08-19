package com.github.hcsp.http;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
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
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        List<GitHubPullRequest> list = new ArrayList<>();
        CloseableHttpClient httpClient = HttpClients.createDefault();//打开链接
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls");//获取请求
        CloseableHttpResponse response = httpClient.execute(httpGet);//得到数据
        HttpEntity entity = response.getEntity();//转成实体
        String html1 = EntityUtils.toString(entity, "utf-8");//从实体转成json编码的字符串
        JSONArray array = JSONArray.parseArray(html1);//使用fastjson的方法转成一个json的数组
        for (Object object : array) {//遍历数组，得到想要的数据
            String title = (String) ((JSONObject) object).get("title");
            Object user = ((JSONObject) object).get("user");
            String author = (String) ((JSONObject) user).get("login");
            int number = (int) (((JSONObject) object).get("number"));
            list.add(new GitHubPullRequest(number, title, author));
            System.out.println("request的编号为：" + number + "。request的标题为：" + title + "。GitHub 用户名为：" + author + "。");
        }
        return list;
    }

    public static void main(String[] args) throws IOException {
        String repo = "gradle/gradle";
        getFirstPageOfPullRequests(repo);
    }
}
