package ie.cct.Garage.model;

public class Vehicle {
	private String vehicleType;
	private String vehicleMake;
	private String vehicleLicense;
	private String vehicleEngineType;
	
	
	public Vehicle() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Vehicle(String mainService, String username, String phoneNumber, String vehicleType, String vehicleMake, String vehicleLicense,
			String vehicleEngineType, String bookingRequired) {
		super();
		this.vehicleType = vehicleType;
		this.vehicleMake = vehicleMake;
		this.vehicleLicense = vehicleLicense;
		this.vehicleEngineType = vehicleEngineType;
	}

	public String getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}

	public String getVehicleMake() {
		return vehicleMake;
	}

	public void setVehicleMake(String vehicleMake) {
		this.vehicleMake = vehicleMake;
	}

	public String getVehicleLicense() {
		return vehicleLicense;
	}

	public void setVehicleLicense(String vehicleLicense) {
		this.vehicleLicense = vehicleLicense;
	}

	public String getVehicleEngineType() {
		return vehicleEngineType;
	}

	public void setVehicleEngineType(String vehicleEngineType) {
		this.vehicleEngineType = vehicleEngineType;
	}
	
}
