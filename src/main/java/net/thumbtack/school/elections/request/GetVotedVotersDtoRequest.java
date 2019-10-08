package net.thumbtack.school.elections.request;

import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Voter;
import java.util.Map;

public class GetVotedVotersDtoRequest {

    private String voterToken;
    private Map<String, Voter> tokensAndVoters;

    public GetVotedVotersDtoRequest(String candidateToken) {
        this.voterToken = candidateToken;
    }

    public String getCandidateToken() {
        return voterToken;
    }

    public void setCandidateToken(String candidateToken) {
        this.voterToken = candidateToken;
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
        return isEmpty(voterToken);
    }

    public boolean validate() throws VoterException {
        if(!tokensAndVoters.containsKey(voterToken)){
            throw new VoterException(VoterErrorCode.OFFLINE_TOKEN);
        }
        if(isEmptyToken()){
            throw new VoterException(VoterErrorCode.VOTER_WRONG_TOKEN);
        }
        return true;
    }
}
