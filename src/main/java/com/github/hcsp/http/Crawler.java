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
        List<GitHubPullRequest> list = new ArrayList<GitHubPullRequest>();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://github.com/gradle/gradle/pulls");
        // The underlying HTTP connection is still held by the response object
        // to allow the response content to be streamed directly from the network socket.
        // In order to ensure correct deallocation of system resources
        // the user MUST call CloseableHttpResponse#close() from a finally clause.
        // Please note that if response content is not fully consumed the underlying
        // connection cannot be safely re-used and will be shut down and discarded
        // by the connection manager.
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        System.out.println(response1.getStatusLine());
        //System.out.println(response1.getCode() + " " + response1.getReasonPhrase());
        HttpEntity entity1 = response1.getEntity();
        // do something useful with the response body
        // and ensure it is fully consumed
        //EntityUtils.consume(entity1);
        InputStream stream1 = entity1.getContent();
        String html = IOUtils.toString(stream1, "UTF-8");
        //拿到html文档
        Document document = Jsoup.parse(html);
        //得到仓库名
        String repopart1 = document.getElementById("repository-container-header").child(0).child(0).child(0).child(1).text();
        String repopart2 = document.getElementById("repository-container-header").child(0).child(0).child(0).child(2).text();
        String repopart3 = document.getElementById("repository-container-header").child(0).child(0).child(0).child(3).text();
        String reponame = repopart1 + repopart2 + repopart3;
        //比较仓库名
        if (reponame.equals(repo)) {
            //创建信息列表 准备拿数据
            ArrayList<Element> pr = document.select(".js-issue-row");
            for (Element eles : pr) {
                //遍历数据得到 标题
                String prtitle = eles.child(0).child(1).child(0).text();
                System.out.println(prtitle);
                //遍历数据得到 ：#18826 opened 12 hours ago by bamboo
                String shuju = eles.child(0).child(1).children().last().child(0).text();
                //处理shuju， 得到编号
                String[] str = shuju.split("\\s+");
                String[] str2 = str[0].split("#");
                int getnum = Integer.parseInt(str2[1]);
                System.out.println(getnum);
                //处理shuju， 得到名字
                String getname = str[(str.length - 1)];
                System.out.println(getname);
                //每得到一次就加到空列表中去
                list.add(new GitHubPullRequest(getnum, prtitle, getname));
            }
            return list;
        }
        System.out.println("没有找到指定仓库名");
        return null;
    }

    public static void main(String[] args) throws IOException {
        getFirstPageOfPullRequests("gradle/gradle");
    }
}
