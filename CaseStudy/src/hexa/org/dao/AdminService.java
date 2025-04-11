package hexa.org.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.Optional;
import java.util.Scanner;

import hexa.org.entity.Admin;
import hexa.org.exception.AdminNotFoundException;
import hexa.org.exception.DatabaseConnectionException;
import hexa.org.exception.InvalidInputException;
import hexa.org.util.DBConnUtil;

public class AdminService implements IAdminService{
	
	// registerAdmin
	@Override
	public boolean registerAdmin(Admin admin) throws InvalidInputException, DatabaseConnectionException {
	    Scanner sc = new Scanner(System.in);

	    System.out.print("Enter First Name: ");
	    admin.setFirstName(sc.nextLine());

	    System.out.print("Enter Last Name: ");
	    admin.setLastName(sc.nextLine());

	    System.out.print("Enter Email: ");
	    admin.setEmail(sc.nextLine());

	    System.out.print("Enter Phone Number: ");
	    admin.setPhoneNumber(sc.nextLine());

	    System.out.print("Enter Username: ");
	    admin.setUsername(sc.nextLine());

	    System.out.print("Enter Password: ");
	    admin.setPassword(sc.nextLine());

	    System.out.print("Enter Role (Super Admin / Fleet Manager): ");
	    admin.setRole(sc.nextLine());

	    if (!(admin.getRole()).equals("Super Admin") && !(admin.getRole()).equals("Fleet Manager")) {
	        throw new InvalidInputException("Role must be 'Super Admin' or 'Fleet Manager'");
	    }
	    admin.setJoinDate(java.time.LocalDateTime.now().toString());
	    
	    boolean flag=false;
	    if (admin == null || admin.getUsername() == null || admin.getPassword() == null) {
	        throw new InvalidInputException("Invalid input for customer registration.");
	    }

	    String query = "INSERT INTO Admin (FirstName, LastName, Email, PhoneNumber, Username, Password, Role, JoinDate) " +
	                   "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

	    try (Connection con = DBConnUtil.getDbConnection(); PreparedStatement ps = con.prepareStatement(query,Statement.RETURN_GENERATED_KEYS)) {

	        ps.setString(1, admin.getFirstName());
	        ps.setString(2, admin.getLastName());
	        ps.setString(3, admin.getEmail());
	        ps.setString(4, admin.getPhoneNumber());
	        ps.setString(5, admin.getUsername());
	        ps.setString(6, admin.getPassword());
	        ps.setString(7, admin.getRole());
	        ps.setString(8, admin.getJoinDate());

	        int rowsInserted = ps.executeUpdate();

	        if (rowsInserted > 0) {
	            ResultSet generatedKeys = ps.getGeneratedKeys();
	            if (generatedKeys.next()) {
	                int adminId = generatedKeys.getInt(1); // Retrieve generated customer ID
	                admin.setAdminId(adminId);       // Set it in POJO
	                System.out.println("Admin registered successfully. Your Customer ID is: " + adminId);
	            }
	        }
	        flag=true;
	    }

	      catch (SQLIntegrityConstraintViolationException e) {
	        throw new InvalidInputException("Email, phone, or username already exists.");
	    } catch (Exception e) {
	        throw new DatabaseConnectionException("Error inserting admin into DB");
	    }
	    return flag;
	}
    // -----------------------------------------------------------------------------------------------------------------

