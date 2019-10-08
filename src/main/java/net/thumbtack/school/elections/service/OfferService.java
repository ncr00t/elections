package net.thumbtack.school.elections.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.thumbtack.school.elections.dao.OfferDao;
import net.thumbtack.school.elections.daoimpl.CandidateDaoImpl;
import net.thumbtack.school.elections.daoimpl.OfferDaoImpl;
import net.thumbtack.school.elections.daoimpl.VoterDaoImpl;
import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Candidate;
import net.thumbtack.school.elections.model.Offer;
import net.thumbtack.school.elections.model.Voter;
import net.thumbtack.school.elections.request.*;
import net.thumbtack.school.elections.response.GetOffersAndAverageRatingsDtoResponse;
import net.thumbtack.school.elections.response.GetOffersSortedByAverageRatingsDtoResponse;
import java.util.Map;
import java.util.Set;

public class OfferService {

    private OfferDao offerDao;
    private VoterDaoImpl voterDao;
    private CandidateDaoImpl candidateDao;
    private Gson gson;

    public OfferService() {
        offerDao = new OfferDaoImpl();
        candidateDao = new CandidateDaoImpl();
        voterDao = new VoterDaoImpl();
        gson = new Gson();
    }

    public String addOffer(String requestJsonString){
        try {
            AddOfferDtoRequest addOfferDtoRequest = gson.fromJson(requestJsonString, AddOfferDtoRequest.class);
            if (addOfferDtoRequest.validate()){
                Offer offer = new Offer(addOfferDtoRequest.getOfferDescription());

                Voter voter = voterDao.getVoterById(addOfferDtoRequest.getVoterId());
                offer.addVoterAndRating(voter, offer.getMAX_RATING());
                offer.setAuthorName(voter.getLogin());
                offerDao.insertOffer(voter.getId(), offer);

                Candidate candidate = candidateDao.getCandidateById(addOfferDtoRequest.getCandidateId());
                offerDao.insertOfferToElectionProgram(candidate.getId(), offer);
                requestJsonString = gson.toJson("");
            }
        } catch (VoterException e) {
            requestJsonString = VoterErrorCode.ERROR_RESPONSE.getErrorString();
        }
        return requestJsonString;
    }

    public String addOfferToElectionProgram(String requestJsonString){
        try {
            AddOfferToElectionProgramDtoRequest addOfferToElectionProgramDtoRequest = gson.fromJson(requestJsonString, AddOfferToElectionProgramDtoRequest.class);
            if (addOfferToElectionProgramDtoRequest.validate()){
                Offer offer = new Offer(addOfferToElectionProgramDtoRequest.getOfferDescription());
                int candidateId = addOfferToElectionProgramDtoRequest.getCandidateId();
                Candidate candidate = candidateDao.getCandidateById(candidateId);
                offerDao.insertOfferToElectionProgram(candidate.getId(), offer);
                requestJsonString = gson.toJson("");
            }
        } catch (VoterException e) {
            requestJsonString = VoterErrorCode.ERROR_RESPONSE.getErrorString();
        }
        return requestJsonString;
    }

    public String removeOfferFromElectionProgram(String requestJsonString){
        try {
            RemoveOfferFromElectionProgramDtoRequest removeOfferFromElectionProgramDtoRequest = gson.fromJson(requestJsonString, RemoveOfferFromElectionProgramDtoRequest.class);
            if (removeOfferFromElectionProgramDtoRequest.validate()){
                Offer offer = new Offer(removeOfferFromElectionProgramDtoRequest.getOfferDescription());
                String candidateToken = removeOfferFromElectionProgramDtoRequest.getCandidateToken();
                Candidate candidate = candidateDao.getCandidates().get(candidateToken);
                offerDao.removeOfferFromElectionProgram(candidate.getId(), offer);
                requestJsonString = gson.toJson("");
            }
        } catch (VoterException e) {
            requestJsonString = VoterErrorCode.ERROR_RESPONSE.getErrorString();
        }
        return requestJsonString;
    }

    public String changeOfferRating(String requestJsonString){
        try {
            ChangeOfferRatingDtoRequest changeOfferRatingDtoRequest = gson.fromJson(requestJsonString, ChangeOfferRatingDtoRequest.class);
            if (changeOfferRatingDtoRequest.validate()){
                Voter voter = changeOfferRatingDtoRequest.getVoter();
                Voter author = changeOfferRatingDtoRequest.getAuthor();
                int rating = changeOfferRatingDtoRequest.getRating();
                offerDao.changeOfferRating(voter, author, rating);
                requestJsonString = gson.toJson("");
            }
        } catch (VoterException e) {
            requestJsonString = VoterErrorCode.ERROR_RESPONSE.getErrorString();
        }
        return requestJsonString;
    }

    public String removeOfferRating(String requestJsonString){
        try {
            RemoveOfferRatingDtoRequest removeOfferRatingDtoRequest = gson.fromJson(requestJsonString, RemoveOfferRatingDtoRequest.class);
            if (removeOfferRatingDtoRequest.validate()){
                Voter voter = removeOfferRatingDtoRequest.getVoter();
                Voter author = removeOfferRatingDtoRequest.getAuthor();
                offerDao.removeOfferRating(voter, author);
                requestJsonString = gson.toJson("");
            }
        } catch (VoterException e) {
            requestJsonString = VoterErrorCode.ERROR_RESPONSE.getErrorString();
        }
        return requestJsonString;
    }

    public String getOffersAndAverageRatings(String requestJsonString){
        try {
            GetOffersAndAverageRatingsDtoRequest offersSortedAverageRatingsDtoRequest = gson.fromJson(requestJsonString, GetOffersAndAverageRatingsDtoRequest.class);
            offersSortedAverageRatingsDtoRequest.setTokensAndVoters(voterDao.getTokensAndVoters());
            if (offersSortedAverageRatingsDtoRequest.validate()){
                Map<Offer, Integer> offersSortedByAverageRatings = offerDao.getOffersAndAverageRatings();
                GetOffersAndAverageRatingsDtoResponse offersSortedByAverageRatingsDtoResponse = new GetOffersAndAverageRatingsDtoResponse(offersSortedByAverageRatings);
                requestJsonString = new GsonBuilder().enableComplexMapKeySerialization()
                                                     .create()
                                                     .toJson(offersSortedByAverageRatingsDtoResponse);
            }
        } catch (VoterException e) {
            requestJsonString = VoterErrorCode.ERROR_RESPONSE.getErrorString();
        }
        return requestJsonString;
    }

    public String getOffersSortedByAverageRatings(String requestJsonString){
        try {
            GetOffersSortedByAverageRatingsDtoRequest offersSortedByAverageRatingsDtoRequest = gson.fromJson(requestJsonString, GetOffersSortedByAverageRatingsDtoRequest.class);
            offersSortedByAverageRatingsDtoRequest.setTokensAndVoters(voterDao.getTokensAndVoters());
            if (offersSortedByAverageRatingsDtoRequest.validate()){
                Set<Offer> offersSortedByAverageRatings = offerDao.getOffersSortedByAverageRatings();
                GetOffersSortedByAverageRatingsDtoResponse offersSortedByAverageRatingsDtoResponse = new GetOffersSortedByAverageRatingsDtoResponse(offersSortedByAverageRatings);
                requestJsonString = gson.toJson(offersSortedByAverageRatingsDtoResponse);
            }
        } catch (VoterException e) {
            requestJsonString = VoterErrorCode.ERROR_RESPONSE.getErrorString();
        }
        return requestJsonString;
    }
}
