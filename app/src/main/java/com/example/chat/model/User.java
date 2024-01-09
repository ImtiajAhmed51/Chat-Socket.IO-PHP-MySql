package com.example.chat.model;

public class User {
    private String userId, userDisplayName, mainVal, userPassword, userDob, userEmail, userNumber, userPicture, userName, userGender, lastMessage, userAccountOpenTime, lastTimeMessage,userRole;
    private boolean isOnline, userVerified, userSecurity;
    private boolean isButtonEnabled;
    public User user;


    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public boolean isButtonEnabled() {
        return isButtonEnabled;
    }

    public void setButtonEnabled(boolean buttonEnabled) {
        isButtonEnabled = buttonEnabled;
    }

    public User(String userId) {
        this.userId = userId;
    }

    public User(String userId,
                String userDisplayName,
                String userName,
                String userPicture,
                Boolean userVerified, Boolean isButtonEnabled) {
        this.userId = userId;
        this.userDisplayName = userDisplayName;
        this.userName = userName;
        this.userPicture = userPicture;
        this.userVerified = userVerified;
        this.isButtonEnabled = isButtonEnabled;

    }


    public User(String userId,
                String userDisplayName,
                String userName,
                String userDob,
                String userEmail,
                String userNumber,
                String userPicture,
                String userGender,
                Boolean userVerified,
                String userAccountOpenTime,
                Boolean userSecurity,
                String userRole) {

        this.userId = userId;
        this.userDisplayName = userDisplayName;
        this.userName = userName;
        this.userDob = userDob;
        this.userEmail = userEmail;
        this.userNumber = userNumber;
        this.userPicture = userPicture;
        this.userGender = userGender;
        this.userVerified = userVerified;
        this.userAccountOpenTime = userAccountOpenTime;
        this.userSecurity = userSecurity;
        this.userRole=userRole;

    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public String getMainVal() {
        return mainVal;
    }

    public void setMainVal(String mainVal) {
        this.mainVal = mainVal;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserDob() {
        return userDob;
    }

    public void setUserDob(String userDob) {
        this.userDob = userDob;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

    public String getUserPicture() {
        return userPicture;
    }

    public void setUserPicture(String userPicture) {
        this.userPicture = userPicture;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getUserAccountOpenTime() {
        return userAccountOpenTime;
    }

    public void setUserAccountOpenTime(String userAccountOpenTime) {
        this.userAccountOpenTime = userAccountOpenTime;
    }

    public String getLastTimeMessage() {
        return lastTimeMessage;
    }

    public void setLastTimeMessage(String lastTimeMessage) {
        this.lastTimeMessage = lastTimeMessage;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public boolean isUserVerified() {
        return userVerified;
    }

    public void setUserVerified(boolean userVerified) {
        this.userVerified = userVerified;
    }

    public boolean isUserSecurity() {
        return userSecurity;
    }

    public void setUserSecurity(boolean userSecurity) {
        this.userSecurity = userSecurity;
    }

    @Override
    public boolean equals(Object anObject) {
        if (!(anObject instanceof User)) {
            return false;
        }
        return ((User) anObject).getUserId().equals(getUserId());
    }
    public boolean allEquals(Object obj){
        if (!(obj instanceof User)) {
            return false;
        }

        User otherUser = (User) obj;
        return  getUserId().equals(otherUser.getUserId())
                &&  getUserDisplayName().equals(otherUser.getUserDisplayName())
                && getUserName().equals(otherUser.getUserName())
                && getUserDob().equals(otherUser.getUserDob())
                && getUserEmail().equals(otherUser.getUserEmail())
                && getUserNumber().equals(otherUser.getUserNumber())
                && getUserAccountOpenTime().equals(otherUser.getUserAccountOpenTime())
                && getUserPicture().equals(otherUser.getUserPicture())
                && getUserGender().equals(otherUser.getUserGender())
                && isUserVerified()==otherUser.isUserVerified()
                && isUserSecurity()==otherUser.isUserSecurity()
                && getUserRole().equals(otherUser.getUserRole());

    }

    public boolean customEqual(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        User otherUser = (User) obj;

        // Compare individual fields for equality
        return userId.equals(otherUser.userId)
                && userName.equals(otherUser.userName)
                && userPicture.equals(otherUser.userPicture)
                && userVerified == otherUser.userVerified;
    }
}
