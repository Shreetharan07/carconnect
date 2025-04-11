package hexa.org.dao;

import java.sql.SQLException;
import java.util.Map;
import hexa.org.entity.Vehicle;
import hexa.org.exception.VehicleNotFoundException;

public interface IVehicleService {
	public void addVehicle();
	
	public boolean removeVehicle(int VehicleID);
	
	public void updateVehicle() throws SQLException,VehicleNotFoundException;
	
	public Map<Integer, Vehicle> getAvailableVehicles();
	
	public Vehicle getVehicleById(int vehicleId);
	
	public void showAllVehicles();
}


