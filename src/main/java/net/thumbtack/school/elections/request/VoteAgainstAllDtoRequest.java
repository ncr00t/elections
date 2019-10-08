package net.thumbtack.school.elections.request;

import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Voter;
import java.util.Map;

public class VoteAgainstAllDtoRequest {

    private Voter voter;
    private String votingToken;
    private Map<String, Voter> tokensAndVoters;

    public VoteAgainstAllDtoRequest(String votingToken, Voter voter) {
        this.votingToken = votingToken;
        this.voter = voter;
    }

    public String getVotingToken() {
        return votingToken;
    }

    public void setVotingToken(String votingToken) {
        this.votingToken = votingToken;
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

    public Voter getVoter() {
        return voter;
    }

    public void setVoter(Voter voter) {
        this.voter = voter;
    }

    public boolean validate() throws VoterException {
        if(isEmpty(votingToken)){
            throw new VoterException(VoterErrorCode.VOTER_WRONG_TOKEN);
        }
        if(!tokensAndVoters.containsKey(votingToken)){
            throw new VoterException(VoterErrorCode.OFFLINE_TOKEN);
        }
        return true;
    }
}