	// The authenticateAdmin method used for Admin log in
//		@Override
//		public boolean authenticateAdmin(String username, String password)throws InvalidInputException {
//		    boolean isAuthenticated = false;
//		    try {
//		        Connection conn = DBConnUtil.getDbConnection();
//		        String sql = "SELECT * FROM admin WHERE username = ? AND password = ?";
//		        PreparedStatement ps = conn.prepareStatement(sql);
//		        ps.setString(1, username);
//		        ps.setString(2, password);
//		        ResultSet rs = ps.executeQuery();
//		        if (rs.next()) {
//		            isAuthenticated = true;
//		        }
//		    } catch (Exception e) {
//		        throw new InvalidInputException("Give valid credentials");
//		    }
//		    return isAuthenticated;
//		}
		@Override
		public boolean authenticateAdmin(String username, String password) throws InvalidInputException,SQLException {
		    try {
		        Connection conn = DBConnUtil.getDbConnection();
		        String sql = "SELECT * FROM admin WHERE username = ? AND password = ?";
		        PreparedStatement ps = conn.prepareStatement(sql);
		        ps.setString(1, username);
		        ps.setString(2, password);
		        ResultSet rs = ps.executeQuery();
		        
		        if (rs.next()) {
		            return true;
		        } else {
		            throw new InvalidInputException("Invalid username or password.");
		        }
		    } catch (SQLException e) {
		        throw new InvalidInputException("Database error occurred.");
		    }
		}

		
	// -------------------------------------------------------------------------------------------------------------------	
		
    // updateAdmin() - used to update data in the Admin table
		
