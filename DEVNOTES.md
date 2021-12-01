### Report

Summary:

* List of rules with invocation stat
* List of violated rules with violation count

Per rule:

* Violation count
* Unique (!) thread dumps with violation counter

### Required data to store

* Invocation stat per rule (In-memory) ( rule_id, invocation_stat )
* Invocation stat per rule + tracing unit (In-memory, limited size) ( rule_id + tracing_id, invocation_stat )

* Violations per rule (In-memory) ( rule_id, violation_counter )
* Violations per rule + dump (In-memory) ( rule_id,  < dump_id, violation_counter > )

* Unique thread dumps (Limited in-memory + Disk write) ( < dump_id, stack_trace_string> )

### TODO

* Switcher for reporters
