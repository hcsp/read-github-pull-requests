package com.github.hcsp.http;

import com.alibaba.fastjson.JSONArray;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/gradle/gradle/pulls");
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        List<GitHubPullRequest> list = new ArrayList<>();
        try {
            System.out.println(response1.getStatusLine());
            HttpEntity entity1 = response1.getEntity();
            InputStream is = entity1.getContent();
            String strArr = IOUtils.toString(is, "utf-8");
            //第二种方式
            List<Map<String, Object>> listObjectSec = JSONArray.parseObject(strArr, List.class);
            for (Map<String, Object> mapList : listObjectSec) {
                String title = (String) mapList.get("title");
                int number = (int) mapList.get("number");
                Map<String, Object> userMap = (Map<String, Object>) mapList.get("user");
                String author = (String) userMap.get("login");
                GitHubPullRequest gitHubPullRequest = new GitHubPullRequest(number, title, author);
                list.add(gitHubPullRequest);
            }
            EntityUtils.consume(entity1);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            response1.close();
        }
        return list;
    }

}
