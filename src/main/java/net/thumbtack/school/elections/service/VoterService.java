package net.thumbtack.school.elections.service;

import com.google.gson.Gson;
import net.thumbtack.school.elections.dao.CandidateDao;
import net.thumbtack.school.elections.daoimpl.CandidateDaoImpl;
import net.thumbtack.school.elections.daoimpl.VoterDaoImpl;
import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Offer;
import net.thumbtack.school.elections.request.*;
import net.thumbtack.school.elections.model.Voter;
import net.thumbtack.school.elections.response.*;
import java.util.Map;
import java.util.Set;

public class VoterService {

    private VoterDaoImpl voterDao;
    private CandidateDao candidateDao;
    private Gson gson;

    public VoterService() {
        this.voterDao = new VoterDaoImpl();
        this.candidateDao = new CandidateDaoImpl();
        gson = new Gson();
    }

    public String registerVoter(String requestJsonString) {
        try {
            RegisterVoterDtoRequest registerVoterDtoRequest = gson.fromJson(requestJsonString,RegisterVoterDtoRequest.class);
            if (registerVoterDtoRequest.validate()) {
                Voter voter = new Voter(registerVoterDtoRequest.getId(),
                                        registerVoterDtoRequest.getFirstName(),
                                        registerVoterDtoRequest.getLogin(),
                                        registerVoterDtoRequest.getPassword());
                voterDao.insert(voter);
                String newToken = GenerateTokenService.generateNewToken();
                voterDao.getTokensAndVoters().put(newToken, voter);
                RegisterVoterDtoResponse registerVoterDtoResponse = new RegisterVoterDtoResponse(newToken);
                requestJsonString = gson.toJson(registerVoterDtoResponse);
            }
        } catch (VoterException e) {
            requestJsonString = VoterErrorCode.ERROR_RESPONSE.getErrorString();
        }
        return requestJsonString;
    }

    public String logout(String requestJsonString){
        try {
            LogoutDtoRequest logoutDtoRequest = gson.fromJson(requestJsonString, LogoutDtoRequest.class);
            Map<String, Voter> tokensAndVoters = voterDao.getTokensAndVoters();
            logoutDtoRequest.setTokensAndVoters(tokensAndVoters);
            String candidateToken = candidateDao.getTokenByLogin(logoutDtoRequest.getLogin());
            logoutDtoRequest.setCandidateToken(candidateToken);
            if (logoutDtoRequest.validate()){
                tokensAndVoters.remove(logoutDtoRequest.getToken());
                String voterToken = voterDao.getTokenByLogin(logoutDtoRequest.getLogin());
                Voter voter = voterDao.getVoterByToken(voterToken);
                if(voter.isExistOffers()) {
                    for (Offer offer : voter.getOffers()) {
                        offer.setAuthorName("Society");
                        offer.removeOfferRating(voter);
                    }
                }
                voterDao.update(voter);
                LogoutDtoResponse logoutDtoResponse = new LogoutDtoResponse(logoutDtoRequest.getToken());
                requestJsonString = gson.toJson(logoutDtoResponse);
            }
        } catch (VoterException e) {
            requestJsonString = VoterErrorCode.ERROR_RESPONSE.getErrorString();
        }
        return requestJsonString;
    }

    public String login(String requestJsonString){
        try {
            LoginDtoRequest loginDtoRequest = gson.fromJson(requestJsonString, LoginDtoRequest.class);
            Map<String, Voter> tokensAndVoters = voterDao.getTokensAndVoters();
            loginDtoRequest.setTokensAndVoters(tokensAndVoters);

            Map<Integer, Voter> voters = voterDao.getVoters();
            loginDtoRequest.setVoters(voters);

            String token = voterDao.getTokenByLogin(loginDtoRequest.getLogin());
            loginDtoRequest.setToken(token);

            Voter voter = tokensAndVoters.get(token);
            loginDtoRequest.setVoter(voter);

            if (loginDtoRequest.validate()){
                if(voter.isExistOffers()) {
                    for (Offer offer : voter.getOffers()) {
                        offer.setAuthorName(voter.getLogin());
                    }
                }
                voterDao.update(voter);
                String newToken = GenerateTokenService.generateNewToken();
                LoginDtoResponse loginDtoResponse = new LoginDtoResponse(newToken);
                tokensAndVoters.put(newToken, voter);
                requestJsonString = gson.toJson(loginDtoResponse);
            }
        } catch (VoterException e) {
            requestJsonString = VoterErrorCode.ERROR_RESPONSE.getErrorString();
        }
        return requestJsonString;
    }

    public String getVotedVoters(String requestJsonString){
        try {
            GetVotedVotersDtoRequest getVotedVotersDtoRequest = gson.fromJson(requestJsonString, GetVotedVotersDtoRequest.class);
            getVotedVotersDtoRequest.setTokensAndVoters(voterDao.getTokensAndVoters());
            if (getVotedVotersDtoRequest.validate()) {
                Set<Voter> votedVoters = voterDao.getVotedVoters();
                GetVotedVotersDtoResponse getVotedVotersDtoResponse = new GetVotedVotersDtoResponse(votedVoters);
                requestJsonString = gson.toJson(getVotedVotersDtoResponse);
            }
        } catch (VoterException e) {
            requestJsonString = VoterErrorCode.ERROR_RESPONSE.getErrorString();
        }
        return requestJsonString;
    }

    public String addVotedVoter(String requestJsonString){
        try {
            AddVotedVoterTokenDtoRequest addVotedVoterTokenDtoRequest = gson.fromJson(requestJsonString, AddVotedVoterTokenDtoRequest.class);
            addVotedVoterTokenDtoRequest.setTokensAndVoters(voterDao.getTokensAndVoters());
            if (addVotedVoterTokenDtoRequest.validate()){
                String voterToken = addVotedVoterTokenDtoRequest.getVoterToken();
                Voter voter = voterDao.getVoterByToken(voterToken);
                voterDao.insertVotedVoter(voter);
                requestJsonString = gson.toJson("");
            }
        } catch (VoterException e) {
            requestJsonString = VoterErrorCode.ERROR_RESPONSE.getErrorString();
        }
        return requestJsonString;
    }
}