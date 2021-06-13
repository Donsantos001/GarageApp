package ie.cct.Garage.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneId;
import java.util.*;

import ie.cct.Garage.model.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ie.cct.Garage.exceptions.BadRequestException;
import ie.cct.Garage.exceptions.UnauthorizedException;
import ie.cct.Garage.util.JWTIssuer;
import io.jsonwebtoken.Claims;



@RestController
@CrossOrigin("*")
public class FirstController {
	// properties

	//maximum service for a staff per day
	final int WORKER_CAPACITY = 4;

	// List of Users
	private List<User> users;
	
	// List of staff
	private List<Staff> workers;
	
	// <key - parts, value - prices>
	private Map<String, Double> partsList;

	// <key - services, value - price>
	private Map<String, Double> servicesList;

	private List<Booking> bookings;

	//constructor
	public FirstController() {
		users = new ArrayList<>();
		workers = new ArrayList<>();
		partsList = new HashMap<>();
		servicesList = new HashMap<>();
		bookings = new ArrayList<>();

		//just temporary values
		workers.add(new Staff("Santyy", new ArrayList<Booking>()));
		partsList.put("Tyre", 232.0);
		partsList.put("Mirror", 282.0);
		partsList.put("Glass", 322.0);
		servicesList.put("Annual Service", 230.90);
		servicesList.put("Major Service", 330.50);
		servicesList.put("Repair", 190.90);
		servicesList.put("Major Repair", 200.0);
	}




	//REGISTER USER

	
	@PostMapping("/users")
	// 204 means no content, it s success, but it does not have content
	@ResponseStatus(code = HttpStatus.OK) // responsnse 200 instead
	public String registerUser(
			// if i don't specify the name, it will take the name of the variable
			@RequestParam(required = true) String username, @RequestParam(required = true) String password) {
		if (password.length() < 10) {
			throw new BadRequestException("password should be at least 10 characters");
		}
		if (!containsAtLeastOneSymbol(password)) {
			throw new BadRequestException("password should cointain at leat 1 of the following symbols: @#+*/&()%$-:_<>=!");
		}
		if (userExist(username)) {
			throw new BadRequestException("User already exist");
		}
		users.add(new User(username, password));
		// return username;
		return "user created successfully";
	}

	// method to check if the variable password contains at least one symbol
	private boolean containsAtLeastOneSymbol(String string) {

		String symbols = "@#+*/&()%$-:_<>=!";

		if (string != null) {
			for (int i = 0; i < symbols.length(); i++) {
				// CharAt returns the index specified in the string
				// indexOf returns the position of the first value specified in the string (it
				// locates the character)
				// if the string contains any of this symbols the result is going to be
				// different the -1
				if (string.indexOf(symbols.charAt(i)) != -1) {
					return true;
				}
			}
		}
		return false;
	}

	// check if the username is stored in the array users.
	private boolean userExist(String user) {
		// foreach loop to check if the user exist
		for (User u : users) {
			if (u.getUsername().contentEquals(user)) {
				return true;
			}
		}
		// if the user is not registered it will return false.
		return false;
	}




	//LOGIN USER


	@GetMapping("/login")
	public String login(@RequestParam(name = "username", required = true) String username,
						@RequestParam(name = "password", required = true) String password) {


		for (User u : users) {
			if ((u.getUsername().contentEquals(username)) && (u.getPassword().contentEquals(password))) {
				// first parameter username
				// second parameter= issuer Garage
				// third parameter is the subject
				// four = time in miliseconds
				// return a token to keep the user logged in
				return JWTIssuer.createJWT(username, "Garage", username, 86400000);
			}

		}
		// TODO: We want to return 401, when the username or password do not match.
		throw new UnauthorizedException("The credentials are not valid, please try again");

	}







	//LIST ALL USER


	// print the list of users
	@GetMapping("/users")
	public List<User> getUsers() {
		return users;
	}







	//REGISTER STAFF


	@PostMapping("/staff")
	// 204 means no content, it s success, but it does not have content
	@ResponseStatus(code = HttpStatus.OK) // response 200 instead
	public String registerStaff(
			// if i don't specify the name, it will take the name of the variable
			@RequestParam(name = "name", required = true) String name) {
		
		if (staffExist(name)) {
			throw new BadRequestException("worker already exist");
		}
		workers.add(new Staff(name, new ArrayList<Booking>()));
	
		return "worker created successfully";
	}

