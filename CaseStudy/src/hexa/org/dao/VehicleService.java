package hexa.org.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import hexa.org.entity.*;
import hexa.org.exception.VehicleNotFoundException;
import hexa.org.util.DBConnUtil;

public class VehicleService implements IVehicleService{

	// addVehicle
	
	@Override
	public void addVehicle() {
		
		
		Scanner sc = new Scanner(System.in);
		Vehicle vehicle = new Vehicle();

		System.out.print("Enter Model: ");
		vehicle.setModel(sc.nextLine());

		System.out.print("Enter Make: ");
		vehicle.setMake(sc.nextLine());

		System.out.print("Enter Year: ");
		vehicle.setYear(sc.nextInt());
		sc.nextLine(); // consume newline

		System.out.print("Enter Color: ");
		vehicle.setColor(sc.nextLine());

		System.out.print("Enter Registration Number: ");
		vehicle.setRegistrationNumber(sc.nextLine());

		System.out.print("Is the vehicle available? (true/false): ");
		vehicle.setAvailability(sc.nextBoolean());

		System.out.print("Enter Daily Rate: ");
		vehicle.setDailyRate(sc.nextDouble());

		try {
		    Connection conn = DBConnUtil.getDbConnection();
		    String sql = "INSERT INTO Vehicle (Model, Make, Year, Color, RegistrationNumber, Availability, DailyRate) VALUES (?, ?, ?, ?, ?, ?, ?)";

		    PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		    stmt.setString(1, vehicle.getModel());
		    stmt.setString(2, vehicle.getMake());
		    stmt.setInt(3, vehicle.getYear());
		    stmt.setString(4, vehicle.getColor());
		    stmt.setString(5, vehicle.getRegistrationNumber());
		    stmt.setBoolean(6, vehicle.isAvailability());
		    stmt.setDouble(7, vehicle.getDailyRate());

		    int rowsInserted = stmt.executeUpdate();

		    if (rowsInserted > 0) {
		        ResultSet rs = stmt.getGeneratedKeys();
		        if (rs.next()) {
		            int generatedId = rs.getInt(1);
		            vehicle.setVehicleId(generatedId); // Set generated ID back to object
		        }
		        System.out.println("Vehicle added successfully with ID: " + vehicle.getVehicleId());
		    } 
		    else {
		        System.out.println("Vehicle insertion failed.");
		    }

		    stmt.close();
		    conn.close();
		} 
		catch (SQLException e) {
		    e.printStackTrace(); // or use logger
		    System.out.println("Error while inserting vehicle: " + e.getMessage());
		}

	}
	
	// ----------------------------------------------------------------------------------
	// removeVehicle
	@Override
	public boolean removeVehicle(int vehicleId) {
	    boolean isRemoved = false;

	    try {
	        Connection conn = DBConnUtil.getDbConnection();
	        String sql = "DELETE FROM Vehicle WHERE VehicleID = ?";

	        PreparedStatement stmt = conn.prepareStatement(sql);
	        stmt.setInt(1, vehicleId);

	        int rowsDeleted = stmt.executeUpdate();

	        if (rowsDeleted > 0) {
	            System.out.println("Vehicle with ID " + vehicleId + " removed successfully.");
	            isRemoved = true;
	        } else {
	            System.out.println("No vehicle found with ID: " + vehicleId);
	        }

	        stmt.close();
	        conn.close();

	    } catch (SQLException e) {
	        e.printStackTrace(); // Or handle it better with logger
	        System.out.println("Error while removing vehicle: " + e.getMessage());
	    }

	    return isRemoved;
	}

	//  ------------------------------------------------------------------------------
	
