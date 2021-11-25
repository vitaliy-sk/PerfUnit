package dev.techh.configuration.data;

public class Limit{
	private String mode;
	private int timeSingle;
	private int count;
	private int timeTotal;
	private String type;

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public int getTimeSingle() {
		return timeSingle;
	}

	public void setTimeSingle(int timeSingle) {
		this.timeSingle = timeSingle;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getTimeTotal() {
		return timeTotal;
	}

	public void setTimeTotal(int timeTotal) {
		this.timeTotal = timeTotal;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
