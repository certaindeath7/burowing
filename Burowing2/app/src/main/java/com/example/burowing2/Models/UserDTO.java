package com.example.burowing2.Models;

public class UserDTO {

    private String  uid;


    private String userName;
    private String postURL;

    public UserDTO(String uID, String userName)
    {
        this.uid = uID;
        this.userName = userName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public UserDTO()
    {

    }



    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

   
}
