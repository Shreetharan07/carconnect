package hexa.org.test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Test;
import hexa.org.dao.VehicleService;
import hexa.org.entity.Vehicle;

public class VehicleServiceTest {
	
	 //my target- it vehicle will delete
	@Test
	public void testRemoveVehicle() {
	    VehicleService service = new VehicleService();
	    boolean removed = service.removeVehicle(9);  

	    assertTrue(removed);
	}
	
	//my target - there is vehicles 
	@Test
	public void testGetAvailableVehicles() {
	    VehicleService service = new VehicleService();
	    Map<Integer, Vehicle> vehicles = service.getAvailableVehicles();

	    assertNotNull(vehicles);
	    assertFalse(vehicles.isEmpty());  // assuming some vehicles are available
	}
	
	//my target - there is a vehicle with vehicle id=7 
	@Test
	public void testGetVehicleById() {
	    VehicleService service = new VehicleService();
	    Vehicle vehicle = service.getVehicleById(7); 

	    assertNotNull(vehicle);
	    assertEquals(7, vehicle.getVehicleId());
	}
}

