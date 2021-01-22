package com.lei.bbs.bean;



public class UrlString extends Entity{

   private String url;

    public UrlString(String url){
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
