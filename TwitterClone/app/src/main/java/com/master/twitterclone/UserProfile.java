package com.master.twitterclone;

/**
 * Created by ADMIN on 8/11/2016.
 */
class UserProfile
{
    public String Name;
    public String Age;
    public String PictureUrl;
    public UserProfile()
    {
    }
    public UserProfile(String Name,String Age,String PictureUrl)
    {
        this.Name = Name;
        this.Age = Age;
        this.PictureUrl = PictureUrl;
    }
}