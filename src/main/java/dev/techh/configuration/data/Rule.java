package dev.techh.configuration.data;

public class Rule {

	private String description = "No description";
	private boolean allowUnknownCalls = true;
	private boolean allowFail = false;
	private String key = "traceId";
	private Limit limit = new Limit();
	private boolean printTrace = false;
	private boolean onlyPublic = true;

	public Rule() {
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public boolean isAllowFail() {
		return allowFail;
	}

	public void setAllowFail(boolean allowFail) {
		this.allowFail = allowFail;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public boolean isOnlyPublic() {
		return onlyPublic;
	}

	public void setOnlyPublic(boolean onlyPublic) {
		this.onlyPublic = onlyPublic;
	}
}
