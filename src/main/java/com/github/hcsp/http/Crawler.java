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

        public int getNumber() {
            return number;
        }

        public String getTitle() {
            return title;
        }

        public String getAuthor() {
            return author;
        }
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo) throws IOException {
        Document document = Jsoup.connect("https://github.com/gradle/gradle/pulls").get();
        ArrayList<Element> selecteddiv = document.select(".js-issue-row");
        List<GitHubPullRequest> gitHubPullRequestslist = new ArrayList<>();
        for (Element element : selecteddiv) {
            String title = element.child(0).child(1).child(0).text();
            String author = element.child(0).child(1).children().select(".text-small").select(".opened-by").select(".muted-link").text();
            String numberlong = element.child(0).child(1).children().select(".text-small").select(".opened-by").text();
            int number = Integer.parseInt(numberlong.substring(1, 6));
            GitHubPullRequest gitHubPullRequest = new GitHubPullRequest(number, title, author);
            gitHubPullRequestslist.add(gitHubPullRequest);

        }

        return gitHubPullRequestslist;
    }
}
