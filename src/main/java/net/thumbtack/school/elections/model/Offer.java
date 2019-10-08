package net.thumbtack.school.elections.model;

import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Offer implements Comparable<Offer>{

    private int id;
    private transient Map<Voter, Integer> votersAndRatings;
    private String authorName;
    private String description;
    private final int MAX_RATING = 5;

    public Offer(String description) {
        this(0, new HashMap<>(), null, description);
    }

    public Offer(int id, Map<Voter, Integer> votersAndRatings, String authorName, String description) {
        this.id = id;
        this.votersAndRatings = votersAndRatings;
        this.authorName = authorName;
        this.description = description;
    }

    public Offer(int id, String description) {
        this(id, new HashMap<>(), null, description);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMAX_RATING() {
        return MAX_RATING;
    }

    public Map<Voter, Integer> getVotersAndRatings() {
        return votersAndRatings;
    }

    public void addVoterAndRating(Voter voter, int rating) throws VoterException {
        if(rating > 0 && rating <= MAX_RATING) {
            votersAndRatings.put(voter, rating);
        }else {
            throw new VoterException(VoterErrorCode.OFFER_WRONG_RATING);
        }
    }

    public void changeOfferRating(Voter voter, int rating) throws VoterException {
        if(votersAndRatings.containsKey(voter)){
            addVoterAndRating(voter, rating);
        }else {
            throw new VoterException(VoterErrorCode.VOTER_NOT_SET_RATING);
        }
    }

    public <T extends Voter> void removeOfferRating(T voter) throws VoterException {
        if(votersAndRatings.containsKey(voter)){
            votersAndRatings.remove(voter);
        }else {
            throw new VoterException(VoterErrorCode.VOTER_NOT_SET_RATING);
        }
//        for(Map.Entry<Voter, Integer> voterAndRating : votersAndRatings.entrySet()){
//            if(voterAndRating.getKey().equals(voter)){
//                votersAndRatings.remove(voter);
//            }
//        }
//        throw new VoterException(VoterErrorCode.VOTER_NOT_SET_RATING);
    }

    public int getRatingByVoter(Voter voter) throws VoterException {
        for(Map.Entry<Voter, Integer> voterAndRating : votersAndRatings.entrySet()){
            if(voterAndRating.getKey().equals(voter)){
                return voterAndRating.getValue();
            }
        }
        throw new VoterException(VoterErrorCode.VOTER_NOT_SET_RATING);
    }

    public int calculateAverageRating() throws VoterException {
        if(votersAndRatings.size() == 0){
            throw new VoterException(VoterErrorCode.OFFER_WITHOUT_RATINGS);
        }
        int sum = votersAndRatings.values()
                                  .stream()
                                  .mapToInt(Integer::intValue)
                                  .sum();
        return sum / votersAndRatings.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Offer)) return false;
        Offer offer = (Offer) o;
        return getId() == offer.getId() &&
                getMAX_RATING() == offer.getMAX_RATING() &&
                Objects.equals(getVotersAndRatings(), offer.getVotersAndRatings()) &&
                Objects.equals(getAuthorName(), offer.getAuthorName()) &&
                Objects.equals(getDescription(), offer.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getVotersAndRatings(), getAuthorName(), getDescription(), getMAX_RATING());
    }

    @Override
    public int compareTo(Offer offer){
        try {
            if(calculateAverageRating() < offer.calculateAverageRating()){
                return 1;
            }else if(calculateAverageRating() > offer.calculateAverageRating()){
                return -1;
            }
        } catch (VoterException e) {
            e.printStackTrace();
        }
        return 0;
    }
}