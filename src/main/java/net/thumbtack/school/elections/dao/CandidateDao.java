package net.thumbtack.school.elections.dao;

import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Candidate;
import net.thumbtack.school.elections.model.Offer;
import net.thumbtack.school.elections.model.Voter;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

public interface CandidateDao {
    void remove(Candidate candidate) throws VoterException;
    String getTokenByLogin(String login);
    Candidate getCandidateByToken(String token) throws VoterException;
    void insert(Candidate candidate) throws VoterException;
    void insertVotedCandidate(Candidate candidate) throws VoterException;
    void insertToRankedCandidates(Candidate candidate);
    Voter getCandidateById(int id);
    Voter getCandidateByLogin(String login) throws VoterException;
    Map<Candidate, Set<Offer>> getCandidatesAndPrograms();
    Map<Integer, Candidate> getCandidates();
    Set<Candidate> getVotedCandidates();
    SortedSet<Candidate> getRankedCandidates();
}
