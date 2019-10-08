package net.thumbtack.school.elections.model;

import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;

import java.util.*;

public class Candidate extends Voter implements Comparable<Candidate>{

    private Set<Offer> electionProgram;
    private int amountVotes;

    public Candidate() {

    }

    public Candidate (int id, String firstName, String login, String password, Set<Offer> offers, Set<Offer> electionProgram) {
        super(id, firstName, login, password,  offers);
        this.electionProgram = electionProgram;
    }

    public Candidate (int id, String firstName, String login, String password) {
        this(id, firstName, login, password,  new HashSet<>(), new HashSet<>());
    }

    public Candidate (String firstName, String login, String password) {
        this(0, firstName, login, password,  new HashSet<>(), new HashSet<>());
    }

    public Set<Offer> getElectionProgram() {
        return electionProgram;
    }

    public void addOfferToElectionProgram(Offer offer){
        electionProgram.add(offer);
    }

    public void removeOfferFromElectionProgram(String candidateLogin, Offer offer) throws VoterException {
        if(!candidateLogin.equals(offer.getAuthorName())){
            electionProgram.remove(offer);
        }else {
            throw new VoterException(VoterErrorCode.CANDIDATE_CANNOT_REMOVE_YOURSELF_OFFER);
        }
    }

    public boolean isContainsToElectionProgram(Offer offer){
      for (Offer offer1 : electionProgram){
          if(offer.equals(offer1)){
              return true;
          }
      }
      return false;
    }

    public int getAmountVotes() {
        return amountVotes;
    }

    public void addVote() {
        amountVotes++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Candidate)) return false;
        if (!super.equals(o)) return false;
        Candidate candidate = (Candidate) o;
        return getAmountVotes() == candidate.getAmountVotes() &&
                Objects.equals(getElectionProgram(), candidate.getElectionProgram());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getElectionProgram(), getAmountVotes());
    }

    @Override
    public int compareTo(Candidate candidate) {
        if(amountVotes < candidate.getAmountVotes()){
            return 1;
        }else if(amountVotes > candidate.getAmountVotes()){
            return -1;
        }
        return 0;
    }
}
