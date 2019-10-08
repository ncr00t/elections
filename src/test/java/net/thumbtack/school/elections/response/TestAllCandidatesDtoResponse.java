package net.thumbtack.school.elections.response;

import net.thumbtack.school.elections.database.DataBase;
import net.thumbtack.school.elections.model.Candidate;
import org.junit.Before;
import org.junit.Test;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TestAllCandidatesDtoResponse {

    private DataBase dataBase;

    @Before
    public void setUp(){
        dataBase = DataBase.getDataBase();
        dataBase.getCandidates().clear();
    }

    @Test
    public void testAllCandidatesDtoRequest() {
        AllCandidatesDtoResponse allCandidatesDtoResponse = new AllCandidatesDtoResponse();
        Map<Integer, Candidate> candidates = dataBase.getCandidates();
        allCandidatesDtoResponse.setAllCandidates(candidates);
        assertEquals(0, allCandidatesDtoResponse.getAllCandidates().size());
    }
}
