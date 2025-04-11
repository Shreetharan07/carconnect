package hexa.org.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import hexa.org.entity.*;
import hexa.org.exception.*;
import hexa.org.util.*;

public class CustomerService implements ICustomerService{

	// The registerCustomer method is used for customer sign up
	@Override
	public boolean registerCustomer(Customer customer) throws InvalidInputException, DatabaseConnectionException {
		boolean flag=false;
	    if (customer == null || customer.getUsername() == null || customer.getPassword() == null) {
	        throw new InvalidInputException("Invalid input for customer registration.");
	    }

	    try {
	    	Connection conn=DBConnUtil.getDbConnection();
	        String sql = "INSERT INTO Customer (firstName, lastName, email, phoneNumber, address, userName, password, registrationDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
	        PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	        stmt.setString(1, customer.getFirstName());
	        stmt.setString(2, customer.getLastName());
	        stmt.setString(3, customer.getEmail());
	        stmt.setString(4, customer.getPhoneNumber());
	        stmt.setString(5, customer.getAddress());
	        stmt.setString(6, customer.getUsername());
	        stmt.setString(7, customer.getPassword());
	        stmt.setString(8, customer.getRegistrationDate());

	        int rowsInserted = stmt.executeUpdate();

	        if (rowsInserted > 0) {
	            ResultSet generatedKeys = stmt.getGeneratedKeys();
	            if (generatedKeys.next()) {
	                int customerId = generatedKeys.getInt(1); // Retrieve generated customer ID
	                customer.setCustomerId(customerId);       // Set it in POJO
	                System.out.println("Customer registered successfully. Your Customer ID is: " + customerId);
	                flag=true;
	            }
	        }
	        
	    }
	    
	        catch (SQLException e) {
	        	throw new DatabaseConnectionException("Error while registering customer: " + e.getMessage());
	    }
	    return flag;
	}
	
	// --------------------------------------------------------------------------------------------------------

