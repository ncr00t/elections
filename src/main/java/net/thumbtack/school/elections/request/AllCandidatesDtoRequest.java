package net.thumbtack.school.elections.request;

import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Voter;

import java.util.Map;

public class AllCandidatesDtoRequest extends AddCandidateDtoRequest{

    private String firstName;
    private String login;
    private String password;
    private String token;
    private Map<String, Voter> tokensAndVoters;

    public AllCandidatesDtoRequest(String firstName, String login, String password, String token) {
        this.firstName = firstName;
        this.login = login;
        this.password = password;
        this.token = token;
    }

    public AllCandidatesDtoRequest(String token) {
        this.token = token;
    }

    public AllCandidatesDtoRequest() {

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Map<String, Voter> getTokensAndVoters() {
        return tokensAndVoters;
    }

    public void setTokensAndVoters(Map<String, Voter> tokensAndVoters) {
        this.tokensAndVoters = tokensAndVoters;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLogin() {
        return login;
    }

    @Override
    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    protected boolean isEmpty(String value){
        return value == null || value.isEmpty();
    }

    private boolean isEmptyToken(){
        return isEmpty(token);
    }

    public boolean validate() throws VoterException {
        if(!tokensAndVoters.containsKey(token)){
            throw new VoterException(VoterErrorCode.OFFLINE_TOKEN);
        }
        if(isEmptyToken()){
            throw new VoterException(VoterErrorCode.VOTER_WRONG_TOKEN);
        }
        return true;
    }
}
