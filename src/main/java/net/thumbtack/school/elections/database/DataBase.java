package net.thumbtack.school.elections.database;

import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Candidate;
import net.thumbtack.school.elections.model.Offer;
import net.thumbtack.school.elections.model.Voter;
import java.util.*;

public class DataBase {

    private Map<Integer,Voter> voters;
    private Map<Integer, Candidate> candidates;
    private Map<String, Voter> tokensAndVoters;
    private Set<Voter> votedVoters;
    private Set<Candidate> votedCandidates;
    private Set<Voter> votedAgainstAll;
    private SortedSet<Candidate> rankedCandidates;

    private DataBase() {
        voters = new HashMap<>();
        candidates = new HashMap<>();
        tokensAndVoters = new HashMap<>();
        votedVoters = new HashSet<>();
        votedCandidates = new HashSet<>();
        votedAgainstAll = new HashSet<>();
        rankedCandidates = new TreeSet<>();
    }

    private static class DataBaseHolder{
        private static final DataBase DATA_BASE = new DataBase();
    }

    public static DataBase getDataBase(){
        return DataBaseHolder.DATA_BASE;
    }

    public <T extends Voter> void insertToVotedAgainstAll(T voter){
        votedAgainstAll.add(voter);
    }

    public Map<Integer,Voter> getVoters() {
        return voters;
    }

    public Map<Integer, Candidate> getCandidates() {
        return candidates;
    }

    public Map<String, Voter> getTokensAndVoters() {
        return tokensAndVoters;
    }

    public Set<Voter> getVotedVoters() {
        return votedVoters;
    }

    public Set<Candidate> getVotedCandidates() {
        return votedCandidates;
    }

    private boolean isExistLogin(String login, Map<Integer, ? extends Voter> map){
        for(Map.Entry<Integer, ? extends Voter> map1 : map.entrySet()){
            if(map1.getValue().getLogin().equals(login)){
                return true;
            }
        }
        return false;
    }

    public Voter getVoterById(int id){
        return voters.get(id);
    }

    public Candidate getCandidateById(int id){
        return candidates.get(id);
    }

    public String getTokenByLogin(String voterLogin, Map<String, ? extends Voter> map){
        String token = null;
        for(Map.Entry<String,? extends Voter> voters : map.entrySet()){
            Voter voter = voters.getValue();
            if(voter.getLogin().equals(voterLogin)){
                token = voters.getKey();
            }
        }
        return token;
    }

    public Voter getVoterByToken(String token) throws VoterException {
        if(tokensAndVoters.containsKey(token)){
            return voters.get(token);
        }else {
            throw new VoterException(VoterErrorCode.VOTER_NOT_FOUND_BY_TOKEN);
        }
    }

    public Candidate getCandidateByToken(String token) throws VoterException {
        if(tokensAndVoters.containsKey(token)){
            return candidates.get(token);
        }else {
            throw new VoterException(VoterErrorCode.CANDIDATE_NOT_FOUND_BY_TOKEN);
        }
    }

    public Map<Candidate, Set<Offer>> getCandidatesAndPrograms(){
        Map<Candidate, Set<Offer>> candidatesAndPrograms = new HashMap<>();
        if(!candidates.isEmpty()) {
            for (Candidate candidate : candidates.values()) {
                candidatesAndPrograms.put(candidate, candidate.getElectionProgram());
            }
        }
        return candidatesAndPrograms;
    }

    public Set<Offer> getAllOffers(){
        Set<Offer> allOffers = new HashSet<>();
        for(Voter voter : voters.values()){
            for(Offer offer : voter.getOffers()){
                allOffers.add(offer);
            }
        }
        return allOffers;
    }

    public Map<Offer, Integer> getOffersAndAverageRatings() throws VoterException {
        Map<Offer, Integer> offersAndAverageRatings = new TreeMap<>();
        for (Offer offer : getAllOffers()){
            offersAndAverageRatings.put(offer, offer.calculateAverageRating());
        }
        return offersAndAverageRatings;
    }

    public Set<Offer> getOffersSortedByAverageRatings() throws VoterException {
        return getOffersAndAverageRatings().keySet();
    }

    public String getTokenByVoterLogin(String login){
        return getTokenByLogin(login, tokensAndVoters);
    }

    public Voter getVoterByLogin(String login) throws VoterException {
        for(Voter voter : voters.values()){
            if(voter.getLogin().equals(login)){
                return voter;
            }
        }
        throw new VoterException(VoterErrorCode.VOTER_NOT_FOUND_BY_LOGIN);
    }

    public Voter getCandidateByLogin(String login) throws VoterException {
        for(Voter voter : candidates.values()){
            if(voter.getLogin().equals(login)){
                return voter;
            }
        }
        throw new VoterException(VoterErrorCode.VOTER_NOT_FOUND_BY_LOGIN);
    }

