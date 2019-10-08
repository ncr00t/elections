package net.thumbtack.school.elections.response;

import net.thumbtack.school.elections.database.DataBase;
import net.thumbtack.school.elections.model.Candidate;
import net.thumbtack.school.elections.model.Offer;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class TestGetCandidatesAndProgramsDtoResponse {

    private DataBase dataBase;

    @Before
    public void setUp(){
        dataBase = DataBase.getDataBase();
        dataBase.getVoters().clear();
    }

    @Test
    public void testGetCandidatesAndProgramsDtoResponse() {
        GetCandidatesAndProgramsDtoResponse candidatesAndProgramsDtoResponse = new GetCandidatesAndProgramsDtoResponse();
        Map<Candidate, Set<Offer>> candidatesAndPrograms = dataBase.getCandidatesAndPrograms();
        candidatesAndProgramsDtoResponse.setCandidatesAndPrograms(candidatesAndPrograms);
        assertEquals(0, candidatesAndProgramsDtoResponse.getCandidatesAndPrograms().size());
    }
}
