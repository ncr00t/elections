package net.thumbtack.school.elections.request;

import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Voter;
import java.util.Map;

public class AddCandidateDtoRequest extends RegisterVoterDtoRequest {

    private String token;
    private Map<String, Voter> tokensAndVoters;

    public AddCandidateDtoRequest(int id, String firstName, String login, String password, String token) {
        super(id, firstName, login, password);
        this.token = token;
    }

    public AddCandidateDtoRequest(String firstName, String login, String password, String token) {
        this(0, firstName, login, password, token);
    }

    public AddCandidateDtoRequest(String token) {
        this.token = token;
    }

    public AddCandidateDtoRequest() {

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
