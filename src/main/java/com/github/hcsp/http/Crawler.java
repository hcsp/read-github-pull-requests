package com.github.hcsp.http;

import com.alibaba.fastjson.JSONArray;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Crawler {
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
        List<GitHubPullRequest> resultList = new ArrayList<>();

        OkHttpClient client = new OkHttpClient();

        String resString = "";
        String url = "https://api.github.com/repos/" + repo + "/pulls";
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            resString = response.body().string();
            List<Map<String, Object>> listMap = (List<Map<String, Object>>) JSONArray.parse(resString);
            Iterator<Map<String, Object>> iter = listMap.iterator();
            while (iter.hasNext()) {
                Map<String, Object> onePRMap = iter.next();
                int prNumber = (int) onePRMap.get("number");
                String prTitle = (String) onePRMap.get("title");
                Map<String, Object> prUser = (Map<String, Object>) onePRMap.get("user");
                String prAuthor = (String) prUser.get("login");
                GitHubPullRequest newPR = new GitHubPullRequest(prNumber, prTitle, prAuthor);
                resultList.add(newPR);
            }
        }

        return resultList;
    }
}
