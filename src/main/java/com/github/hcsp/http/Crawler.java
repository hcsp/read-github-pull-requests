package com.github.hcsp.http;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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
        //创建一个对象集合
        ArrayList<GitHubPullRequest> pullRequest = new ArrayList<>();

        //1.1、创建一个客户端 【httpcomponents maven得来】
        CloseableHttpClient httpclient = HttpClients.createDefault();
        //1.2、设置请求方式，这里是Get请求【httpcomponents maven得来】
        HttpGet httpGet = new HttpGet("https://github.com/" + repo + "/pulls");

        //1.3、用httpclient客户端去执行httpGet请求。拿到response1响应
        CloseableHttpResponse response = httpclient.execute(httpGet);

        try {
            //1、设置页面请求------------------------------------------------
            //查看响应状态是否为200
            System.out.println(response.getStatusLine());
            // 对一个响应体做一些有用的处理。HttpEntity是实体
            HttpEntity entity = response.getEntity();
            //拿到请求的数据流 commons-io
            InputStream is = entity.getContent();
            //1、设置页面响应------------------------------------------------
            //设置解析编码为UTF-8
            String html = IOUtils.toString(is, "UTF-8");
            //通过jsoup去解析html页面为文档,System.out.println(document);
            Document document = Jsoup.parse(html);
            //通过select去选出Class中含有***类的元素
            ArrayList<Element> pulls = document.select(".js-issue-row");

            for (Element element : pulls) {
                pullRequest.add(
                        new GitHubPullRequest(
                                //获取编号element.child(0).child(1).child(3).child(0).text()
                                //通过搜索Java jsoup获取标签下文本发现ownText()有这个功能
                                Integer.getInteger(element.select("span[class=\"opened-by\"]").text().substring(1, 6)),
                                //获取标题element.child(0).child(1).child(0).text()
                                element.child(0).child(1).child(0).text(),
                                //获取作者element.child(0).child(1).child(3).child(0).child(1).text()
                                element.child(0).child(1).child(3).child(0).child(1).text()
                        )
                );
            }
            return pullRequest;
        } finally {
            response.close();
        }
    }
}
