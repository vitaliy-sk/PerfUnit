package dev.techh.configuration.data;

public class Rule {
	private String description;
	private String method;
	private boolean allowUnknownCalls;
	private Limit limit;
	private boolean printTrace;

	public Rule() {
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public boolean isAllowUnknownCalls() {
		return allowUnknownCalls;
	}

	public void setAllowUnknownCalls(boolean allowUnknownCalls) {
		this.allowUnknownCalls = allowUnknownCalls;
	}

	public Limit getLimit() {
		return limit;
	}

	public void setLimit(Limit limit) {
		this.limit = limit;
	}

	public boolean isPrintTrace() {
		return printTrace;
	}

	public void setPrintTrace(boolean printTrace) {
		this.printTrace = printTrace;
	}

}
