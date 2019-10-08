package net.thumbtack.school.elections.request;

import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Voter;

import java.util.Map;

public class LogoutDtoRequest {

    private String firstName;
    private String login;
    private String password;
    private String token;
    private String candidateToken;
    private Map<String, Voter> tokensAndVoters;

    public LogoutDtoRequest(String login, String token) {
        this.token = token;
        setLogin(login);
    }

    public LogoutDtoRequest(String firstName, String login, String password, String token) {
        this.firstName = firstName;
        this.login = login;
        this.password = password;
        this.token = token;
    }

    public LogoutDtoRequest() {

    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCandidateToken() {
        return candidateToken;
    }

    public void setCandidateToken(String candidateToken) {
        this.candidateToken = candidateToken;
    }

    public Map<String, Voter> getTokensAndVoters() {
        return tokensAndVoters;
    }

    public void setTokensAndVoters(Map<String, Voter> tokensAndVoters) {
        this.tokensAndVoters = tokensAndVoters;
    }

    protected boolean isEmpty(String value){
        return value == null || value.isEmpty();
    }

    public boolean validate() throws VoterException {
        if(isEmpty(login)){
            throw new VoterException(VoterErrorCode.VOTER_WRONG_LOGIN);
        }
        if (!isEmpty(candidateToken)){
            throw new VoterException(VoterErrorCode.VOTER_IS_CANDIDATE);
        }
        if(!tokensAndVoters.containsKey(token)){
            throw new VoterException(VoterErrorCode.OFFLINE_TOKEN);
        }
        if(isEmpty(token)){
            throw new VoterException(VoterErrorCode.VOTER_WRONG_TOKEN);
        }
        return true;
    }
}

