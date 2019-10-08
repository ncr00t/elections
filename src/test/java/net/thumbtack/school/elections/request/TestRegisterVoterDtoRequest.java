package net.thumbtack.school.elections.request;

import net.thumbtack.school.elections.exception.VoterErrorCode;
import net.thumbtack.school.elections.exception.VoterException;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestRegisterVoterDtoRequest {

    @Test
    public void testRegisterVoterDtoRequest() throws VoterException {
        RegisterVoterDtoRequest registerVoterDtoRequest = new RegisterVoterDtoRequest(0,"ivan", "ivanov", "12345678");
        assertEquals("ivan", registerVoterDtoRequest.getFirstName());
        assertEquals("ivanov", registerVoterDtoRequest.getLogin());
        assertEquals("12345678", registerVoterDtoRequest.getPassword());

        registerVoterDtoRequest.setFirstName("petr");
        assertEquals("petr", registerVoterDtoRequest.getFirstName());

        registerVoterDtoRequest.setLogin("petrov");
        assertEquals("petrov", registerVoterDtoRequest.getLogin());

        registerVoterDtoRequest.setPassword("123456789");
        assertEquals("123456789", registerVoterDtoRequest.getPassword());
    }

    @Test
    public void testValidateWrongFirstName() {
        try {
            RegisterVoterDtoRequest registerVoterDtoRequest1 = new RegisterVoterDtoRequest(0, null, "ivanov", "12345678");
            registerVoterDtoRequest1.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_FIRSTNAME, ex.getVoterErrorCode());
        }

        try {
            RegisterVoterDtoRequest registerVoterDtoRequest2 = new RegisterVoterDtoRequest(0, "", "ivanov", "12345678");
            registerVoterDtoRequest2.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_FIRSTNAME, ex.getVoterErrorCode());
        }

        try {
            RegisterVoterDtoRequest registerVoterDtoRequest3 = new RegisterVoterDtoRequest(0, "ivan", "ivanov", "12345678");
            registerVoterDtoRequest3.setFirstName(null);
            registerVoterDtoRequest3.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_FIRSTNAME, ex.getVoterErrorCode());
        }

        try {
            RegisterVoterDtoRequest registerVoterDtoRequest4 = new RegisterVoterDtoRequest(0, "ivan", "ivanov", "12345678");
            registerVoterDtoRequest4.setFirstName("");
            registerVoterDtoRequest4.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_FIRSTNAME, ex.getVoterErrorCode());
        }
    }

    @Test
    public void testValidateWrongLogin() {
        try {
            RegisterVoterDtoRequest registerVoterDtoRequest1 = new RegisterVoterDtoRequest(0, "ivan", null, "12345678");
            registerVoterDtoRequest1.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_LOGIN, ex.getVoterErrorCode());
        }

        try {
            RegisterVoterDtoRequest registerVoterDtoRequest2 = new RegisterVoterDtoRequest(0, "ivan", "", "12345678");
            registerVoterDtoRequest2.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_LOGIN, ex.getVoterErrorCode());
        }

        try {
            RegisterVoterDtoRequest registerVoterDtoRequest3 = new RegisterVoterDtoRequest(0, "ivan", "ivanov", "12345678");
            registerVoterDtoRequest3.setLogin(null);
            registerVoterDtoRequest3.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_LOGIN, ex.getVoterErrorCode());
        }

        try {
            RegisterVoterDtoRequest registerVoterDtoRequest4 = new RegisterVoterDtoRequest(0, "ivan", "ivanov", "12345678");
            registerVoterDtoRequest4.setLogin("");
            registerVoterDtoRequest4.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_LOGIN, ex.getVoterErrorCode());
        }
    }

    @Test
    public void testValidateWrongPassword() {
        try {
            RegisterVoterDtoRequest registerVoterDtoRequest1 = new RegisterVoterDtoRequest(0, "ivan", "ivanov", null);
            registerVoterDtoRequest1.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_PASSWORD, ex.getVoterErrorCode());
        }

        try {
            RegisterVoterDtoRequest registerVoterDtoRequest2 = new RegisterVoterDtoRequest(1,"ivan", "ivanov", "");
            registerVoterDtoRequest2.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_PASSWORD, ex.getVoterErrorCode());
        }

        try {
            RegisterVoterDtoRequest registerVoterDtoRequest3 = new RegisterVoterDtoRequest(2, "ivan", "ivanov", "12345678");
            registerVoterDtoRequest3.setPassword(null);
            registerVoterDtoRequest3.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_PASSWORD, ex.getVoterErrorCode());
        }

        try {
            RegisterVoterDtoRequest registerVoterDtoRequest4 = new RegisterVoterDtoRequest(3, "ivan", "ivanov", "12345678");
            registerVoterDtoRequest4.setPassword("");
            registerVoterDtoRequest4.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_WRONG_PASSWORD, ex.getVoterErrorCode());
        }
    }

    @Test
    public void testValidateShortPassword() {
        try {
            RegisterVoterDtoRequest registerVoterDtoRequest1 = new RegisterVoterDtoRequest(0, "ivan", "ivanov", "123456");
            registerVoterDtoRequest1.validate();
            fail();
        } catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_SHORT_PASSWORD, ex.getVoterErrorCode());
        }

        try {
            RegisterVoterDtoRequest registerVoterDtoRequest4 = new RegisterVoterDtoRequest(1, "ivan", "ivanov", "12345678");
            registerVoterDtoRequest4.setPassword("123456");
            registerVoterDtoRequest4.validate();
            fail();
        }  catch (VoterException ex) {
            assertEquals(VoterErrorCode.VOTER_SHORT_PASSWORD, ex.getVoterErrorCode());
        }
    }
}
