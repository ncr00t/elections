package net.thumbtack.school.elections.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.thumbtack.school.elections.dao.OfferDao;
import net.thumbtack.school.elections.dao.VoterDao;
import net.thumbtack.school.elections.daoimpl.CandidateDaoImpl;
import net.thumbtack.school.elections.daoimpl.OfferDaoImpl;
import net.thumbtack.school.elections.daoimpl.VoterDaoImpl;
import net.thumbtack.school.elections.database.DataBase;
import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Candidate;
import net.thumbtack.school.elections.model.Offer;
import net.thumbtack.school.elections.model.Voter;
import net.thumbtack.school.elections.request.*;
import net.thumbtack.school.elections.response.*;

import java.util.Map;
import java.util.Set;

public class CandidateService {

    private CandidateDaoImpl candidateDao;
    private VoterDao voterDao;
    private OfferDao offerDao;
    private DataBase dataBase;
    private Gson gson;

    public CandidateService() {
        candidateDao = new CandidateDaoImpl();
        voterDao = new VoterDaoImpl();
        offerDao = new OfferDaoImpl();
        dataBase = DataBase.getDataBase();
        gson = new Gson();
    }

    public String addCandidate(String requestJsonString){
        try {
            AddCandidateDtoRequest addCandidateDtoRequest = gson.fromJson(requestJsonString,AddCandidateDtoRequest.class);
            addCandidateDtoRequest.setTokensAndVoters(voterDao.getTokensAndVoters());
            if (addCandidateDtoRequest.validate()) {
                Candidate candidate = new Candidate(addCandidateDtoRequest.getId(),
                                                    addCandidateDtoRequest.getFirstName(),
                                                    addCandidateDtoRequest.getLogin(),
                                                    addCandidateDtoRequest.getPassword());
                Voter voter = voterDao.getVoterByLogin(addCandidateDtoRequest.getLogin());
                if(voter.isExistOffers()){
                    for(Offer offer : voter.getOffers()){
                        offerDao.insertOfferToElectionProgram(candidate.getId(), offer);
                    }
                }
                voterDao.removeVoter(voter);
                voterDao.removeTokenByLogin(voter.getLogin());
                candidateDao.insert(candidate);
                String newCandidateToken = GenerateTokenService.generateNewToken();
                voterDao.getTokensAndVoters().put(newCandidateToken, candidate);
                AddCandidateDtoResponse addCandidateDtoResponse = new AddCandidateDtoResponse(newCandidateToken);
                requestJsonString = gson.toJson(addCandidateDtoResponse);
            }
        }catch (VoterException ve){
            requestJsonString = VoterErrorCode.ERROR_RESPONSE.getErrorString();
        }
        return requestJsonString;
    }

    public String addVotedCandidate(String requestJsonString){
        try {
            AddVotedCandidateDtoRequest addVotedCandidateDtoRequest = gson.fromJson(requestJsonString, AddVotedCandidateDtoRequest.class);
            addVotedCandidateDtoRequest.setTokensAndVoters(voterDao.getTokensAndVoters());
            if (addVotedCandidateDtoRequest.validate()){
                int candidateId = addVotedCandidateDtoRequest.getCandidateId();
                Candidate candidate = candidateDao.getCandidateById(candidateId);
                candidateDao.insertVotedCandidate(candidate);
                requestJsonString = gson.toJson("");
            }
        } catch (VoterException e) {
            requestJsonString = VoterErrorCode.ERROR_RESPONSE.getErrorString();
        }
        return requestJsonString;
    }

    public String getCandidates(String requestJsonString){
        try {
            AllCandidatesDtoRequest allCandidatesDtoRequest = gson.fromJson(requestJsonString, AllCandidatesDtoRequest.class);
            allCandidatesDtoRequest.setTokensAndVoters(voterDao.getTokensAndVoters());
            if (allCandidatesDtoRequest.validate()) {
                Map<Integer, Candidate> allCandidates = candidateDao.getCandidates();
                AllCandidatesDtoResponse allCandidatesDtoResponse = new AllCandidatesDtoResponse(allCandidates);
                requestJsonString = gson.toJson(allCandidatesDtoResponse);
            }
        } catch (VoterException e) {
            requestJsonString = VoterErrorCode.ERROR_RESPONSE.getErrorString();
        }
        return requestJsonString;
    }

    public String getVotedCandidates(String requestJsonString){
        try {
            GetVotedCandidatesDtoRequest getVotedCandidatesDtoRequest = gson.fromJson(requestJsonString, GetVotedCandidatesDtoRequest.class);
            getVotedCandidatesDtoRequest.setTokensAndVoters(voterDao.getTokensAndVoters());
            if (getVotedCandidatesDtoRequest.validate()) {
                Set<Candidate> votedCandidates = candidateDao.getVotedCandidates();
                GetVotedCandidatesDtoResponse getVotedCandidatesDtoResponse = new GetVotedCandidatesDtoResponse(votedCandidates);
                requestJsonString = gson.toJson(getVotedCandidatesDtoResponse);
            }
        } catch (VoterException e) {
            requestJsonString = VoterErrorCode.ERROR_RESPONSE.getErrorString();
        }
        return requestJsonString;
    }

    public String removeCandidate(String requestJsonString){
        try {
            RemoveCandidateDtoRequest removeCandidateDtoRequest = gson.fromJson(requestJsonString, RemoveCandidateDtoRequest.class);
            removeCandidateDtoRequest.setTokensAndVoters(voterDao.getTokensAndVoters());
            removeCandidateDtoRequest.setCandidates(candidateDao.getCandidates());
            int candidateId = removeCandidateDtoRequest.getCandidateId();
            if (removeCandidateDtoRequest.validate()) {
                Candidate candidate = candidateDao.getCandidateById(candidateId);
                Voter voter = new Voter(candidate.getFirstName(), candidate.getLogin(),
                                        candidate.getPassword(), candidate.getOffers());
                for(Offer offer : candidate.getOffers()){
                    offerDao.insertOffer(voter.getId(), offer);
                }
                candidateDao.remove(candidate);
                voterDao.removeTokenByLogin(candidate.getLogin());
                voterDao.insert(voter);
                Map<String, Voter> tokensAndVoters = voterDao.getTokensAndVoters();
                tokensAndVoters.put(GenerateTokenService.generateNewToken(), voter);
                requestJsonString = gson.toJson("");
            }
        }catch (VoterException ve){
            requestJsonString = VoterErrorCode.ERROR_RESPONSE.getErrorString();
        }
        return requestJsonString;
    }

    public String getCandidatesAndPrograms(String requestJsonString) {
        GetCandidatesAndProgramsDtoRequest getCandidatesAndProgramsDtoRequest = gson.fromJson(requestJsonString, GetCandidatesAndProgramsDtoRequest.class);
        getCandidatesAndProgramsDtoRequest.setTokensAndVoters(voterDao.getTokensAndVoters());
        try {
            if(getCandidatesAndProgramsDtoRequest.validate()){
                Map<Candidate, Set<Offer>> candidatesAndPrograms = candidateDao.getCandidatesAndPrograms();
                GetCandidatesAndProgramsDtoResponse getCandidatesAndProgramsDtoResponse = new GetCandidatesAndProgramsDtoResponse(candidatesAndPrograms);
                requestJsonString = new GsonBuilder().enableComplexMapKeySerialization()
                                                     .create()
                                                     .toJson(getCandidatesAndProgramsDtoResponse);
            }
        } catch (VoterException e) {
            requestJsonString = VoterErrorCode.ERROR_RESPONSE.getErrorString();
        }
        return requestJsonString;
    }
}