	// updateVehicle
	@Override
	public void updateVehicle() throws SQLException,VehicleNotFoundException {
		Scanner sc=new Scanner(System.in);
		System.out.print("Enter Vehicle ID to update: ");
		int vehicleId = sc.nextInt();
		Vehicle vehicle=getVehicleById(vehicleId);
		if(vehicle==null) throw new VehicleNotFoundException("There is no vehicle with vehicleId: "+vehicleId);
		sc.nextLine();

		boolean keepUpdating = true;

		while (keepUpdating) {
		    System.out.println("\nWhat do you want to update?");
		    System.out.println("1. Model");
		    System.out.println("2. Make");
		    System.out.println("3. Year");
		    System.out.println("4. Color");
		    System.out.println("5. Registration Number");
		    System.out.println("6. Availability");
		    System.out.println("7. Daily Rate");
		    System.out.println("8. Exit - updated everything");

		    System.out.print("Choose an option: ");
		    int choice = sc.nextInt();
		    sc.nextLine();

		    String column = "";
		    String sql = "";
		    PreparedStatement stmt = null;

		    switch (choice) {
		        case 1:
		            System.out.print("Enter new Model: ");
		            String model = sc.nextLine();
		            column = "Model";
		            sql = "UPDATE Vehicle SET " + column + " = ? WHERE VehicleID = ?";
		            stmt = DBConnUtil.getDbConnection().prepareStatement(sql);
		            stmt.setString(1, model);
		            stmt.setInt(2, vehicleId);
		            break;
		        case 2:
		            System.out.print("Enter new Make: ");
		            String make = sc.nextLine();
		            column = "Make";
		            sql = "UPDATE Vehicle SET " + column + " = ? WHERE VehicleID = ?";
		            stmt = DBConnUtil.getDbConnection().prepareStatement(sql);
		            stmt.setString(1, make);
		            stmt.setInt(2, vehicleId);
		            break;
		        case 3:
		            System.out.print("Enter new Year: ");
		            int year = sc.nextInt();
		            column = "Year";
		            sql = "UPDATE Vehicle SET " + column + " = ? WHERE VehicleID = ?";
		            stmt = DBConnUtil.getDbConnection().prepareStatement(sql);
		            stmt.setInt(1, year);
		            stmt.setInt(2, vehicleId);
		            break;
		        case 4:
		            System.out.print("Enter new Color: ");
		            String color = sc.nextLine();
		            column = "Color";
		            sql = "UPDATE Vehicle SET " + column + " = ? WHERE VehicleID = ?";
		            stmt = DBConnUtil.getDbConnection().prepareStatement(sql);
		            stmt.setString(1, color);
		            stmt.setInt(2, vehicleId);
		            break;
		        case 5:
		            System.out.print("Enter new Registration Number: ");
		            String regNo = sc.nextLine();
		            column = "RegistrationNumber";
		            sql = "UPDATE Vehicle SET " + column + " = ? WHERE VehicleID = ?";
		            stmt = DBConnUtil.getDbConnection().prepareStatement(sql);
		            stmt.setString(1, regNo);
		            stmt.setInt(2, vehicleId);
		            break;
		        case 6:
		            System.out.print("Is the vehicle available? (true/false): ");
		            boolean available = sc.nextBoolean();
		            column = "Availability";
		            sql = "UPDATE Vehicle SET " + column + " = ? WHERE VehicleID = ?";
		            stmt = DBConnUtil.getDbConnection().prepareStatement(sql);
		            stmt.setBoolean(1, available);
		            stmt.setInt(2, vehicleId);
		            break;
		        case 7:
		            System.out.print("Enter new Daily Rate: ");
		            double rate = sc.nextDouble();
		            column = "DailyRate";
		            sql = "UPDATE Vehicle SET " + column + " = ? WHERE VehicleID = ?";
		            stmt = DBConnUtil.getDbConnection().prepareStatement(sql);
		            stmt.setDouble(1, rate);
		            stmt.setInt(2, vehicleId);
		            break;
		        case 8:
		            keepUpdating = false;
		            continue;
		        default:
		            System.out.println("Invalid choice.");
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
	// -------------------------------------------------------------------------------------------------------------
	// getAvailableVehicles
	
	public Map<Integer, Vehicle> getAvailableVehicles() {
	    Map<Integer, Vehicle> availableVehicles = new HashMap<>();

	    String query = "SELECT * FROM Vehicle WHERE Availability = TRUE";

	    try (Connection conn = DBConnUtil.getDbConnection();  // this try syntax is introduce
	         PreparedStatement stmt = conn.prepareStatement(query); //Automatically closes all resources (Connection, Statement, ResultSet)
	         ResultSet rs = stmt.executeQuery()) {

	        while (rs.next()) {
	            Vehicle vehicle = new Vehicle();
	            vehicle.setVehicleId(rs.getInt("VehicleID"));
	            vehicle.setModel(rs.getString("Model"));
	            vehicle.setMake(rs.getString("Make"));
	            vehicle.setYear(rs.getInt("Year"));
	            vehicle.setColor(rs.getString("Color"));
	            vehicle.setRegistrationNumber(rs.getString("RegistrationNumber"));
	            vehicle.setAvailability(rs.getBoolean("Availability"));
	            vehicle.setDailyRate(rs.getDouble("DailyRate"));

	            // Store in HashMap with VehicleID as key
	            availableVehicles.put(vehicle.getVehicleId(), vehicle);
	        }

	    } catch (SQLException e) {
	        System.err.println("Error retrieving available vehicles: " + e.getMessage());
	    }

	    return availableVehicles;
	}

	//-------------------------------------------------------------------------------------------------------------
	//getVehicleById() 
	
	@Override
	public Vehicle getVehicleById(int vehicleId) {
	    String query = "SELECT * FROM Vehicle WHERE VehicleID = ?";
	    try (Connection con = DBConnUtil.getDbConnection(); PreparedStatement ps = con.prepareStatement(query)) {
	        ps.setInt(1, vehicleId);
	        ResultSet rs = ps.executeQuery();

	        if (rs.next()) {
	            Vehicle vehicle = new Vehicle();
	            vehicle.setVehicleId(rs.getInt("VehicleID"));
	            vehicle.setModel(rs.getString("Model"));
	            vehicle.setMake(rs.getString("Make"));
	            vehicle.setYear(rs.getInt("Year"));
	            vehicle.setColor(rs.getString("Color"));
	            vehicle.setRegistrationNumber(rs.getString("RegistrationNumber"));
	            vehicle.setAvailability(rs.getBoolean("Availability"));
	            vehicle.setDailyRate(rs.getDouble("DailyRate"));
	            return vehicle;
	        }
	        

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return null; 
	}
	
	// ---------------------------------------------------------------------------------------------------------------
	
	@Override
	public void showAllVehicles() {
	    String query = "SELECT * FROM Vehicle";

	    try (Connection con = DBConnUtil.getDbConnection();
	         PreparedStatement ps = con.prepareStatement(query);
	         ResultSet rs = ps.executeQuery()) {

	        boolean found = false;

	        System.out.println("-------------------------------------------------------------");
	        System.out.println("                     AVAILABLE VEHICLES");
	        System.out.println("-------------------------------------------------------------");

	        while (rs.next()) {
	            found = true;
	            int vehicleId = rs.getInt("VehicleID");
	            String model = rs.getString("Model");
	            String make = rs.getString("Make");
	            int year = rs.getInt("Year");
	            String color = rs.getString("Color");
	            String regNo = rs.getString("RegistrationNumber");
	            boolean availability = rs.getBoolean("Availability");
	            double dailyRate = rs.getDouble("DailyRate");

	            System.out.println("Vehicle ID       : " + vehicleId);
	            System.out.println("Model            : " + model);
	            System.out.println("Make             : " + make);
	            System.out.println("Year             : " + year);
	            System.out.println("Color            : " + color);
	            System.out.println("Registration No. : " + regNo);
	            System.out.println("Available        : " + (availability ? "Yes" : "No"));
	            System.out.printf ("Daily Rate       : ₹%.2f%n", dailyRate);
	            System.out.println("-------------------------------------------------------------");
	        }

	        if (!found) {
	            System.out.println("⚠️ No vehicles found in the system.");
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        System.out.println("❌ Error fetching vehicles from database.");
	    }
	}

	

}
