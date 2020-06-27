package com.github.hcsp.http;

import com.squareup.okhttp.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
        //https://api.github.com/repos/gradle/gradle/pulls

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://github.com/gradle/gradle/pulls")
                .build();

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) {
            throw new IOException("服务器端错误: " + response);
        }

        String html = response.body().string();
        Document document = Jsoup.parse(html);

        Elements pull = document.select(".js-issue-row");

        List<GitHubPullRequest> pullInfo = new ArrayList<>();

        for (Element element : pull) {
            String title = element.child(0).child(1).child(0).text();
            System.out.println(title);

            String author = element.child(0).child(1).children().select(".text-small").select(".opened-by").select(".muted-link").text();
            System.out.println(author);

            String info = element.child(0).child(1).children().select(".text-small").select(".opened-by").text();

            String[] newInfo = info.split(" ");

            Integer number = Integer.parseInt(newInfo[0].substring(1, newInfo[0].length()));
            System.out.println(number);

            pullInfo.add(new GitHubPullRequest(number, title, author));

        }

        return pullInfo;
    }
}
