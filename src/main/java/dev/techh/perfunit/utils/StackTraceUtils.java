package dev.techh.perfunit.utils;

import dev.techh.perfunit.exception.LimitReachedException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class StackTraceUtils {

    public static String stackTraceToString(LimitReachedException exception) { // TODO Improve way to normalize stack trace
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        exception.printStackTrace(pw);
        String stackString = sw.getBuffer().toString();
        stackString = stackString.substring( stackString.indexOf("\tat") );
        return stackString;
    }

    public static long stackId(String stack) {
        // using checksum instead of hashing due to performance reason
        Checksum crc32 = new CRC32();
        byte[] bytes = stack.getBytes(StandardCharsets.UTF_8);
        crc32.update(bytes, 0, bytes.length);
        return crc32.getValue();
    }

}
