package net.thumbtack.school.elections.request;

import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Candidate;
import net.thumbtack.school.elections.model.Voter;
import java.util.Map;

public class RemoveCandidateDtoRequest {

    private int candidateId;
    private String candidateToken;
    private Map<String, Voter> tokensAndVoters;
    private Map<Integer, Candidate> candidates;

    public RemoveCandidateDtoRequest(int candidateId, String candidateToken) {
        this.candidateId = candidateId;
        this.candidateToken = candidateToken;
    }

    public int getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }

    public Map<String, Voter> getTokensAndVoters() {
        return tokensAndVoters;
    }

    public void setTokensAndVoters(Map<String, Voter> tokensAndVoters) {
        this.tokensAndVoters = tokensAndVoters;
    }

    public String getCandidateToken() {
        return candidateToken;
    }

    public void setCandidateToken(String candidateToken) {
        this.candidateToken = candidateToken;
    }

    public Map<Integer, Candidate> getCandidates() {
        return candidates;
    }

    public void setCandidates(Map<Integer, Candidate> candidates) {
        this.candidates = candidates;
    }

    public boolean validate() throws VoterException {
        if(!tokensAndVoters.containsKey(candidateToken)){
            throw new VoterException(VoterErrorCode.OFFLINE_TOKEN);
        }
        if(!candidates.containsKey(candidateToken)){
            throw new VoterException(VoterErrorCode.CANDIDATE_NOT_FOUND_BY_TOKEN);
        }
        return true;
    }
}
