package com.boardclub.eventcalendar.dto;

public class UserDto {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;

    private String telegram;

    public String getTelegram() {
        return telegram;
    }

    public void setTelegram(String telegram) {
        if (telegram != null && telegram.startsWith("@")) {
            this.telegram = telegram.substring(1); // удаляем '@'
        } else {
            this.telegram = telegram;
        }
    }

    // геттеры и сеттеры
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}