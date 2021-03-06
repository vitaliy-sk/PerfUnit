package dev.techh.perfunit.configuration.data;

import java.util.Objects;

public class Rule {

	private String id;
	private String description = "No description";
	private boolean allowUnknownCalls = true;
	private boolean allowFail = false;
	private String tracingKey = "traceId";
	private Limit limit = new Limit();
	private boolean onlyPublic = true;

	public Rule() {
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTracingKey() {
		return tracingKey;
	}

	public void setTracingKey(String tracingKey) {
		this.tracingKey = tracingKey;
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

	public boolean isOnlyPublic() {
		return onlyPublic;
	}

	public void setOnlyPublic(boolean onlyPublic) {
		this.onlyPublic = onlyPublic;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Rule rule = (Rule) o;
		return id.equals(rule.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Rule{");
		sb.append("id='").append(id).append('\'');
		sb.append(", description='").append(description).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
