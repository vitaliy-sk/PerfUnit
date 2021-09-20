package dev.techh;

import dev.techh.transformer.PerfUnitTransformer;

import java.lang.instrument.Instrumentation;

public class PerfUnit {

    public static void premain(String arguments, Instrumentation instrumentation) {

        instrumentation.addTransformer(new PerfUnitTransformer());

    }

}
