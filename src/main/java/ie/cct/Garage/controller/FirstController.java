package ie.cct.Garage.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
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
	// List of Users
	private List<User> users;

	final int WORKER_CAPACITY = 4;
	
	// List of staff
	private List<Staff> workers;
	
	// key - parts, value- prices>
	private Map<String, Double> partsList;

	private List<Booking> bookings;

	//constructor
	public FirstController() {
			
		users = new ArrayList<>();
		workers = new ArrayList<>();
		partsList = new HashMap<>();
		bookings = new ArrayList<>();

		//just a temporary value
		workers.add(new Staff("Santyy", new ArrayList<Booking>()));
		partsList.put("Tyre", 232.0);
		partsList.put("Mirror", 282.0);
		partsList.put("Glass", 322.0);
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





	//LIST USER


	// print the list of users
	@GetMapping("/users")
	public List<User> getUsers() {
		return users;
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



	// print the list of users
	@GetMapping("/staff")
	public List<Staff> getStaffs() {
		return workers;
	}



	//REGISTER STAFF


	@PostMapping("/staff")
	// 204 means no content, it s success, but it does not have content
	@ResponseStatus(code = HttpStatus.OK) // response 200 instead
	public String registerStaff(
			// if i don't specify the name, it will take the name of the variable
			@RequestBody(required = true) String name) {
		
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
					booking.setId(Long.valueOf(bookings.size()+1));
					bookings.add(booking);
					return "Booking created successfully";
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

	//GET BOOKINGS FOR A WEEK


	@GetMapping("/admin/bookings/week")
	public ArrayList<Booking> getBookingsForWeek(
			@RequestParam(name = "week", required = true) Integer week,
			@RequestHeader(name = "Authorization", required = true) String token){

		ArrayList<Booking> bookingForDay = new ArrayList<>();
		HashMap<Integer, LocalDate> datesOfWeek = new HashMap<>();

		Calendar calendar = Calendar.getInstance();

		for(int i = Calendar.SUNDAY; i  <= Calendar.SATURDAY; i++){
			calendar.setWeekDate(LocalDate.now().getYear(), week, i);
			datesOfWeek.put(i, calendar.getTime().toInstant()
								.atZone(ZoneId.systemDefault())
								.toLocalDate());
		}

		for(Booking bk : bookings){
			if(datesOfWeek.values().contains(bk.getDate())){
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
	public String assignStaff(
			@RequestHeader(name = "Authorization", required = true) String token,
			@RequestBody(required = true) String staff,
			@RequestBody(required = true) Booking booking){

		ArrayList<Booking> bookingForDay = new ArrayList<>();

		for(Staff st : workers){
			if(st.getName().equalsIgnoreCase(staff)){
				ArrayList<Booking> bk = bookingsPerUser(staff);

				int count = countBookingsByDay(booking.getDate(), bk);

				if(count < 4){
					st.addTask(booking);
					return "Task assigned successfully";
				}
				else {
					throw new BadRequestException("staff service limit exceeded");
				}
			}
		}

		throw new BadRequestException("user doesn't exist");

	}



	@PostMapping("/admin/bookings/addcost")
	public String addCostToBooking(
			@RequestHeader(name = "Authorization", required = true) String token,
			@RequestParam(name = "part", required = true) String part,
			@RequestBody(required = true) Booking booking){

		for(Booking bk : bookings){
			if(bk == booking){
				bk.addExtraCost(part, partsList.get(part)); // just a temporary value
			}
		}

		throw new BadRequestException("user doesn't exist");

	}


	@PostMapping("/admin/bookings/update/{status}")
	public String updateStatus(
			@RequestHeader(name = "Authorization", required = true) String token,
			@RequestParam(name = "part", required = true) String part,
			@RequestBody(required = true) Booking booking){

		for(Booking bk : bookings){
			if(bk == booking){
				bk.addExtraCost(part, partsList.get(part)); // just a temporary value
			}
		}

		throw new BadRequestException("user doesn't exist");

	}



	//ASSIGN TASK FOR A STAFF

	@PostMapping("/admin/bookings/{user}")
	public String printInvoice(
			@PathVariable(name = "username") String username,
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
			invoice += "Customer : " + bk.getUserDetail().getName();
			invoice += "\nMobile No : " + bk.getUserDetail().getPhoneNumber();
			invoice += "\nEmail : " + bk.getUserDetail().getEmail();
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











	public ArrayList<Booking> bookingsPerUser(String user){
		ArrayList<Booking> bks = new ArrayList<>();

		for(Booking bk : bookings){
			if(bk.getUser().getUsername().equalsIgnoreCase(user)){
				bks.add(bk);
			}
		}

		return bks;
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
}
