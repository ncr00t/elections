package net.thumbtack.school.elections.response;

public class RegisterVoterDtoResponse {

    private String token;

    public RegisterVoterDtoResponse(String token) {
        this.token = token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
