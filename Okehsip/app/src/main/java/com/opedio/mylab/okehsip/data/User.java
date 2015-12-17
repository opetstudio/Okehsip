package com.opedio.mylab.okehsip.data;

import java.io.Serializable;

/**
 * Created by 247 on 12/15/2015.
 */
public class User implements Serializable {
    private String fullName;
    private String email;
    private String password;
    private String confPassword;
    private String firstName;
    private String lastName;
    private String username;
    private String phone;
    private String objectId;
    public String sessionToken;
    private String gravatarId;
    private String avatarUrl;
    private String authTokenType;

    private String verificationCode;

    public String getAuthTokenType() {
        return authTokenType;
    }

    public void setAuthTokenType(String authTokenType) {
        this.authTokenType = authTokenType;
    }

    public String getConfPassword() {
        return confPassword;
    }

    public void setConfPassword(String confPassword) {
        this.confPassword = confPassword;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getGravatarId() {
        return gravatarId;
    }

    public void setGravatarId(String gravatarId) {
        this.gravatarId = gravatarId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
    public boolean isParamOk(){
        if(this.fullName != null && !"".equalsIgnoreCase(this.fullName)
                && this.email != null && !"".equalsIgnoreCase(this.email)
                && this.password != null && !"".equalsIgnoreCase(this.password))
            return true;
        return false;
    }
    public boolean isParamEmailOk(){
        if(
                this.email != null && !"".equalsIgnoreCase(this.email)
                )
            return true;
        return false;
    }
    public boolean isParamOk2(){
        if(this.email != null && !"".equalsIgnoreCase(this.email)
                && this.verificationCode != null && !"".equalsIgnoreCase(this.verificationCode))
            return true;
        return false;
    }
    public boolean isParamOk3(){
        if(this.email != null && !"".equalsIgnoreCase(this.email)
                && this.password != null && !"".equalsIgnoreCase(this.password))
            return true;
        return false;
    }
    public boolean isParamOk4(){
        if(this.email != null && !"".equalsIgnoreCase(this.email)
                && this.password != null && !"".equalsIgnoreCase(this.password)
                && this.confPassword != null && !"".equalsIgnoreCase(this.confPassword)
                && this.verificationCode != null && !"".equalsIgnoreCase(this.verificationCode)
                )
            return true;
        return false;
    }
    public boolean isConfPassOk(){
        if(
           this.password != null && !"".equalsIgnoreCase(this.password)
                && this.confPassword != null && !"".equalsIgnoreCase(this.confPassword)
                && this.confPassword.equals(this.password)
                )
            return true;
        return false;
    }
}