		@Override
		public void updateAdmin(String username) throws SQLException, AdminNotFoundException {
		    Scanner sc=new  Scanner(System.in);
		   
		    // Check if admin exists
		    Admin existing = getAdminByUsername(username);
		    if (existing == null) {
		        throw new AdminNotFoundException("Admin with username " + username + " not found.");
		    }


		    boolean keepUpdating = true;

		    while (keepUpdating) {
		        System.out.println("\nWhich field do you want to update?");
		        System.out.println("1. First Name");
		        System.out.println("2. Last Name");
		        System.out.println("3. Email");
		        System.out.println("4. Phone Number");
		        System.out.println("5. Username");
		        System.out.println("6. Password");
		        System.out.println("7. Role");
		        System.out.println("8. Exit - Done updating");

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
		                sql = "UPDATE Admin SET " + column + " = ? WHERE username = ?";
		                stmt = DBConnUtil.getDbConnection().prepareStatement(sql);
		                stmt.setString(1, sc.nextLine());
		                stmt.setString(2, username);
		                break;
		            case 2:
		                System.out.print("Enter new Last Name: ");
		                column = "LastName";
		                sql = "UPDATE Admin SET " + column + " = ? WHERE username = ?";
		                stmt = DBConnUtil.getDbConnection().prepareStatement(sql);
		                stmt.setString(1, sc.nextLine());
		                stmt.setString(2, username);
		                break;
		            case 3:
		                System.out.print("Enter new Email: ");
		                column = "Email";
		                sql = "UPDATE Admin SET " + column + " = ? WHERE username = ?";
		                stmt = DBConnUtil.getDbConnection().prepareStatement(sql);
		                stmt.setString(1, sc.nextLine());
		                stmt.setString(2, username);
		                break;
		            case 4:
		                System.out.print("Enter new Phone Number: ");
		                column = "PhoneNumber";
		                sql = "UPDATE Admin SET " + column + " = ? WHERE username = ?";
		                stmt = DBConnUtil.getDbConnection().prepareStatement(sql);
		                stmt.setString(1, sc.nextLine());
		                stmt.setString(2, username);
		                break;
		            case 5:
		                System.out.print("Enter new Username: ");
		                column = "Username";
		                sql = "UPDATE Admin SET " + column + " = ? WHERE username = ?";
		                stmt = DBConnUtil.getDbConnection().prepareStatement(sql);
		                stmt.setString(1, sc.nextLine());
		                stmt.setString(2, username);
		                break;
		            case 6:
		                System.out.print("Enter new Password: ");
		                column = "Password";
		                sql = "UPDATE Admin SET " + column + " = ? WHERE username = ?";
		                stmt = DBConnUtil.getDbConnection().prepareStatement(sql);
		                stmt.setString(1, sc.nextLine());
		                stmt.setString(2, username);
		                break;
		            case 7:
		                System.out.print("Enter new Role (Super Admin / Fleet Manager): ");
		                String role = sc.nextLine();
		                if (!role.equals("Super Admin") && !role.equals("Fleet Manager")) {
		                    System.out.println("Invalid role. Must be 'Super Admin' or 'Fleet Manager'");
		                    continue;
		                }
		                column = "Role";
		                sql = "UPDATE Admin SET " + column + " = ? WHERE username = ?";
		                stmt = DBConnUtil.getDbConnection().prepareStatement(sql);
		                stmt.setString(1, role);
		                stmt.setString(2,username);
		                break;
		            case 8:
		                keepUpdating = false;
		                System.out.println("Update process finished.");
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
		
		
	// --------------------------------------------------------------------------------------------------------
		
   // getAdminByUsername() - This method is used in updateAdmin() to check if admin with particular ID is present or not
		
		@Override
		public Admin getAdminByUsername(String username) {
		    String query = "SELECT * FROM Admin WHERE Username = ?";
		    try (Connection con = DBConnUtil.getDbConnection(); PreparedStatement ps = con.prepareStatement(query)) {
		        ps.setString(1, username);
		        ResultSet rs = ps.executeQuery();
		        if (rs.next()) {
		            Admin admin = new Admin();
		            admin.setAdminId(rs.getInt("AdminID"));
		            admin.setFirstName(rs.getString("FirstName"));
		            admin.setLastName(rs.getString("LastName"));
		            admin.setEmail(rs.getString("Email"));
		            admin.setPhoneNumber(rs.getString("PhoneNumber"));
		            admin.setUsername(rs.getString("Username"));
		            admin.setPassword(rs.getString("Password"));
		            admin.setRole(rs.getString("Role"));
		            admin.setJoinDate(rs.getString("JoinDate"));
		            return admin;
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		    return null; // Admin not found with this username
		}

	// --------------------------------------------------------------------------------------------------------
		
   // getAdminByID() - Currently we are not using this method
		
		@Override
		public Admin getAdminById(int adminId) {
		    String query = "SELECT * FROM Admin WHERE AdminID = ?";
		    try (Connection con = DBConnUtil.getDbConnection(); PreparedStatement ps = con.prepareStatement(query)) {
		        ps.setInt(1, adminId);
		        ResultSet rs = ps.executeQuery();
		        if (rs.next()) {
		            Admin admin = new Admin();
		            admin.setAdminId(rs.getInt("AdminID"));
		            admin.setFirstName(rs.getString("FirstName"));
		            admin.setLastName(rs.getString("LastName"));
		            admin.setEmail(rs.getString("Email"));
		            admin.setPhoneNumber(rs.getString("PhoneNumber"));
		            admin.setUsername(rs.getString("Username"));
		            admin.setPassword(rs.getString("Password"));
		            admin.setRole(rs.getString("Role"));
		            admin.setJoinDate(rs.getString("JoinDate"));
		            return admin;
		        }
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		    return null; // admin not found
		}
  
		
	// -----------------------------------------------------------------------------------------------------------
		
	// deletAdmin() - It deletes the Admin account , here I cross verifing the user is valid user or not
		
		@Override
		public void deleteAdmin(String username) {
		    Scanner sc = new Scanner(System.in);

		    System.out.print("Are you sure you want to delete your account? (yes/no): ");
		    String confirm = sc.nextLine();
		    if (!confirm.equalsIgnoreCase("yes")) {
		        System.out.println("Deletion cancelled.");
		        return;
		    }

		    System.out.print("Enter your password to confirm: ");
		    String password = sc.nextLine();

		    Admin existing = getAdminByUsername(username);

		    if (existing == null) {
		        System.out.println("No admin found with username: " + username);
		        return;
		    }

		    if (!existing.getPassword().equals(password)) {
		        System.out.println("Incorrect password. Deletion denied.");
		        return;
		    }

		    String query = "DELETE FROM Admin WHERE Username = ?";
		    try (Connection con = DBConnUtil.getDbConnection(); PreparedStatement ps = con.prepareStatement(query)) {
		        ps.setString(1, username);
		        int result = ps.executeUpdate();

		        if (result > 0) {
		            System.out.println("Admin with username '" + username + "' deleted successfully.");
		        } else {
		            System.out.println("Failed to delete admin.");
		        }

		    } catch (Exception e) {
		        e.printStackTrace();
		        System.out.println("Error occurred while deleting admin.");
		    }
		}


		
}
