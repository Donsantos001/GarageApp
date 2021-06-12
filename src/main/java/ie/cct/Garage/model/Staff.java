package ie.cct.Garage.model;

import java.util.ArrayList;

public class Staff {

	private String name;
	private ArrayList<Booking> task;
	
	
	public Staff() {
		super();
		task = new ArrayList<>();
		// TODO Auto-generated constructor stub
	}
	
	public Staff(String name, ArrayList<Booking> task) {
		super();
		this.name = name;
		this.task = task;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Booking> getTask() {
		return task;
	}

	public void setTask(ArrayList<Booking> task) {
		this.task = task;
	}

	public void addTask(Booking task) {
		this.task.add(task);
	}
}
