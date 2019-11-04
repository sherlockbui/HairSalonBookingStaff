package com.example.hairsalonbookingstaff.Model;

public class MyNotification {
    private String  _id, title, content;
    private boolean read;

    public MyNotification() {
    }

    public String getTitle() {
        return title;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

}
