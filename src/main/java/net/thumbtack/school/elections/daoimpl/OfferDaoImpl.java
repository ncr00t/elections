package net.thumbtack.school.elections.daoimpl;

import net.thumbtack.school.elections.dao.OfferDao;
import net.thumbtack.school.elections.database.DataBase;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Offer;
import net.thumbtack.school.elections.model.Voter;

import java.util.Map;
import java.util.Set;

public class OfferDaoImpl implements OfferDao {

    private DataBase dataBase;

    public OfferDaoImpl() {
        this.dataBase = DataBase.getDataBase();
    }

    @Override
    public void insertOffer(int voterId, Offer offer) throws VoterException {
        dataBase.insertOfferToVoterOffers(voterId, offer);
    }

    @Override
    public void insertOfferToElectionProgram(int candidateId, Offer offer) throws VoterException {
        dataBase.insertOfferToElectionProgram(candidateId, offer);
    }

    @Override
    public Map<Offer, Integer> getOffersAndAverageRatings() throws VoterException {
       return dataBase.getOffersAndAverageRatings();
    }

    @Override
    public Set<Offer> getOffersSortedByAverageRatings() throws VoterException {
      return dataBase.getOffersSortedByAverageRatings();
    }

    @Override
    public Offer changeOfferRating(Voter voter, Voter author, int rating) throws VoterException {
       return dataBase.changeOfferRating(voter, author, rating);
    }

    @Override
    public Offer removeOfferRating(Voter voter, Voter author) throws VoterException {
       return dataBase.removeOfferRating(voter, author);
     }

    @Override
    public void removeOfferFromElectionProgram(int candidateId, Offer offer) throws VoterException {
        dataBase.removeOfferFromElectionProgram(candidateId, offer);
    }
}
