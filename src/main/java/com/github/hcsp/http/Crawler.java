package com.github.hcsp.http;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    public  static final List<GitHubPullRequest> pullList=new ArrayList<>();
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
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request build = new Request.Builder().url("https://api.github.com/repos/gradle/gradle/pulls").build();
        try (Response response = okHttpClient.newCall(build).execute()) {
            InputStream is = response.body().byteStream();
            String json = IOUtils.toString(is, "UTF-8");
            JSONArray jsonArray = JSONArray.parseArray(json);
            jsonArray.forEach(Crawler::addtoList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pullList;
    }
    public static void addtoList(Object o) {
        if(o instanceof JSONObject){
            JSONObject o1 = (JSONObject) o;
            String number = o1.getString("number");
            String tittle = o1.getString("title");
            JSONObject user = o1.getJSONObject("user");
            String ID = user.getString("login");
            pullList.add(new GitHubPullRequest(Integer.parseInt(number),tittle,ID));
        }
    }
}
