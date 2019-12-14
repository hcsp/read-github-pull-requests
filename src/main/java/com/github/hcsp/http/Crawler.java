package com.github.hcsp.http;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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

        @Override
        public String toString() {
            return "{number="+number+", title="+title+", author="+author+"}";
        }
    }

    // 给定一个仓库名，例如"golang/go"，或者"gradle/gradle"，返回第一页的Pull request信息
    public static List<GitHubPullRequest> getFirstPageOfPullRequests(String repo){
        return getFirstPageOfPullRequestsByGitHubApi(repo);
    }
    //爬取html
    public static List<GitHubPullRequest> getFirstPageOfPullRequestsByGitHubUrl(String repo){
        String url = "https://github.com/"+ repo +"/pulls";
        List<GitHubPullRequest> list = new ArrayList<>();

        try{
            String html = getUrlContent(url);
            Document doc = Jsoup.parse(html);

            List<Element> elems = doc.select(".Box-row");
            for ( Element elem: elems ) {

                String title = elem.select(".link-gray-dark").get(0).text();
                String author = elem.select(".muted-link").get(0).text();
                String idStr = elem.attr("id");
                int number = Integer.parseInt(idStr.split("_")[1]);
                list.add(new GitHubPullRequest(number, title, author));
            }

        }catch (Exception exp){
            exp.printStackTrace();
        }
        return list;
    }
    //爬取json数据
    public static List<GitHubPullRequest> getFirstPageOfPullRequestsByGitHubApi(String repo){
        String url = "https://api.github.com/repos/"+ repo +"/pulls";
        List<GitHubPullRequest> list = new ArrayList<>();

        try{
            String urlContent = getUrlContent(url);
            List<Group> groups = JSON.parseArray(urlContent, Group.class);
            for (Group group : groups){
                String title = group.title;
                String author = group.user.login;
                int number = group.number;
                list.add(new GitHubPullRequest(number, title, author));
            }

        }catch (Exception exp){
            exp.printStackTrace();
        }

        return list;
    }
    //解析json格式用的类
    private static class Group{
        private String title;
        private int number;
        public User user;

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public int getNumber() {
            return number;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public User getUser() {
            return user;
        }
    }
    private static class User{
        private String login;

        public void setLogin(String login) {
            this.login = login;
        }

        public String getLogin() {
            return login;
        }
    }

    private static String getUrlContent(String url)throws Exception{
        StringBuilder result = new StringBuilder(1024*1024);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response1 = httpclient.execute(httpGet);
        try {
            HttpEntity entity1;
            entity1 = response1.getEntity();
            // do something useful with the response body
            // and ensure it is fully consumed
            InputStream is = entity1.getContent();
            //从Stream到string，要利用工具这个方法
            //网络上传递的是字节流，如果变成字符，需要转字符流？
//            result.append(IOUtils.toString(is, "utf-8"));
            result.append(toString(is));
            is.close();
        }catch (Exception exp){
            exp.printStackTrace();
        }finally {
            response1.close();
        }
        return result.toString();
    }

    private static String toString(InputStream is)throws Exception{
        StringBuilder sb = new StringBuilder();
        byte[] bytes = new byte[1024];
        int b=-1;
        while ( (b = is.read(bytes)) !=-1 ){
            sb.append(new String(bytes, 0, b));
        }
        return sb.toString();
    }
}
