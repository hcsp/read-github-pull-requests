package com.github.hcsp.http;

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
        ArrayList<GitHubPullRequest> response = new ArrayList<>();
        String url = "https://github.com/"+ repo + "/pulls";
        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.select(".js-issue-row");
        for (Element e : elements){
            String[] number_temp = e.child(0).child(1).select(".text-small").text().split(" ");
            int number = Integer.valueOf(number_temp[0].substring(1));
            String title = e.child(0).child(1).child(0).text();
            String author = e.child(0).child(1).select(".muted-link").text();
            response.add(new GitHubPullRequest(number, title, author));
        }
        return response;
    }



    /*public static void main(String[] args)throws Exception {
        ArrayList<GitHubPullRequest> response = new ArrayList<>();
        String url = "https://github.com/golang/go/pulls";
        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.select(".js-issue-row");
        for (Element e : elements){
            String[] number_temp = e.child(0).child(1).child(3).child(0).text().split(" ");
            int number = Integer.valueOf(number_temp[0].substring(1));
            String title = e.child(0).child(1).child(0).text();
            String author = e.child(0).child(1).child(3).child(0).select(".muted-link").text();
            response.add(new GitHubPullRequest(number,title,author));
        }

    }

     */
}
