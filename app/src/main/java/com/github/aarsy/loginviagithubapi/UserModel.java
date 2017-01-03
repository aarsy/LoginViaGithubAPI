package com.github.aarsy.loginviagithubapi;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by abhay yadav on 04-Dec-16.
 */

public class UserModel {
    String username;
    String id;
    String avatar_url;
    String user_url;
    String profile_html_url;
    String followers_url;
    String following_url;
    String gists_url;
    String starred_url;
    String subscriptions_url;
    String organizations_url;
    String repos_url;
    String events_url;
    String received_events_url;
    String location;

    String email;
    String bio;
    String public_repos;
    String followers;
    String following;
    String name;

    public UserModel(JSONObject jsonObject, Context context) {
        try {
            username = jsonObject.getString("login");
            id = jsonObject.getString("id");
            avatar_url = jsonObject.getString("avatar_url");
            user_url = jsonObject.getString("url");
            profile_html_url = jsonObject.getString("html_url");
            followers_url = jsonObject.getString("followers_url");
            following_url = jsonObject.getString("following_url");
            gists_url = jsonObject.getString("gists_url");
            starred_url = jsonObject.getString("starred_url");
            subscriptions_url = jsonObject.getString("subscriptions_url");
            organizations_url = jsonObject.getString("organizations_url");
            repos_url = jsonObject.getString("repos_url");
            events_url = jsonObject.getString("events_url");
            received_events_url = jsonObject.getString("received_events_url");
            name = jsonObject.getString("name");
            location = jsonObject.getString("location");
            email = jsonObject.getString("email");
            bio = jsonObject.getString("bio");
            public_repos = jsonObject.getString("public_repos");
            followers = jsonObject.getString("followers");
            following = jsonObject.getString("following");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }



    public String getProfile_html_url() {
        return profile_html_url;
    }

    public void setProfile_html_url(String profile_html_url) {
        this.profile_html_url = profile_html_url;
    }

    public String getFollowers_url() {
        return followers_url;
    }

    public void setFollowers_url(String followers_url) {
        this.followers_url = followers_url;
    }

    public String getFollowing_url() {
        return following_url;
    }

    public void setFollowing_url(String following_url) {
        this.following_url = following_url;
    }

    public String getGists_url() {
        return gists_url;
    }

    public void setGists_url(String gists_url) {
        this.gists_url = gists_url;
    }

    public String getStarred_url() {
        return starred_url;
    }

    public void setStarred_url(String starred_url) {
        this.starred_url = starred_url;
    }

    public String getSubscriptions_url() {
        return subscriptions_url;
    }

    public void setSubscriptions_url(String subscriptions_url) {
        this.subscriptions_url = subscriptions_url;
    }

    public String getOrganizations_url() {
        return organizations_url;
    }

    public void setOrganizations_url(String organizations_url) {
        this.organizations_url = organizations_url;
    }

    public String getRepos_url() {
        return repos_url;
    }

    public void setRepos_url(String repos_url) {
        this.repos_url = repos_url;
    }

    public String getEvents_url() {
        return events_url;
    }

    public void setEvents_url(String events_url) {
        this.events_url = events_url;
    }

    public String getReceived_events_url() {
        return received_events_url;
    }

    public void setReceived_events_url(String received_events_url) {
        this.received_events_url = received_events_url;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPublic_repos() {
        return public_repos;
    }

    public void setPublic_repos(String public_repos) {
        this.public_repos = public_repos;
    }

    public String getFollowers() {
        return followers;
    }

    public void setFollowers(String followers) {
        this.followers = followers;
    }

    public String getFollowing() {
        return following;
    }

    public void setFollowing(String following) {
        this.following = following;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
