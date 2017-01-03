package com.github.aarsy.loginviagithubapi;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by abhay yadav on 04-Dec-16.
 */

public class RepoModel {


    String repoId;
    String reponame;
    String description;
    String cloneUrl;
    String stargazers_count;
    String forks_count;
    String open_issues_count;
    public RepoModel(JSONObject jsonObject, Context context) {
        try {
            reponame = jsonObject.getString("name");
            repoId = jsonObject.getString("id");
            description= jsonObject.getString("description");
            cloneUrl= jsonObject.getString("clone_url");
            stargazers_count = jsonObject.getString("stargazers_count");
            forks_count= jsonObject.getString("forks_count");
            open_issues_count= jsonObject.getString("open_issues_count");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRepoId() {
        return repoId;
    }

    public void setRepoId(String repoId) {
        this.repoId = repoId;
    }

    public String getReponame() {
        return reponame;
    }

    public void setReponame(String reponame) {
        this.reponame = reponame;
    }

    public String getCloneUrl() {
        return cloneUrl;
    }

    public void setCloneUrl(String cloneUrl) {
        this.cloneUrl = cloneUrl;
    }

    public String getStargazers_count() {
        return stargazers_count;
    }

    public void setStargazers_count(String stargazers_count) {
        this.stargazers_count = stargazers_count;
    }

    public String getForks_count() {
        return forks_count;
    }

    public void setForks_count(String forks_count) {
        this.forks_count = forks_count;
    }

    public String getOpen_issues_count() {
        return open_issues_count;
    }

    public void setOpen_issues_count(String open_issues_count) {
        this.open_issues_count = open_issues_count;
    }
}
