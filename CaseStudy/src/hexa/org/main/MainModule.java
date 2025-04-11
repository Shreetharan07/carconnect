package hexa.org.main;

import java.sql.SQLException;

import java.time.LocalDate;
import java.util.Map;
import java.util.Scanner;



import hexa.org.dao.*;
import hexa.org.entity.*;
import hexa.org.exception.*;


public class MainModule {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CustomerService customerService=new CustomerService();
        AdminService adminService=new AdminService();
        VehicleService vehicleService=new VehicleService();
        ReservationService reservationService=new ReservationService();
        boolean exit = false;

        while (!exit) {
        	System.out.println("------------------------------------------------------------------------------------");
            System.out.println("\n* * * * * * *Welcome to CarConnect* * * * * * *\n");
            System.out.println("1. Register as Customer");
            System.out.println("2. Login as Customer");
            System.out.println("3. Register as Admin");
            System.out.println("4. Login as Admin");
            System.out.println("5. Exit");
            System.out.print("\nEnter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); 

            switch (choice) {
            case 1:
                System.out.println("**********Customer Registration************");
                // call customer registration method
                Customer customer = new Customer();
                System.out.print("Enter First Name: ");
                customer.setFirstName(scanner.nextLine());
                System.out.print("Enter Last Name: ");
                customer.setLastName(scanner.nextLine());
                System.out.print("Enter Email: ");
                customer.setEmail(scanner.nextLine());
                System.out.print("Enter Phone Number: ");
                customer.setPhoneNumber(scanner.nextLine());
                System.out.print("Enter Address: ");
                customer.setAddress(scanner.nextLine());
                System.out.print("Enter Username: ");
                customer.setUsername(scanner.nextLine());
                System.out.print("Enter Password: ");
                customer.setPassword(scanner.nextLine());

                customer.setRegistrationDate(LocalDate.now().toString()); // import java.time.LocalDate;

                try {
                    if (customerService.registerCustomer(customer)) {
                        System.out.println("Registration Successful!");
                    } else {
                        System.out.println("Registration Failed. Try again.");
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
                break;
                case 2:
                    System.out.println("***********Customer Login**************");
                    System.out.println("Enter username: ");
                    String username=scanner.next();
                    System.out.println("Enter password: ");
                    String password=scanner.next();
                    boolean flag=false;
//                    try { flag = customerService.authenticateCustomer(username,password); } //AuthenticationException
//				 	catch (AuthenticationException e) { System.out.println(e); }
                    try {
                        if (customerService.authenticateCustomer(username, password)) {
                            flag=true;
                            // Proceed to customer menu
                        }
                    } catch (AuthenticationException e) {
                    	System.out.println(e);
                    }

				
                    if (flag) {
                        System.out.println("Login successful. Welcome, " + username + "!");
                    } else {
                        System.out.println("Please try again....");
                    }
                    
                    while(flag) {
                    	System.out.println("\n1.Update your details");
                    	System.out.println("2.Delete your account");
                    	System.out.println("3.Get Available Vehicles");
                    	System.out.println("4.Create Reservation");
                    	System.out.println("5.Update Reservation");
                    	System.out.println("6.Cancel Reservation");
                    	System.out.println("7.exit");
                    	System.out.println("Enter you choice");
                    	int c=scanner.nextInt();
                    	switch(c) {
                    	case 1:
                    		try {customerService.updateCustomer(username);}
                    		catch (SQLException e) { System.out.println(e);}
                    		break;
                    	
                    	case 2:
                    		customerService.deleteCustomer(username);
                    		
                    		break;
                    	case 3:
                    		Map<Integer, Vehicle> vehicleMap = vehicleService.getAvailableVehicles();

                    		if (vehicleMap.isEmpty()) {
                    		    System.out.println("No vehicles available for rent.");
                    		} else {
                    		    System.out.println("Available Vehicles:\n");

                    		    for (Map.Entry<Integer, Vehicle> entry : vehicleMap.entrySet()) {
                    		        Vehicle v = entry.getValue();                   		       
                    		        System.out.println("Vehicle ID: " + entry.getKey());
                    		        System.out.println("Model     : " + v.getModel());
                    		        System.out.println("Make      : " + v.getMake());
                    		        System.out.println("Year      : " + v.getYear());
                    		        System.out.println("Color     : " + v.getColor());
                    		        System.out.println("Reg No    : " + v.getRegistrationNumber());
                    		        System.out.println("Daily Rate: " + v.getDailyRate());
                    		        System.out.println("---------------------------------------------------");
                    		    }
                    		}

                    		break;
                    	
                    	case 4:
                    		try {
                    	        boolean isReserved = reservationService.createReservation();

                    	        if (isReserved) {
                    	            System.out.println("Reservation successful!");
                    	        } else {
                    	            System.out.println("Reservation failed. Please try again.");
                    	        }

                    	    } catch (ReservationException e) {    // ReservationException
                    	        System.out.println("Reservation Error: " + e.getMessage());

                    	    } catch (SQLException e) {
                    	        System.out.println("Database Error: " + e.getMessage());
                    	    }
                    	    break;
                    	case 5:
                    	 
                    	    try {
                    	        reservationService.updateReservation(); // 🎯 calls your full menu-driven method
                    	    } catch (ReservationException e) {
                    	        System.out.println("❌ Reservation Error: " + e.getMessage());
                    	    } catch (SQLException e) {
                    	        System.out.println("❌ Database Error: " + e.getMessage());
                    	    }
                    	    break;

                    	case 6:
                    	    System.out.print("Enter Reservation ID to cancel: ");
                    	    int reservationId = new Scanner(System.in).nextInt();

                    	    try {
                    	        boolean result = reservationService.cancelReservation(reservationId);
                    	        if (result) {
                    	            System.out.println("Reservation cancelled successfully.");
                    	        } else {
                    	            System.out.println("Cancellation failed. Try again.");
                    	        }
                    	    } catch (ReservationException e) {
                    	        System.out.println(e.getMessage());
                    	    }
                    	    break;
                    	    
                    	case 7:
                    		flag=false;
                    		break;
                    		
                    	default:
                    		System.out.println("Invalid choice. Please try again.");
                    	
                    	}
                    }
                    
                    break;
                
                case 3:
                	Admin admin = new Admin();
                	try {
                		if(adminService.registerAdmin(admin)) System.out.println("Admin Registration Successful!");
                		else System.out.println("Registration Failed. Try again.");
                	}
                	catch (InvalidInputException e) {      // InvalidInputException is handled
						System.out.println(e);
					}
                	catch (DatabaseConnectionException e) {    // DatabaseConnectionException is handled
                		System.out.println(e);
					}
                	
                	break;
                	
                case 4:
                    System.out.println("***************Admin Login******************");
                    System.out.println("Enter username: ");
                    String user_name=scanner.next();
                    System.out.println("Enter password: ");
                    String pass_word=scanner.next();
                    boolean flag1=false;
                    try {
                    		if(adminService.authenticateAdmin(user_name,pass_word)) {
                    			flag1=true;
                    		}
                    }
                    catch (InvalidInputException e) {
						System.out.println(e);
					}
                    catch (SQLException e1) {
						System.out.println(e1);
					}
                    if (flag1) {
                        System.out.println("Login successful. Welcome, " + user_name + "!");
                    } else {
                        System.out.println("Please try again.");
                    }
                    
                    while(flag1) {    
                    	System.out.println("\n1.Update admin");
                    	System.out.println("2.delete admin");
                    	System.out.println("3.show all vehicles");
                    	System.out.println("4.Add Vehicle");
                    	System.out.println("5.Update Vehicle");
                    	System.out.println("6.Remove Vehicle");                
                    	System.out.println("7.Exit");
                    	System.out.print("\nEnter your choice: ");
                    	int c=scanner.nextInt();
                    	switch(c) {
                    	case 1:
                    		try {
                    			adminService.updateAdmin(user_name);
                    		}
                    		catch(AdminNotFoundException e) {System.out.println(e);}   // AdminNotFoundException
                    		catch(SQLException e) {System.out.println(e);}
                    		break;
                    	case 2:
                    		adminService.deleteAdmin(user_name);
                    		break;
                    	case 3:
                    		vehicleService.showAllVehicles();
                    		break;
                    		
                    	case 4: 
                    		vehicleService.addVehicle();
                    		break;
                    	case 5:
                    		try{vehicleService.updateVehicle();}
                    		catch(VehicleNotFoundException e) {System.out.println(e);} // VehicleNotFoundException
                    		catch (SQLException e) {System.out.println(e.getMessage());}
                    		break;
                    	case 6:
                    		System.out.print("Enter the Vehicle ID to remove: ");
                    		int vehicleIdToRemove = scanner.nextInt();

                    		boolean removed = vehicleService.removeVehicle(vehicleIdToRemove);
                    		if (!removed) {
                    		    System.out.println("Remove operation failed or vehicle not found.");
                    		}
                    		break;
                   		
                    	case 7:
                    		flag1=false;
                    		break;
                    		
                    	default:
                    		System.out.println("Invalid choice. Please try again.");
                    	}
                    	
                    }
                    break;
                case 5:
                    System.out.println("Exiting... Thank you for using CarConnect!");
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        scanner.close();
    }
}

