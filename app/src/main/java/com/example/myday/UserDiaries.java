package com.example.myday;

public class UserDiaries {
    public String day;
    public String content;
    public String weather;
    public String emoticon;
    public String fileuri;
    public String filesort;

    public UserDiaries(String day,String content,String weather,String emoticon,String fileuri,String filesort) {
        this.day = day;
        this.content = content;
        this.weather = weather;
        this.emoticon = emoticon;
        this.fileuri = fileuri;
        this.filesort = filesort;
    }
}
