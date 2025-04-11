package hexa.org.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import hexa.org.dao.CustomerService;
import hexa.org.entity.Customer;
import hexa.org.exception.AuthenticationException;

public class CustomerServiceTest {

    @Test
    public void testInvalidLoginShouldFail() {
        CustomerService service = new CustomerService();
        assertThrows(AuthenticationException.class, () -> {
            service.authenticateCustomer("wronguser", "wrongpass");
        });
    }
    
    
}


