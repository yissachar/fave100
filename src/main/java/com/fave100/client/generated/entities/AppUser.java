/*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*
* WARNING: THIS IS A GENERATED FILE. ANY CHANGES YOU
* MAKE WILL BE LOST THE NEXT TIME THIS FILE IS GENERATED
*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*/

package com.fave100.client.generated.entities;

import java.util.List;

public class AppUser {

    private boolean critic;
    private List<String> hashtags;
    private String avatarImage;
    private boolean admin;
    private String username;

    public boolean isCritic() {
        return this.critic;
    }

    public void setCritic(boolean critic){
        this.critic = critic;
    }

    public List<String> getHashtags() {
        return this.hashtags;
    }

    public void setHashtags(List<String> hashtags){
        this.hashtags = hashtags;
    }

    public String getAvatarImage() {
        return this.avatarImage;
    }

    public void setAvatarImage(String avatarImage){
        this.avatarImage = avatarImage;
    }

    public boolean isAdmin() {
        return this.admin;
    }

    public void setAdmin(boolean admin){
        this.admin = admin;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username){
        this.username = username;
    }

}