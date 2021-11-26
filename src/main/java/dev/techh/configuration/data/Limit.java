package dev.techh.configuration.data;

public class Limit{
	private String key;
	private String mode;
	private int timeSingle;
	private int count;
	private int timeTotal;

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

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