	// check if the name is stored in the array Staff.
	private boolean staffExist(String name) {
		// foreach loop to check if the worker exists
		for (Staff s : workers) {
			if (s.getName().contentEquals(name)) {
				return true;
			}
		}
		// if the worker is not registered it will return false.
		return false;
	}




	// GET ALL STAFFS

	@GetMapping("/staff")
	public List<Staff> getStaffs() {
		return workers;
	}






	


	
	//USER ADDS BOOKING
	
	@PostMapping("/booking")
	public String addBooking(@RequestHeader(name = "Authorization", required = true) String token,
			 @RequestBody(required = true) Booking booking){

		// Token is divided in two parts. The first contains the Bearer and the second
		// contains the token.
		Claims claims = JWTIssuer.decodeJWT(token.split(" ")[1]);
		// subClaim contains the subject, this is one of the four values that contains the token
		// The "sub" is equal to user name.
		String subClaim = claims.get("sub", String.class);


		// foreach to go through to the array
		for (User u : users) {

			// username is the same as the subject "sub"
			if (u.getUsername().equals(subClaim)) {
				booking.setUser(u);
				booking.setStatus("Booked"); //default value for booking

				if(booking.getDate().getDayOfWeek() == DayOfWeek.SUNDAY){
					throw new BadRequestException("Bookings cannot be made for sunday");
				}

				//get number of bookings already for that day
				int noOfBooking = 0;
				for(Booking bk : bookings){
					if(bk.getDate() == booking.getDate()){
						noOfBooking++;
					}
				}

				//add booking only if number of bookings is less than available workers capacity
				if(noOfBooking < workers.size()*WORKER_CAPACITY){
					if(servicesList.get(booking.getServiceType()) != null){
						booking.setId(bookings.size()+1);
						booking.setCost(servicesList.get(booking.getServiceType()));
						bookings.add(booking);
						return "Booking created successfully";
					}
					else {
						throw new BadRequestException("Service doesn't exist");
					}
				}
				else {
					throw new BadRequestException("Bookings has reached limit for this day");
				}

			}

		}

		throw new UnauthorizedException("user not registered");
	}














	//GET BOOKINGS FOR A DAY

