package net.thumbtack.school.elections.request;

import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Voter;
import java.util.Map;

public class VoteForCandidateDtoRequest {

    private int votingId;
    private int candidateId;
    private String votingToken;
    private String candidateToken;
    private Map<String, Voter> tokensAndVoters;

    public VoteForCandidateDtoRequest(int votingId, int candidateId, String votingToken, String candidateToken) {
        this.votingId = votingId;
        this.candidateId = candidateId;
        this.votingToken = votingToken;
        this.candidateToken = candidateToken;
    }

    public String getVotingToken() {
        return votingToken;
    }

    public void setVotingToken(String votingToken) {
        this.votingToken = votingToken;
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

    private boolean isEmpty(String value){
        return value == null || value.equals("");
    }

    public int getVotingId() {
        return votingId;
    }

    public void setVotingId(int votingId) {
        this.votingId = votingId;
    }

    public int getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }

    public boolean validate() throws VoterException {
        if(isEmpty(candidateToken)){
            throw new VoterException(VoterErrorCode.CANDIDATE_WRONG_TOKEN);
        }
        if(!tokensAndVoters.containsKey(votingToken)){
            throw new VoterException(VoterErrorCode.OFFLINE_TOKEN);
        }
        if(!tokensAndVoters.containsKey(candidateToken)){
            throw new VoterException(VoterErrorCode.OFFLINE_TOKEN);
        }
        return true;
    }
}
