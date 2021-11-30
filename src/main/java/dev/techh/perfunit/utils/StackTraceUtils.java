package dev.techh.perfunit.utils;

import dev.techh.perfunit.exception.LimitReachedException;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StackTraceUtils {

    public static String stackTraceToString(LimitReachedException exception) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        exception.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

}
