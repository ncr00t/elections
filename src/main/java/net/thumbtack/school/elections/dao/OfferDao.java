package net.thumbtack.school.elections.dao;

import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Candidate;
import net.thumbtack.school.elections.model.Offer;
import net.thumbtack.school.elections.model.Voter;

import java.util.Map;
import java.util.Set;

public interface OfferDao {
    void insertOffer(int voterId, Offer offer) throws VoterException;
    void insertOfferToElectionProgram(int candidateId, Offer offer) throws VoterException;
    Offer changeOfferRating(Voter voter, Voter author, int rating) throws VoterException;
    Offer removeOfferRating(Voter voter, Voter author) throws VoterException;
    void removeOfferFromElectionProgram(int candidateId, Offer offer) throws VoterException;
    Map<Offer, Integer> getOffersAndAverageRatings() throws VoterException;
    Set<Offer> getOffersSortedByAverageRatings() throws VoterException;
}
