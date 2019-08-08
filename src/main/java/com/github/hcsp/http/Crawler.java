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
import org.jsoup.select.Elements;
//import org.apache.http.util.EntityUtils;
//import sun.misc.IOUtils;

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
        //创建HttpClient客户端
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //创建请求方式  post  get  http://localhost:8888/demo/test/

        String uri = "https://github.com/"+repo+"/pulls";
        HttpGet httpGet = new HttpGet(uri);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        //相应结果
        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println(statusCode);

        HttpEntity entity = response.getEntity();
        InputStream stream = entity.getContent();
        String html = IOUtils.toString(stream, "UTF-8");
        Document document = Jsoup.parse(html);
        Elements elements =  document.select(".lh-condensed");
        List<GitHubPullRequest> list = new ArrayList();

        for (Element element:elements) {
            // Pull request的编号
            int number;
            // Pull request的标题
            String title;
            // Pull request的作者的GitHub id
            String author;
            title = element.select(".js-navigation-open").text();
            author = element.select(".muted-link").text();
            number = Integer.parseInt(element.select(".opened-by").text().split(" ")[0].substring(1));
            list.add(new GitHubPullRequest(number,title,author));
            System.out.println(number);
        }
        EntityUtils.consume(entity);
        response.close();
        httpClient.close();
        return list;
    }

    public static void main(String[] args) throws IOException {
        List<GitHubPullRequest> list = getFirstPageOfPullRequests("gradle/gradle");
        System.out.println(list);
    }
}
