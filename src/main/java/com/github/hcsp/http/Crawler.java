package com.github.hcsp.http;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

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
        List<GitHubPullRequest> res = new ArrayList<>();

        String url = "https://github.com/"+repo+"/pulls";
        Document doc = Jsoup.connect(url).get();

        ArrayList<Element> issue = doc.select(".js-issue-row");
        for (Element element : issue) {
            int number = Integer.parseInt(element.select(".mt-1.text-small.text-gray").get(0).child(0).text().split(" ")[0].substring(1));
            String title = element.child(0).child(1).child(0).text();
            String author = element.select(".mt-1.text-small.text-gray").get(0).child(0).child(1).text();
            GitHubPullRequest tmp = new GitHubPullRequest(number, title, author);
            res.add(tmp);
        }

        return res;
    }
}