	@GetMapping("/admin/bookings/day")
	public ArrayList<Booking> getBookingsForDay(
					@RequestParam("localdate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
					@RequestHeader(name = "Authorization", required = true) String token){

		ArrayList<Booking> bookingForDay = new ArrayList<>();

		for(Booking bk : bookings){
			if(bk.getDate().compareTo(date) == 0){
				bookingForDay.add(bk);
			}
		}

		return bookingForDay;
	}

	public int countBookingsByDay(LocalDate date, ArrayList<Booking> booking){
		int count = 0;

		for(Booking bk : booking){
			if(bk.getDate() == date){
				count++;
			}
		}
		return count;
	}




	//GET BOOKINGS FOR A WEEK


	@GetMapping("/admin/bookings/week")
	public ArrayList<Booking> getBookingsForWeek(
			@RequestParam(name = "week", required = true) Integer week,
			@RequestParam(name = "year", required = true) Integer year,
			@RequestHeader(name = "Authorization", required = true) String token){

		//check if year or week is valid
		if(year > LocalDate.now().getYear() || week > 52){
			throw new BadRequestException("Invalid Year or Week");
		}


		ArrayList<Booking> bookingForDay = new ArrayList<>();
		ArrayList<LocalDate> datesOfWeek = new ArrayList<>();

		Calendar calendar = Calendar.getInstance();

		for(int i = Calendar.SUNDAY; i  <= Calendar.SATURDAY; i++){
			calendar.setWeekDate(year, week, i);
			datesOfWeek.add(calendar.getTime().toInstant()
								.atZone(ZoneId.systemDefault())
								.toLocalDate());
		}


		//add booking is date is among the week days
		for(Booking bk : bookings){
			if(datesOfWeek.contains(bk.getDate())){
				bookingForDay.add(bk);
			}
		}

		return bookingForDay;
	}













	//GET ALL BOOKINGS

	@GetMapping("/admin/bookings")
	public List<Booking> getAllBookings(
			@RequestHeader(name = "Authorization", required = true) String token){

		return bookings;
	}













	//ASSIGN TASK FOR A STAFF

	@PostMapping("/admin/bookings/staff")
	public String assignTask(
			@RequestHeader(name = "Authorization", required = true) String token,
			@RequestParam(name = "staff", required = true) String staff,
			@RequestParam(name = "bookingid", required = true) Integer id){

		ArrayList<Booking> bookingForDay = new ArrayList<>();

		Booking booking = null;

		for(Booking bk : bookings){
			if(bk.getId() == id){
				booking = bk;
			}
		}

		if(booking == null){
			throw new BadRequestException("Booking doesn't exist");
		}

		for(Staff st : workers){
			if(st.getTask().contains(booking)){
				if(!st.getName().equalsIgnoreCase(staff)){
					throw new BadRequestException("Task already assigned to another staff");
				}
			}

			if(st.getName().equalsIgnoreCase(staff)){
				ArrayList<Booking> bk = bookingsPerUser(staff);

				if(!st.getTask().contains(booking)){
					int count = countBookingsByDay(booking.getDate(), bk);

					if(count < 4){
						st.addTask(booking);
						return "Task assigned successfully";
					}
					else {
						throw new BadRequestException("staff service limit exceeded");
					}
				}

				throw new BadRequestException("Task already assigned to this staff")
			}
		}

		throw new BadRequestException("Staff doesn't exist");

	}












	@PostMapping("/admin/bookings/addcost")
	public String addCostToBooking(
			@RequestHeader(name = "Authorization", required = true) String token,
			@RequestParam(name = "part", required = true) String part,
			@RequestParam(name = "bookingid", required = true) Integer id){

		for(Booking bk : bookings){
			if(bk.getId() == id){
				if(partsList.get(part) == null){
					throw new BadRequestException("Parts doesn't exist, try adding a new part");
				}
				bk.addExtraCost(part, partsList.get(part)); // just a temporary value

				return "Cost Added successfully";
			}
		}

		throw new BadRequestException("Booking doesn't exist");

	}













	//ADD A NEW VEHICLE PART WITH PRICE

	@PostMapping("/admin/part/add")
	public String addVehiclePart(
			@RequestHeader(name = "Authorization", required = true) String token,
			@RequestParam(name = "part", required = true) String part,
			@RequestParam(name = "price", required = true) Double price){

		if(partsList.get(part) == null){
			partsList.put(part, price);
			return "Part added successfully";
		}

		throw new BadRequestException("Part already exists");

	}












	//UPDATE THE STATUS OF A BOOKING

	@PostMapping("/admin/bookings/update/{status}")
	public String updateStatus(
			@RequestHeader(name = "Authorization", required = true) String token,
			@PathVariable(name = "status", required = true) String status,
			@RequestParam(name = "bookingid", required = true) Integer id){

		for(Booking bk : bookings){
			if(bk.getId() == id){
				bk.setStatus(status); // just a temporary value
				return "Status updated successfully";
			}
		}

		throw new BadRequestException("Booking doesn't exist");
	}


















	//PRINT INVOICE FOR USER

	@GetMapping("/admin/bookings/invoice")
	public String printInvoice(
			@RequestParam(name = "username", required = true) String username,
			@RequestHeader(name = "Authorization", required = true) String token //admin authorization
			){

		ArrayList<Booking> completedBooking = new ArrayList<>();

		for(Booking bk : bookingsPerUser(username)){
			if(bk.getStatus().equalsIgnoreCase("Completed")){
				completedBooking.add(bk);
			}
		}

		String invoice = "";

		for(Booking bk : completedBooking){
			invoice += "Customer : " + bk.getDetail().getName();
			invoice += "\nMobile No : " + bk.getDetail().getPhoneNumber();
			invoice += "\nEmail : " + bk.getDetail().getEmail();
			invoice += "\n\nVehicle : " + bk.getVehicle().getVehicleMake();
			invoice += "\nLicence : " + bk.getVehicle().getVehicleLicense();
			invoice += "\n" + bk.getServiceType() + " : €" + bk.getCost();

			int total = 0;
			for(String part : bk.getExtraCost().keySet()){
				invoice += "\n" + part + " : €" + bk.getExtraCost().get(part);
				total += bk.getExtraCost().get(part);
			}

			invoice += "\nTOTAL DUE  : €" + (total + bk.getCost()) + "\n";
		}

		return invoice;

	}
















	//GET BOOKINGS PER USER

	@GetMapping("/admin/bookings/{user}")
	public ArrayList<Booking> getBookingsPerUser(
			@RequestHeader(name = "Authorization", required = true) String token,
			@PathVariable(name = "user") String username){

		return bookingsPerUser(username);
	}

	public ArrayList<Booking> bookingsPerUser(String user){
		ArrayList<Booking> bks = new ArrayList<>();

		for(Booking bk : bookings){
			if(bk.getUser().getUsername().equalsIgnoreCase(user)){
				bks.add(bk);
			}
		}

		return bks;
	}

}
