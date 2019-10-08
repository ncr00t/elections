package net.thumbtack.school.elections.request;

import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Voter;
import java.util.Map;

public class AddVotedVoterTokenDtoRequest{

    private String voterToken;
    private Map<String, Voter> tokensAndVoters;

    public AddVotedVoterTokenDtoRequest(String candidateToken) {
        this.voterToken = candidateToken;
    }

    public String getVoterToken() {
        return voterToken;
    }

    public void setVoterToken(String voterToken) {
        this.voterToken = voterToken;
    }

    public Map<String, Voter> getTokensAndVoters() {
        return tokensAndVoters;
    }

    public void setTokensAndVoters(Map<String, Voter> tokensAndVoters) {
        this.tokensAndVoters = tokensAndVoters;
    }

    protected boolean isEmpty(String value){
        return value == null || value.equals("");
    }

    public boolean validate() throws VoterException {
        if(isEmpty(voterToken)){
            throw new VoterException(VoterErrorCode.VOTER_WRONG_TOKEN);
        }
        if(!tokensAndVoters.containsKey(voterToken)){
            throw new VoterException(VoterErrorCode.OFFLINE_TOKEN);
        }
        return true;
    }
}
