package com.example.burowing2.Models;

public class MessageDTO {
    private String receiver;
    private String sender;
    private String content;

   public MessageDTO(String receiver, String sender, String content)
    {
        this.receiver = receiver;
        this.sender = sender;
        this.content = content;
    }

    public MessageDTO()
    {

    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
