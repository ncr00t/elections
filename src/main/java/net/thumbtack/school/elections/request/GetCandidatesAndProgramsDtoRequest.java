package net.thumbtack.school.elections.request;

import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Voter;
import java.util.Map;

public class GetCandidatesAndProgramsDtoRequest{

    private String token;
    private Map<String, Voter> tokensAndVoters;

    public GetCandidatesAndProgramsDtoRequest(String token) {
        this.token = token;
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
