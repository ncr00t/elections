package net.thumbtack.school.elections.request;

import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Voter;
import java.util.Map;

public class GetVotedCandidatesDtoRequest {

    private String candidateToken;
    private Map<String, Voter> tokensAndVoters;

    public GetVotedCandidatesDtoRequest(String candidateToken) {
        this.candidateToken = candidateToken;
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

    private boolean isEmptyToken(){
        return isEmpty(candidateToken);
    }

    public boolean validate() throws VoterException {
        if(!tokensAndVoters.containsKey(candidateToken)){
            throw new VoterException(VoterErrorCode.OFFLINE_TOKEN);
        }
        if(isEmptyToken()){
            throw new VoterException(VoterErrorCode.CANDIDATE_WRONG_TOKEN);
        }
        return true;
    }
}
