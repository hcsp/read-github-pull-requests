package com.github.hcsp.http;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;


import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;


class OutBean {
    int number;
    String title;

    InBean user;

    class InBean {
        String login;

        @Override
        public String toString() {
            return login;
        }
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    @Override
    public String toString() {
        return "OutBean{" +
                "number=" + number +
                ", title='" + title + '\'' +
                ", user=" + user +
                '}';
    }
}

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
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://api.github.com/repos/" + repo + "/pulls");
        CloseableHttpResponse response1 = httpclient.execute(httpGet);

        ArrayList<GitHubPullRequest> list = new ArrayList<>();

        try {
            System.out.println(response1.getStatusLine());
            HttpEntity entity1 = response1.getEntity();

            // do something useful with the response body
            // and ensure it is fully consumed

            InputStream is = entity1.getContent();

            StringWriter writer = new StringWriter();
            IOUtils.copy(is, writer, "UTF-8");
            String theString = writer.toString();

            Gson gson = new Gson();

            OutBean[] beans = gson.fromJson(theString, OutBean[].class);

            for (OutBean bean : beans) {
                list.add(new GitHubPullRequest(bean.getNumber(), bean.getTitle(), bean.user.login));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            response1.close();
        }

        return list;

    }
}
