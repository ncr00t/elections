package net.thumbtack.school.elections.model;

import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import java.util.*;

public class Voter {

    private int id;
    private String firstName;
    private String login;
    private String password;
    private Set<Offer> offers;

    public Voter() {

    }

    public Voter(int id, String firstName, String login, String password, Set<Offer> offers) {
        this.id = id;
        this.firstName = firstName;
        this.login = login;
        this.password = password;
        this.offers = offers;
    }

    public Voter(int id, String firstName, String login, String password) {
        this(id, firstName, login, password,  new HashSet<>());
    }

    public Voter(String firstName, String login, String password) {
        this(0, firstName, login, password,  new HashSet<>());
    }

    public Voter(String firstName, String login, String password, Set<Offer> offers) {
        this(0, firstName, login, password,  offers);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Offer> getOffers() {
        return offers;
    }

    public boolean isExistOffers(){
        return !offers.isEmpty();
    }

    public void addOffer(Offer offer){
        offers.add(offer);
    }

    public Offer changeOfferRating(Voter author,int rating) throws VoterException {
        Offer changedOffer = null;
        if(!isAuthor(author.getLogin())){
            for(Offer offer : author.getOffers()){
                if(offer.getAuthorName().equals(author.getLogin())){
                    offer.changeOfferRating(author, rating);
                    changedOffer = offer;
                }
            }
        }else{
            throw new VoterException(VoterErrorCode.VOTER_CANNOT_CHANGE_YOURSELF_RATING);
        }
        return changedOffer;
    }

    public Offer removeOfferRating(Voter author) throws VoterException {
        Offer changedOffer = null;
        if(!isAuthor(author.getLogin())){
            for(Offer offer : author.getOffers()){
                offer.removeOfferRating(author);
                changedOffer = offer;
            }
        }else{
            throw new VoterException(VoterErrorCode.VOTER_CANNOT_REMOVE_YOURSELF_RATING);
        }
        return changedOffer;
    }

    public boolean isContainsOffer(Offer offer){
        for(Offer offer1 : offers){
            if(offer.equals(offer1)){
                return true;
            }
        }
        return false;
    }

    public boolean isAuthor(String authorName){
        return authorName.equals(login);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Voter)) return false;
        Voter voter = (Voter) o;
        return getId() == voter.getId() &&
                Objects.equals(getFirstName(), voter.getFirstName()) &&
                Objects.equals(getLogin(), voter.getLogin()) &&
                Objects.equals(getPassword(), voter.getPassword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getFirstName(), getLogin(), getPassword());
    }
}
