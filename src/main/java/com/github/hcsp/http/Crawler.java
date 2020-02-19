package com.github.hcsp.http;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
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
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://github.com/" + repo + "/pulls");
        CloseableHttpResponse response = httpclient.execute(httpGet);

        try {
            System.out.println(response.getStatusLine());
            HttpEntity entity1 = response.getEntity();


            InputStream inputStream = entity1.getContent();
            String html = IOUtils.toString(inputStream, "UTF-8");
            Document document = Jsoup.parse(html);

            EntityUtils.consume(entity1);

            //选择包含需要提取内容范围（包含某个个有共同字节的内容来选择范围）
            ArrayList<Element> issues = document.select(".js-issue-row");
            System.out.println(issues);
            for (Element element : issues) {
                String gettitle = element.child(0).child(1).child(0).text(); //child从0开始算，最近的一个为0
                // 把字符串里面不是数字的字符转换为空字符
                int getnumber = Integer.parseInt(element.selectFirst("a").attr("id").replaceAll("[^\\d]", ""));

                String[] author = element.selectFirst("span.opened-by").text().split("\\s");
                String getauthor = author[author.length - 1];

                GitHubPullRequest gitHubPullRequest = new GitHubPullRequest(getnumber, gettitle, getauthor);
                list.add(gitHubPullRequest);
            }
        } finally {
            response.close();
        }
        return list;
    }
}