    public String getTokenByCandidateLogin(String login){
        return getTokenByLogin(login, tokensAndVoters);
    }

    public Set<Voter> getVotedAgainstAll() {
        return votedAgainstAll;
    }

    public SortedSet<Candidate> getRankedCandidates() {
        return rankedCandidates;
    }

    public void removeTokenByLogin(String login) throws VoterException{
        String token = getTokenByLogin(login, tokensAndVoters);
        if(token == null){
            throw new VoterException(VoterErrorCode.TOKEN_NOT_FOUND_BY_LOGIN);
        }
        tokensAndVoters.remove(getTokenByLogin(login, tokensAndVoters));
    }

    public void removeVoter(Voter voter) throws VoterException {
        if(!voters.containsKey(voter.getId())){
            throw new VoterException(VoterErrorCode.VOTER_NOT_FOUND_BY_ID);
        }
        voters.remove(voter.getId());
    }

    public void insertOfferToVoterOffers(int voterId, Offer offer) throws VoterException {
        if(voters.containsKey(voterId)){
            Voter voter = voters.get(voterId);
            voter.addOffer(offer);
        }else {
            throw new VoterException(VoterErrorCode.VOTER_NOT_FOUND_BY_ID);
        }
    }

    public void insertOfferToElectionProgram(int candidateId, Offer offer) throws VoterException {
        if(candidates.containsKey(candidateId)){
            Candidate candidate = candidates.get(candidateId);
            candidate.addOfferToElectionProgram(offer);
        }else {
            throw new VoterException(VoterErrorCode.CANDIDATE_NOT_FOUND_BY_ID);
        }
    }

    public void removeOfferFromElectionProgram(int candidateId, Offer offer) throws VoterException {
        if(candidates.containsKey(candidateId)){
            Candidate candidate = candidates.get(candidateId);
            candidate.removeOfferFromElectionProgram(candidate.getLogin(), offer);
        }else {
            throw new VoterException(VoterErrorCode.CANDIDATE_NOT_FOUND_BY_TOKEN);
        }
    }

    public Offer changeOfferRating(Voter voter, Voter author, int rating) throws VoterException {
        Offer changedOffer = voter.changeOfferRating(author, rating);
        return changedOffer;
    }

    public Offer removeOfferRating(Voter voter, Voter author) throws VoterException {
        Offer deletedOffer = voter.removeOfferRating(author);
        return deletedOffer;
    }

    public <T extends Voter> void insert(T voter, Map<Integer, T> map) throws VoterException {
        if(!isExistLogin(voter.getLogin(), map)){
            map.put(voter.getId(), voter);
        } else {
            throw new VoterException(VoterErrorCode.DUPLICATE_VOTER_LOGIN);
        }
    }

    public void insertVoter(Voter voter) throws VoterException {
       insert(voter, voters);
    }

    public <T extends Voter> void update(T voter, Map<Integer, T> map) {
        map.put(voter.getId(), voter);
    }

    public void updateVoter(Voter voter) throws VoterException{
        if(voters.containsKey(voter.getId())) {
            update(voter, voters);
        }else {
            throw new VoterException(VoterErrorCode.VOTER_NOT_FOUND_BY_ID);
        }
    }

    public void insertCandidate(Candidate candidate) throws VoterException {
        insert(candidate, candidates);
    }

    public void removeCandidate(Candidate candidate) throws VoterException {
        if(candidates.containsKey(candidate.getId())) {
            candidates.remove(candidate.getId());
        }else {
            throw new VoterException(VoterErrorCode.CANDIDATE_NOT_FOUND_BY_ID);
        }
    }

    public void insertVotedVoter(Voter voter) throws VoterException {
        if(votedVoters.contains(voter)){
            throw new VoterException(VoterErrorCode.VOTER_CANNOT_VOTE_TWICE);
        }
        votedVoters.add(voter);
    }

    public void insertVotedCandidate(Candidate candidate) throws VoterException {
        if(votedCandidates.contains(candidate)){
            throw new VoterException(VoterErrorCode.CANDIDATE_CANNOT_VOTE_TWICE);
        }
        votedCandidates.add(candidate);
    }

    public void insertToRankedCandidates(Candidate candidate){
        rankedCandidates.add(candidate);
    }

    public boolean isContainsWithLogin(String login, Set<Voter> voters){
        for(Voter voter : voters){
            if(voter.getLogin().equals(login)){
                return true;
            }
        }
        return false;
    }

    public boolean isContainsWithLogin(String login, Map<Integer, ? extends Voter> voters){
        for(Voter voter : voters.values()){
            if(voter.getLogin().equals(login)){
                return true;
            }
        }
        return false;
    }
}
