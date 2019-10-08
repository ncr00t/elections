package net.thumbtack.school.elections.request;

import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Voter;
import java.util.Map;

public class LoginDtoRequest {

    private Voter voter;
    private Map<String, Voter> tokensAndVoters;
    private Map<Integer, Voter> voters;
    private String login;
    private String password;
    private String token;

    public LoginDtoRequest() {
    }

    public void setVoter(Voter voter) throws VoterException {
        if(isEmpty(voter)){
            throw new VoterException(VoterErrorCode.VOTER_NOT_FOUND_BY_TOKEN);
        } else {
            this.voter = voter;
        }
    }

    public Voter getVoter() {
        return voter;
    }

    public Map<Integer, Voter> getVoters() {
        return voters;
    }

    public void setVoters(Map<Integer, Voter> voters) {
        this.voters = voters;
    }

    public Map<String, Voter> getTokensAndVoters() {
        return tokensAndVoters;
    }

    public void setTokensAndVoters(Map<String, Voter> tokensAndVoters) {
        this.tokensAndVoters = tokensAndVoters;
    }

    public void setToken(String token) throws VoterException {
        if(isEmpty(token)) {
            throw new VoterException(VoterErrorCode.TOKEN_NOT_FOUND_BY_LOGIN);
        }else {
            this.token = token;
        }
    }

    public void setLogin(String login) throws VoterException {
        if(isEmpty(login)){
            throw new VoterException(VoterErrorCode.VOTER_WRONG_LOGIN);
        }else {
            this.login = login;
        }
    }

    public void setPassword(String password) throws VoterException {
        if(isEmpty(password)){
            throw new VoterException(VoterErrorCode.VOTER_WRONG_PASSWORD);
        }else {
            this.password = password;
        }
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }

    protected boolean isEmpty(String value) {
        return value == null || value.equals("");
    }

    public boolean isEmpty(Voter voter){
        return voter == null;
    }

    public boolean validate() throws VoterException {
        if(token != null && !voter.getPassword().equals(getPassword())){
            throw new VoterException(VoterErrorCode.PASSWORD_NOT_FOUND);
        }
        if(tokensAndVoters.containsKey(getToken())){
            throw new VoterException(VoterErrorCode.ACTIVE_TOKEN);
        }
        return true;
    }
}
