package hexa.org.dao;

import java.sql.SQLException;

import hexa.org.entity.*;
import hexa.org.exception.*;

public interface ICustomerService {
	boolean registerCustomer(Customer customer) throws InvalidInputException, DatabaseConnectionException;

	boolean authenticateCustomer(String username,String password) throws AuthenticationException;
	
	public Customer getCustomerById(int customerId);
	
	public Customer getCustomerByUsername(String username);
	
	public void updateCustomer(String username) throws SQLException;
	
	public void deleteCustomer(String username);
	
	public void update_Customer(String username, String field, String newValue);
	
	
}

