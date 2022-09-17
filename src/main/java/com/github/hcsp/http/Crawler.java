package com.github.hcsp.http;

import java.net.URISyntaxException;
import java.util.List;
import java.io.IOException;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;


public class Crawler {
    static class User {
        public String login;

    }

    static class OriginalPullRequest {
        @JsonProperty("number")
        int number;
        // Pull request的标题
        @JsonProperty("title")
        String title;

        @JsonProperty("user")
        User user;

    }

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
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            final HttpGet httpget = new HttpGet("https://api.github.com/repos/" + repo + "/pulls");
            System.out.println("Executing request " + httpget.getMethod() + " " + httpget.getUri());
            // Create a custom response handler
            final HttpClientResponseHandler<String> responseHandler = response -> {
                final int status = response.getCode();
                if (status >= HttpStatus.SC_SUCCESS && status < HttpStatus.SC_REDIRECTION) {
                    final HttpEntity entity = response.getEntity();
                    try {
                        if (entity == null) {
                            return null;
                        }
                        String ret = EntityUtils.toString(entity);
                        EntityUtils.consume(entity);
                        return ret;
                    } catch (final ParseException ex) {
                        throw new ClientProtocolException(ex);
                    }
                } else {
                    throw new ClientProtocolException("Unexpected response status: " + status);
                }
            };
            final String responseBody = httpclient.execute(httpget, responseHandler);
            return convertResponseBodyToList(responseBody);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

    public static List<GitHubPullRequest> convertResponseBodyToList(String entity) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper.readValue(entity, new TypeReference<List<OriginalPullRequest>>() {
        }).stream().map(item -> new GitHubPullRequest(item.number, item.title, item.user.login)).collect(Collectors.toList());
    }
}