	// The authenticateCustomer method used for customer log in
//	@Override
//	public boolean authenticateCustomer(String username, String password)throws AuthenticationException {
//	    boolean isAuthenticated = false;
//	    try {
//	        Connection conn = DBConnUtil.getDbConnection();
//	        String sql = "SELECT * FROM customer WHERE username = ? AND password = ?";
//	        PreparedStatement ps = conn.prepareStatement(sql);
//	        ps.setString(1, username);
//	        ps.setString(2, password);
//	        ResultSet rs = ps.executeQuery();
//	        if (rs.next()) {
//	            isAuthenticated = true;
//	        }
//	        else throw new AuthenticationException("Incorrect username or password.Please give correct login credentials ");
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	    }
//	    return isAuthenticated;
//	}
	
	@Override
	public boolean authenticateCustomer(String username, String password) throws AuthenticationException {
	    boolean isAuthenticated = false;
	    try {
	        Connection conn = DBConnUtil.getDbConnection();
	        String sql = "SELECT * FROM customer WHERE username = ? AND password = ?";
	        PreparedStatement ps = conn.prepareStatement(sql);
	        ps.setString(1, username);
	        ps.setString(2, password);
	        ResultSet rs = ps.executeQuery();
	        
	        if (rs.next()) {
	            isAuthenticated = true;
	        } else {
	            throw new AuthenticationException("Incorrect username or password. Please give correct login credentials.");
	        }
	    } catch (SQLException e) {
	        //e.printStackTrace(); 
	        throw new AuthenticationException("Internal error during authentication. Please try again later.");
	    }
	    return isAuthenticated;
	}

	// --------------------------------------------------------------------------------------------------------
	
	@Override
	public Customer getCustomerById(int customerId) {
	    String query = "SELECT * FROM Customer WHERE CustomerID = ?";
	    try (Connection con = DBConnUtil.getDbConnection(); PreparedStatement ps = con.prepareStatement(query)) {
	        ps.setInt(1, customerId);
	        ResultSet rs = ps.executeQuery();

	        if (rs.next()) {
	            Customer customer = new Customer();
	            customer.setCustomerId(rs.getInt("CustomerID"));
	            customer.setFirstName(rs.getString("FirstName"));
	            customer.setLastName(rs.getString("LastName"));
	            customer.setEmail(rs.getString("Email"));
	            customer.setPhoneNumber(rs.getString("PhoneNumber"));
	            customer.setAddress(rs.getString("Address"));
	            customer.setUsername(rs.getString("Username"));
	            customer.setPassword(rs.getString("Password"));
	            customer.setRegistrationDate(rs.getString("RegistrationDate")); // or Date type if used
	            return customer;
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return null; 
	}

	//-----------------------------------------------------------------------------------------------------------------
	//GetCustomerByUsername()  - i am using this method in updateCustomer()
	
		@Override
		public Customer getCustomerByUsername(String username) {
		    String query = "SELECT * FROM Customer WHERE Username = ?";
		    try (Connection con = DBConnUtil.getDbConnection(); PreparedStatement ps = con.prepareStatement(query)) {
		        ps.setString(1, username);
		        ResultSet rs = ps.executeQuery();

		        if (rs.next()) {
		            Customer customer = new Customer();
		            customer.setCustomerId(rs.getInt("CustomerID"));
		            customer.setFirstName(rs.getString("FirstName"));
		            customer.setLastName(rs.getString("LastName"));
		            customer.setEmail(rs.getString("Email"));
		            customer.setPhoneNumber(rs.getString("PhoneNumber"));
		            customer.setAddress(rs.getString("Address"));
		            customer.setUsername(rs.getString("Username"));
		            customer.setPassword(rs.getString("Password"));
		            customer.setRegistrationDate(rs.getString("RegistrationDate")); // If using String for now
		            return customer;
		        }

		    } catch (Exception e) {
		        e.printStackTrace();
		    }

		    return null; 
		}

	//-------------------------------------------------------------------------------------------------------------
	//updateCustomer()
		
		@Override
		public void updateCustomer(String username) throws SQLException {
		    Scanner sc = new Scanner(System.in);
		    

		    Customer existing = getCustomerByUsername(username);
		    if (existing == null) {
		        System.out.println("Customer with ID " + username + " not found.");
		        return;
		    }

		    boolean keepUpdating = true;

		    while (keepUpdating) {
		        System.out.println("\nWhich field do you want to update?");
		        System.out.println("1. First Name");
		        System.out.println("2. Last Name");
		        System.out.println("3. Email");
		        System.out.println("4. Phone Number");
		        System.out.println("5. Address");
		        System.out.println("6. Username");
		        System.out.println("7. Password");
		        System.out.println("8. Exit");

		        System.out.print("Enter your choice: ");
		        int choice = sc.nextInt();
		        sc.nextLine();

		        String column = "";
		        String sql = "";
		        PreparedStatement stmt = null;

		        switch (choice) {
		            case 1:
		                System.out.print("Enter new First Name: ");
		                column = "FirstName";
		                sql = "UPDATE Customer SET " + column + " = ? WHERE username = ?";
		                stmt = DBConnUtil.getDbConnection().prepareStatement(sql);
		                stmt.setString(1, sc.nextLine());
		                stmt.setString(2, username);
		                break;
		            case 2:
		                System.out.print("Enter new Last Name: ");
		                column = "LastName";
		                sql = "UPDATE Customer SET " + column + " = ? WHERE username = ?";
		                stmt = DBConnUtil.getDbConnection().prepareStatement(sql);
		                stmt.setString(1, sc.nextLine());
		                stmt.setString(2, username);
		                break;
		            case 3:
		                System.out.print("Enter new Email: ");
		                column = "Email";
		                sql = "UPDATE Customer SET " + column + " = ? WHERE username = ?";
		                stmt = DBConnUtil.getDbConnection().prepareStatement(sql);
		                stmt.setString(1, sc.nextLine());
		                stmt.setString(2, username);
		                break;
		            case 4:
		                System.out.print("Enter new Phone Number: ");
		                column = "PhoneNumber";
		                sql = "UPDATE Customer SET " + column + " = ? WHERE username = ?";
		                stmt = DBConnUtil.getDbConnection().prepareStatement(sql);
		                stmt.setString(1, sc.nextLine());
		                stmt.setString(2, username);
		                break;
		            case 5:
		                System.out.print("Enter new Address: ");
		                column = "Address";
		                sql = "UPDATE Customer SET " + column + " = ? WHERE username = ?";
		                stmt = DBConnUtil.getDbConnection().prepareStatement(sql);
		                stmt.setString(1, sc.nextLine());
		                stmt.setString(2, username);
		                break;
		            case 6:
		                System.out.print("Enter new Username: ");
		                column = "Username";
		                sql = "UPDATE Customer SET " + column + " = ? WHERE username = ?";
		                stmt = DBConnUtil.getDbConnection().prepareStatement(sql);
		                stmt.setString(1, sc.nextLine());
		                stmt.setString(2, username);
		                break;
		            case 7:
		                System.out.print("Enter new Password: ");
		                column = "Password";
		                sql = "UPDATE Customer SET " + column + " = ? WHERE username = ?";
		                stmt = DBConnUtil.getDbConnection().prepareStatement(sql);
		                stmt.setString(1, sc.nextLine());
		                stmt.setString(2, username);
		                break;
		            case 8:
		                keepUpdating = false;
		                System.out.println("Customer update completed.");
		                continue;
		            default:
		                System.out.println("Invalid choice. Try again.");
		                continue;
		        }

		        if (stmt != null) {
		            int rows = stmt.executeUpdate();
		            if (rows > 0) {
		                System.out.println(column + " updated successfully!");
		            } else {
		                System.out.println("Failed to update " + column);
		            }
		            stmt.close();
		        }
		    }
		}
   // ------------------------------------------------------------------------------------------------------------
   // deleteCustomer()
		
		@Override
		public void deleteCustomer(String username) {
		    Scanner sc = new Scanner(System.in);

		    System.out.print("Are you sure you want to delete your account? (yes/no): ");
		    String confirm = sc.nextLine();
		    if (!confirm.equalsIgnoreCase("yes")) {
		        System.out.println("Deletion cancelled.");
		        return;
		    }

		    System.out.print("Enter your password to confirm deletion: ");
		    String password = sc.nextLine();

		    Customer existing = getCustomerByUsername(username);
		    if (existing == null) {
		        System.out.println("No customer found with username: " + username);
		        return;
		    }

		    if (!existing.getPassword().equals(password)) {
		        System.out.println("Incorrect password. Deletion denied.");
		        return;
		    }

		    String query = "DELETE FROM Customer WHERE Username = ?";
		    try (Connection con = DBConnUtil.getDbConnection(); PreparedStatement ps = con.prepareStatement(query)) {
		        ps.setString(1, username);
		        int result = ps.executeUpdate();

		        if (result > 0) {
		            System.out.println("Customer account deleted successfully.");
		        } else {
		            System.out.println("Failed to delete customer account.");
		        }

		    } catch (Exception e) {
		        e.printStackTrace();
		        System.out.println("Error occurred while deleting customer.");
		    }
		}
		
		// ---------------------------------------------------------------------------------------------------------------
		
		@Override
		public void update_Customer(String username, String field, String newValue) {
		    Customer existing = getCustomerByUsername(username);
		    if (existing == null) {
		        System.out.println("Customer with username " + username + " not found.");
		        return;
		    }

		    String sql = "UPDATE Customer SET " + field + " = ? WHERE username = ?";
		    try (PreparedStatement stmt = DBConnUtil.getDbConnection().prepareStatement(sql)) {
		        stmt.setString(1, newValue);
		        stmt.setString(2, username);

		        int rows = stmt.executeUpdate();
		        if (rows > 0) {
		            System.out.println(field + " updated successfully!");
		        } else {
		            System.out.println("Failed to update " + field);
		        }
		    }
		    catch (Exception e) {
				System.out.println(e);
			}
		}

}
