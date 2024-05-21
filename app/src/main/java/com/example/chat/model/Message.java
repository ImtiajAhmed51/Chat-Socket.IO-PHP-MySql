package com.example.chat.model;

public class Message extends User {

    private String messageId, messageReplyId, messageText, messagePhoto, messageTime;
    private boolean messageSeen, messageSent, messageUnsend;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageReplyId() {
        return messageReplyId;
    }

    public void setMessageReplyId(String messageReplyId) {
        this.messageReplyId = messageReplyId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessagePhoto() {
        return messagePhoto;
    }

    public void setMessagePhoto(String messagePhoto) {
        this.messagePhoto = messagePhoto;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public boolean isMessageSeen() {
        return messageSeen;
    }

    public void setMessageSeen(boolean messageSeen) {
        this.messageSeen = messageSeen;
    }

    public boolean isMessageSent() {
        return messageSent;
    }

    public void setMessageSent(boolean messageSent) {
        this.messageSent = messageSent;
    }

    public boolean isMessageUnsend() {
        return messageUnsend;
    }

    public void setMessageUnsend(boolean messageUnsend) {
        this.messageUnsend = messageUnsend;
    }

    public Message(User user,
                   String messageId,
                   String messageReplyId,
                   String messageText,
                   String messagePhoto,
                   String messageTime,
                   boolean messageSeen,
                   boolean messageSent,
                   boolean messageUnsend) {
        super(user.getId(), user.getUserId(), user.getUserDisplayName(), user.getUserName(), user.getUserPicture(), user.isUserVerified(), user.getUserRole(), user.getUserActiveStatus(), user.isUserSecurity());
        this.messageId = messageId;
        this.messageReplyId = messageReplyId;
        this.messageText = messageText;
        this.messagePhoto = messagePhoto;
        this.messageTime = messageTime;
        this.messageSeen = messageSeen;
        this.messageSent = messageSent;
        this.messageUnsend = messageUnsend;
    }

}
