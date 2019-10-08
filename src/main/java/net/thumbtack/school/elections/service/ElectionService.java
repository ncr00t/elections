package net.thumbtack.school.elections.service;

import com.google.gson.Gson;
import net.thumbtack.school.elections.daoimpl.CandidateDaoImpl;
import net.thumbtack.school.elections.daoimpl.VoterDaoImpl;
import net.thumbtack.school.elections.database.DataBase;
import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Candidate;
import net.thumbtack.school.elections.model.Voter;
import net.thumbtack.school.elections.request.GetElectionResultsDtoRequest;
import net.thumbtack.school.elections.request.VoteAgainstAllDtoRequest;
import net.thumbtack.school.elections.request.VoteForCandidateDtoRequest;
import net.thumbtack.school.elections.response.GetElectionResultsDtoResponse;
import java.util.SortedSet;

public class ElectionService {

    private VoterDaoImpl voterDao;
    private CandidateDaoImpl candidateDao;
    private Gson gson;

    public ElectionService() {
        voterDao = new VoterDaoImpl();
        candidateDao = new CandidateDaoImpl();
        gson = new Gson();
    }

    public String voteForCandidate(String requestJsonString){
        try {
            VoteForCandidateDtoRequest voteForCandidateDtoRequest = gson.fromJson(requestJsonString, VoteForCandidateDtoRequest.class);
            voteForCandidateDtoRequest.setTokensAndVoters(voterDao.getTokensAndVoters());
            if (voteForCandidateDtoRequest.validate()){
                int votingId = voteForCandidateDtoRequest.getVotingId();
                int candidateId = voteForCandidateDtoRequest.getCandidateId();
                Candidate candidate = candidateDao.getCandidateById(candidateId);
                if(candidate.getElectionProgram().isEmpty()) {
                    throw new VoterException(VoterErrorCode.CANDIDATE_WITHOUT_ELECTION_PROGRAM);
                }
                if (voterDao.getVoters().containsKey(votingId)) {
                    Voter voter = voterDao.getVoterById(votingId);
                    voterDao.insertVotedVoter(voter);
                }
                if(candidateDao.getCandidates().containsKey(votingId)){
                    Candidate votingCandidate = candidateDao.getCandidateById(votingId);
                    if(votingCandidate.getElectionProgram().isEmpty()){
                        throw new VoterException(VoterErrorCode.CANDIDATE_WITHOUT_ELECTION_PROGRAM);
                    }
                    if(!voterDao.getVoterById(votingId).equals(candidateId)){
                        candidateDao.insertVotedCandidate(votingCandidate);
                    }else throw new VoterException(VoterErrorCode.CANDIDATE_CANNOT_VOTE_FOR_YOURSELF);
                }
                candidate.addVote();
                candidateDao.insertToRankedCandidates(candidate);
                requestJsonString = gson.toJson("");
            }
        } catch (VoterException e) {
            requestJsonString = VoterErrorCode.ERROR_RESPONSE.getErrorString();
        }
        return requestJsonString;
    }

    public String voteAgainstAll(String requestJsonString){
        try {
            VoteAgainstAllDtoRequest voteAgainstAllDtoRequest = gson.fromJson(requestJsonString, VoteAgainstAllDtoRequest.class);
            voteAgainstAllDtoRequest.setTokensAndVoters(voterDao.getTokensAndVoters());
            DataBase dataBase = DataBase.getDataBase();
            if (voteAgainstAllDtoRequest.validate()){
                Voter voter = voteAgainstAllDtoRequest.getVoter();
                if (dataBase.isContainsWithLogin(voter.getLogin(), voterDao.getVoters())) {
                    if(dataBase.isContainsWithLogin(voter.getLogin(), dataBase.getVotedVoters()) || dataBase.isContainsWithLogin(voter.getLogin(), dataBase.getVotedAgainstAll())){
                        throw new VoterException(VoterErrorCode.VOTER_CANNOT_VOTE_TWICE);
                    }
                    voterDao.insertToVotedAgainstAll(voter);
                }
                if(dataBase.isContainsWithLogin(voter.getLogin(), candidateDao.getCandidates())){
                    Candidate votingCandidate = candidateDao.getCandidateById(voter.getId());
                    if(votingCandidate.getElectionProgram().isEmpty()){
                        throw new VoterException(VoterErrorCode.CANDIDATE_WITHOUT_ELECTION_PROGRAM);
                    }
                    if(dataBase.isContainsWithLogin(voter.getLogin(), dataBase.getVotedVoters()) || dataBase.isContainsWithLogin(voter.getLogin(), dataBase.getVotedAgainstAll())){
                        throw new VoterException(VoterErrorCode.CANDIDATE_CANNOT_VOTE_TWICE);
                    }
                    voterDao.insertToVotedAgainstAll(votingCandidate);
                }
                requestJsonString = gson.toJson("");
            }
        } catch (VoterException e) {
            requestJsonString = VoterErrorCode.ERROR_RESPONSE.getErrorString();
        }
        return requestJsonString;
    }

    public String getElectionResults(String requestJsonString){
        try {
            GetElectionResultsDtoRequest getElectionResultsDtoRequest = gson.fromJson(requestJsonString, GetElectionResultsDtoRequest.class);
            getElectionResultsDtoRequest.setTokensAndVoters(voterDao.getTokensAndVoters());
            if (getElectionResultsDtoRequest.validate()){
                int amountVotesAgainstAll = voterDao.getVotedAgainstAll().size();
                SortedSet<Candidate> rankedCandidates = candidateDao.getRankedCandidates();
                if(rankedCandidates.isEmpty() && voterDao.getVotedAgainstAll().isEmpty()){
                    throw new VoterException(VoterErrorCode.VOTING_HAS_NOT_STARTED);
                }
                if(!rankedCandidates.isEmpty()){
                    Candidate candidateWithMostVotes = rankedCandidates.first();
                    if(candidateWithMostVotes.getAmountVotes() > amountVotesAgainstAll){
                        GetElectionResultsDtoResponse getElectionResultsDtoResponse = new GetElectionResultsDtoResponse(candidateWithMostVotes);
                        requestJsonString = gson.toJson(getElectionResultsDtoResponse);
                    }else throw new VoterException(VoterErrorCode.ELECTIONS_DECLARED_INVALID);
                } else throw new VoterException(VoterErrorCode.ELECTIONS_DECLARED_INVALID);
            }
        } catch (VoterException e) {
            requestJsonString = VoterErrorCode.ERROR_RESPONSE.getErrorString();
        }
        return requestJsonString;
    }
}
