package hexa.org.dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

import hexa.org.entity.Customer;
import hexa.org.entity.Reservation;
import hexa.org.entity.Vehicle;
import hexa.org.exception.ReservationException;
import hexa.org.util.DBConnUtil;
import hexa.org.util.DBPropertyUtil;

import java.sql.Timestamp;




// ReservationService

public class ReservationService implements IReservationService{
	
	@Override
	public boolean createReservation() throws ReservationException, SQLException {
	    Scanner sc = new Scanner(System.in);

	    System.out.print("Enter Customer ID: ");
	    int customerId = sc.nextInt();

	    System.out.print("Enter Vehicle ID: ");
	    int vehicleId = sc.nextInt();
	    sc.nextLine(); // flush newline

	    System.out.print("Enter Start Date and Time (yyyy-MM-dd HH:mm:ss): ");
	    String startInput = sc.nextLine();

	    System.out.print("Enter End Date and Time (yyyy-MM-dd HH:mm:ss): ");
	    String endInput = sc.nextLine();

	    // Parse dates
	    LocalDateTime start = LocalDateTime.parse(startInput, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	    LocalDateTime end = LocalDateTime.parse(endInput, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

	    if (start.isAfter(end) || start.isEqual(end)) {
	        throw new ReservationException("End time must be after start time.");
	    }

	    //  Check if vehicle exists
	    VehicleService vehicleService=new VehicleService();
	    Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
	    if (vehicle == null) {
	        throw new ReservationException("Vehicle not found.");
	    }

	    //  Check time-slot availability
	    if (!isVehicleAvailable(vehicleId, start, end)) {
	        throw new ReservationException("Vehicle is already booked for the selected time.");
	    }

	    //  Calculate total cost (hourly)
	    long hours = ChronoUnit.HOURS.between(start, end);
	    double hourlyRate = vehicle.getDailyRate() / 24.0;
	    double totalCost = hourlyRate * hours;

	    //  Insert reservation
	    String query = "INSERT INTO Reservation (CustomerID, VehicleID, StartDate, EndDate, TotalCost, Status) VALUES (?, ?, ?, ?, ?, ?)";

	    try (Connection con = DBConnUtil.getDbConnection(); PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
	        ps.setInt(1, customerId);
	        ps.setInt(2, vehicleId);
	        ps.setTimestamp(3, Timestamp.valueOf(start));
	        ps.setTimestamp(4, Timestamp.valueOf(end));
	        ps.setDouble(5, totalCost);
	        ps.setString(6, "Confirmed");

	        int rows = ps.executeUpdate();
	        if (rows > 0) {
	            ResultSet rs = ps.getGeneratedKeys();
	            if (rs.next()) {
	                int reservationId = rs.getInt(1);
	                System.out.printf(
	                    "Reservation Confirmed!\nReservation ID: %d\nDuration: %d hours\nTotal Cost: ₹%.2f%n",
	                    reservationId, hours, totalCost
	                );
	                
	                return true;
	            }
	        }
	    }

	    System.out.println("Failed to create reservation.");
	    return false;
	}

	
	// ----------------------------------------------------------------------------------------------------------
	
	// isVehicleAvailable
	
	public boolean isVehicleAvailable(int vehicleId, LocalDateTime start, LocalDateTime end) {
	    String query = "SELECT * FROM Reservation WHERE VehicleID = ? " +
	                   "AND Status IN ('Pending', 'Confirmed') " +
	                   "AND ((StartDate <= ? AND EndDate > ?) " +
	                   "OR (StartDate < ? AND EndDate >= ?) " +
	                   "OR (StartDate >= ? AND EndDate <= ?))";

	    try (Connection con = DBConnUtil.getDbConnection(); PreparedStatement ps = con.prepareStatement(query)) {
	        Timestamp startTs = Timestamp.valueOf(start);
	        Timestamp endTs = Timestamp.valueOf(end);

	        ps.setInt(1, vehicleId);
	        ps.setTimestamp(2, startTs);
	        ps.setTimestamp(3, startTs);
	        ps.setTimestamp(4, endTs);
	        ps.setTimestamp(5, endTs);
	        ps.setTimestamp(6, startTs);
	        ps.setTimestamp(7, endTs);

	        ResultSet rs = ps.executeQuery();
	        return !rs.next(); // ✅ available if no overlaps found
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false; // assume unavailable on error
	    }
	}

	// -----------------------------------------------------------------------------------------------------------
	// getReservationsByCustomerId
	
	@Override
	public void getReservationsByCustomerId(int customerId) {
	    String query = "SELECT * FROM Reservation WHERE CustomerID = ? ORDER BY StartDate DESC";

	    try (Connection con = DBConnUtil.getDbConnection(); PreparedStatement ps = con.prepareStatement(query)) {
	        ps.setInt(1, customerId);
	        ResultSet rs = ps.executeQuery();

	        boolean found = false;
	        while (rs.next()) {
	            found = true;
	            int reservationId = rs.getInt("ReservationID");
	            int vehicleId = rs.getInt("VehicleID");
	            Timestamp start = rs.getTimestamp("StartDate");
	            Timestamp end = rs.getTimestamp("EndDate");
	            double cost = rs.getDouble("TotalCost");
	            String status = rs.getString("Status");

	            System.out.println("-----------------------------------------------------");
	            System.out.println("Reservation ID  : " + reservationId);
	            System.out.println("Vehicle ID      : " + vehicleId);
	            System.out.println("Start DateTime  : " + start.toLocalDateTime());
	            System.out.println("End DateTime    : " + end.toLocalDateTime());
	            System.out.printf ("Total Cost      : ₹%.2f%n", cost);
	            System.out.println("Status          : " + status);
	        }

	        if (!found) {
	            System.out.println("No reservations found for Customer ID: " + customerId);
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        System.out.println("Error fetching reservations.");
	    }
	}
	
	// ---------------------------------------------------------------------------------------------------------
	
	//getReservationById
	
	@Override
	public void getReservationById(int reservationId) {
	    String query = "SELECT * FROM Reservation WHERE ReservationID = ?";

	    try (Connection con = DBConnUtil.getDbConnection(); PreparedStatement ps = con.prepareStatement(query)) {
	        ps.setInt(1, reservationId);
	        ResultSet rs = ps.executeQuery();

	        if (rs.next()) {
	            int customerId = rs.getInt("CustomerID");
	            int vehicleId = rs.getInt("VehicleID");
	            Timestamp start = rs.getTimestamp("StartDate");
	            Timestamp end = rs.getTimestamp("EndDate");
	            double totalCost = rs.getDouble("TotalCost");
	            String status = rs.getString("Status");

	            System.out.println("--------------------------------------------------");
	            System.out.println("Reservation ID  : " + reservationId);
	            System.out.println("Customer ID     : " + customerId);
	            System.out.println("Vehicle ID      : " + vehicleId);
	            System.out.println("Start DateTime  : " + start.toLocalDateTime());
	            System.out.println("End DateTime    : " + end.toLocalDateTime());
	            System.out.printf ("Total Cost      : ₹%.2f%n", totalCost);
	            System.out.println("Status          : " + status);
	        } else {
	            System.out.println("No reservation found with ID: " + reservationId);
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        System.out.println("Error fetching reservation.");
	    }
	}

// ------------------------------------------------------------------------------------------------------------
	
	// cancelReservation
	
	
	@Override
	public boolean cancelReservation(int reservationId) throws ReservationException {
	    String updateQuery = "UPDATE Reservation SET Status = 'Cancelled' WHERE ReservationID = ?";

	    try (Connection con = DBConnUtil.getDbConnection();
	         PreparedStatement ps = con.prepareStatement(updateQuery)) {

	        ps.setInt(1, reservationId);
	        int rows = ps.executeUpdate();

	        if (rows > 0) {
	            System.out.println("Reservation ID " + reservationId + " has been cancelled.");
	            return true;
	        } else {
	            throw new ReservationException("Reservation ID not found. Cancellation failed.");
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        throw new ReservationException("Error while cancelling reservation.");
	    }
	}

// ----------------------------------------------------------------------------------------------------------------------------
	
	
//
	
	@Override
	public void updateReservation() throws ReservationException,SQLException{
	    Scanner sc = new Scanner(System.in);

	    System.out.print("Enter Reservation ID to update: ");
	    int reservationId = sc.nextInt();
	    boolean keepUpdating = true;

	    while (keepUpdating) {
	        System.out.println("\nWhat do you want to update?");
	        System.out.println("1. Start Date & Time");
	        System.out.println("2. End Date & Time");
	        System.out.println("3. Exit - Done Updating");

	        System.out.print("Choose an option: ");
	        int choice = sc.nextInt();
	        sc.nextLine();

	        String column = "";
	        String sql = "";
	        PreparedStatement stmt = null;

	        switch (choice) {
	            case 1:
	                System.out.print("Enter new Start Date and Time (yyyy-MM-dd HH:mm:ss): ");
	                String newStart = sc.nextLine();
	                column = "StartDate";
	                sql = "UPDATE Reservation SET " + column + " = ? WHERE ReservationID = ?";
	                stmt = DBConnUtil.getDbConnection().prepareStatement(sql);
	                stmt.setTimestamp(1, Timestamp.valueOf(newStart));
	                stmt.setInt(2, reservationId);
	                break;

	            case 2:
	                System.out.print("Enter new End Date and Time (yyyy-MM-dd HH:mm:ss): ");
	                String newEnd = sc.nextLine();
	                column = "EndDate";
	                sql = "UPDATE Reservation SET " + column + " = ? WHERE ReservationID = ?";
	                stmt = DBConnUtil.getDbConnection().prepareStatement(sql);
	                stmt.setTimestamp(1, Timestamp.valueOf(newEnd));
	                stmt.setInt(2, reservationId);
	                break;

	            case 3:
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



	

	
}
