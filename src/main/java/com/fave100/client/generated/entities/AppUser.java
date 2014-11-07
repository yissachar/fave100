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

    private String avatarImage;
    private String username;
    private boolean admin;
    private List<String> hashtags;
    private boolean critic;

    public String getAvatarImage() {
        return this.avatarImage;
    }

    public void setAvatarImage(String avatarImage){
        this.avatarImage = avatarImage;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public boolean isAdmin() {
        return this.admin;
    }

    public void setAdmin(boolean admin){
        this.admin = admin;
    }

    public List<String> getHashtags() {
        return this.hashtags;
    }

    public void setHashtags(List<String> hashtags){
        this.hashtags = hashtags;
    }

    public boolean isCritic() {
        return this.critic;
    }

    public void setCritic(boolean critic){
        this.critic = critic;
    }

}