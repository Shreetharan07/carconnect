package hexa.org.dao;

import java.sql.SQLException;

import hexa.org.entity.Admin;
import hexa.org.exception.AdminNotFoundException;
import hexa.org.exception.DatabaseConnectionException;
import hexa.org.exception.InvalidInputException;

public interface IAdminService {
	
	boolean authenticateAdmin(String username,String password)throws InvalidInputException,SQLException;
	
	public boolean registerAdmin(Admin admin) throws InvalidInputException, DatabaseConnectionException ;
	
	public void updateAdmin(String username) throws SQLException, AdminNotFoundException;
	
	public Admin getAdminByUsername(String username);
	
	public Admin getAdminById(int adminId);
	
	public void deleteAdmin(String username);

}

