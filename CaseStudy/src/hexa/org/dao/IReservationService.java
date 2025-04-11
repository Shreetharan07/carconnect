package hexa.org.dao;

import java.sql.SQLException;
import java.time.LocalDateTime;

import hexa.org.entity.Vehicle;
import hexa.org.exception.ReservationException;

public interface IReservationService {

	public boolean createReservation() throws ReservationException, SQLException;
	
	public boolean isVehicleAvailable(int vehicleId, LocalDateTime start, LocalDateTime end);
	
	public void getReservationsByCustomerId(int customerId);
	
	public void getReservationById(int reservationId);
	
	public boolean cancelReservation(int reservationId) throws ReservationException;
	
	public void updateReservation() throws ReservationException, SQLException;
}

