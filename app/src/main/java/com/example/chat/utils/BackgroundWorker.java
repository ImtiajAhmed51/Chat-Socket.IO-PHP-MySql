package com.example.chat.utils;

import android.os.AsyncTask;

import java.util.HashMap;

public class BackgroundWorker extends AsyncTask<String, String, String> {
    private final AsyncResponse asyncResponse;
    private final String[] listKey = {"userId", "userDisplayName", "userName", "userDob", "userEmail", "userNumber", "userGender", "userPassword", "userPicture", "receiverUserId", "userActiveStatus", "friendId"};

    public interface AsyncResponse {
        void onFinish(Object obj);
    }

    public BackgroundWorker(AsyncResponse asyncResponse) {
        this.asyncResponse = asyncResponse;
    }

    protected String doInBackground(String... params) {
        String type = params[0];
        String url = null;
        HashMap<String, String> data = new HashMap<>();
        RequestHandler requestHandler = new RequestHandler();
        switch (type) {
            case "CreateAnAccount":
                url = Config.CREATE_AN_ACCOUNT_URL;
                data.put(this.listKey[0], params[1]);
                data.put(this.listKey[1], params[2]);
                data.put(this.listKey[2], params[3]);
                data.put(this.listKey[3], params[4]);
                data.put(this.listKey[4], params[5]);
                data.put(this.listKey[5], params[6]);
                data.put(this.listKey[6], params[7]);
                data.put(this.listKey[7], params[8]);
                break;
            case "Login":
                url = Config.LOGIN_URL;
                data.put(this.listKey[4], params[1]);
                data.put(this.listKey[5], params[2]);
                data.put(this.listKey[7], params[3]);
                break;
            case "EmailNumberCheck":
                url = Config.EMAIL_NUMBER_CHECK_URL;
                data.put(this.listKey[4], params[1]);
                data.put(this.listKey[5], params[2]);
                break;
            case "UserNameCheck":
                url = Config.USERNAME_CHECK_URL;
                data.put(this.listKey[2], params[1]);
                break;
            case "ChangePicture":
                url = Config.CHANGE_PICTURE_URL;
                data.put(this.listKey[0], params[1]);
                data.put(this.listKey[8], params[2]);
                break;
            case "ShowAllUser":
                url = Config.SHOW_ALL_USER_URL;
                data.put(this.listKey[0], params[1]);
                break;
            case "FriendsShowAllUser":
                url = Config.FRIENDS_SHOW_ALL_USER_URL;
                data.put(this.listKey[0], params[1]);
                break;
            case "PendingRequestShowAllUser":
                url = Config.PENDING_REQUEST_ALL_USER_URL;
                data.put(this.listKey[0], params[1]);
                break;
            case "AddRequestUser":
                url = Config.ADD_REQUEST_USER_URL;
                data.put(this.listKey[0], params[1]);
                data.put(this.listKey[9], params[2]);
                break;
            case "UserActiveStatusChange":
                url = Config.USER_ACTIVE_STATUS_CHANGE_URL;
                data.put(this.listKey[0], params[1]);
                data.put(this.listKey[10], params[2]);
                break;
            case "PendingRequestCancelUser":
                url = Config.PENDING_REQUEST_CANCEL_USER_URL;
                data.put(this.listKey[11], params[1]);
                break;
            case "SentRequestShowAllUser":
                url = Config.SENT_REQUEST_ALL_USER_URL;
                data.put(this.listKey[0], params[1]);
                break;
            case "SentRequestCancelUser":
                url = Config.SENT_REQUEST_CANCEL_USER_URL;
                data.put(this.listKey[11], params[1]);
                break;

            case "SentRequestAcceptUser":
                url = Config.SENT_REQUEST_ACCEPT_USER_URL;
                data.put(this.listKey[11], params[1]);
                break;

        }
        return requestHandler.sendPostRequest(url, data);
    }

    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        this.asyncResponse.onFinish(s);
    }

    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }
}
