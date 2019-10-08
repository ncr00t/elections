package net.thumbtack.school.elections.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TestCandidate {

    @Test
    public void testCandidate() {
        Candidate candidate = new Candidate("ivan", "ivanov", "12345678");
        assertEquals("ivan", candidate.getFirstName());
        assertEquals("ivanov", candidate.getLogin());
        assertEquals("12345678", candidate.getPassword());
        candidate.setFirstName("petr");
        assertEquals("petr", candidate.getFirstName());
        candidate.setLogin("petrov");
        assertEquals("petrov", candidate.getLogin());
        candidate.setPassword("123456789");
        assertEquals("123456789", candidate.getPassword());
    }

    @Test
    public void testEqualsCandidate() {
        Candidate candidate1 = new Candidate("ivan", "ivanov", "12345678");
        Candidate candidate2 = new Candidate("ivan", "ivanov", "12345678");
        Candidate candidate3 = new Candidate("petr", "petrov", "123456789");
        assertEquals(candidate1, candidate2);
        assertNotEquals(candidate1, candidate3);
    }

}
