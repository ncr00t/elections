package net.thumbtack.school.elections.response;

public class LogoutDtoResponse {

    private String token;

    public LogoutDtoResponse() {

    }

    public LogoutDtoResponse(String token) {
        this.token = token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}

