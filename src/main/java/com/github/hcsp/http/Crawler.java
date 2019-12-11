package com.github.hcsp.http;

import com.google.common.base.Splitter;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
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
        String url = "https://github.com/" + repo + "/pulls";
        List<GitHubPullRequest> results = new ArrayList<>();

        String html = getUrlResponse(url);

        parseHtml(results, html);

        return results;

    }

    private static void parseHtml(List<GitHubPullRequest> results, String html) {
        Document document = Jsoup.parse(html);

        Elements elements = document.getElementsByClass("js-issue-row");

        for (Element element : elements) {

            String  title = element.select(".link-gray-dark").text();

            String tempNum = element.select(".link-gray-dark").attr("id");

            String numberStr = Splitter.on("_").trimResults().splitToList(tempNum).get(1);

            Integer number = Integer.valueOf(numberStr);

            String author = element.select(".opened-by").select(".muted-link").text();

            GitHubPullRequest gitHubPullRequest = new GitHubPullRequest(number, title, author);

            results.add(gitHubPullRequest);
        }

    }

    private static String getUrlResponse(String url) throws IOException {

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        Call call = okHttpClient.newCall(request);
        Response response = call.execute();
        return response.body().string();

    }


    public static void main(String[] args) throws IOException {
        getFirstPageOfPullRequests("gradle/gradle");
    }
}
