package net.thumbtack.school.elections.dao;

import net.thumbtack.school.elections.exception.VoterException;
import net.thumbtack.school.elections.model.Voter;

import java.util.Map;
import java.util.Set;

public interface VoterDao {
    void insert(Voter voter) throws VoterException;
    void insertVotedVoter(Voter voter) throws VoterException;
    <T extends Voter> void insertToVotedAgainstAll(T voter);
    void update(Voter voter) throws VoterException;
    void removeVoter(Voter voter) throws VoterException;
    void removeTokenByLogin(String login) throws VoterException;
    Voter getVoterById(int id);
    Voter getVoterByLogin(String login) throws VoterException;
    String getTokenByLogin(String voterLogin);
    Voter getVoterByToken(String voterToken) throws VoterException;
    Map<String, Voter> getTokensAndVoters();
    Map<Integer, Voter> getVoters();
    Set<Voter> getVotedVoters();
    Set<Voter> getVotedAgainstAll();
}
