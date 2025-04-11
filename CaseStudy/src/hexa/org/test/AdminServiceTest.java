package hexa.org.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import hexa.org.dao.AdminService;
import hexa.org.entity.Admin;
import hexa.org.exception.InvalidInputException;

public class AdminServiceTest {
	@Test
    public void testInvalidLoginShouldFail() {
        AdminService service = new AdminService();
        assertThrows(InvalidInputException.class, () -> {
            service.authenticateAdmin("wronguser", "wrongpass");
        });
    }
	
	@Test
	public void testGetAdminByUsername() {
	    AdminService service = new AdminService();
	    Admin admin = service.getAdminByUsername("Vishnu");  

	    assertNotNull(admin);
	    assertEquals("Vishnu", admin.getUsername());
	}	

}

