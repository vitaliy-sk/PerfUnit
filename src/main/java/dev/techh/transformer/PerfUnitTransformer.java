package dev.techh.transformer;

import dev.techh.configuration.data.Configuration;
import dev.techh.configuration.data.Rule;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import javassist.bytecode.Descriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.invoke.MethodHandles;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.Set;

public class PerfUnitTransformer implements ClassFileTransformer {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Set<String> includedClasses;
    private final Map<String, Rule> rules;

    public PerfUnitTransformer(Configuration configuration) {
        this.rules = configuration.getRules();
        this.includedClasses = configuration.getClasses();
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer)
            throws IllegalClassFormatException {


        String javaClassName = Descriptor.toJavaName(className);

        if (!includedClasses.contains(javaClassName)) return classfileBuffer;

        LOG.debug("|- Class [{}]", javaClassName);

        try {
            ClassPool cp = ClassPool.getDefault();
            cp.childFirstLookup = true;

            cp.insertClassPath(new LoaderClassPath(loader));

            CtClass ctClass = cp.get(javaClassName);
            CtMethod[] methods = ctClass.getDeclaredMethods();

            for (CtMethod method : methods) {
                Map.Entry<String, Rule> rule = getRule(ctClass, method);
                if (rule != null) {
                    LOG.debug("\t|-[+] Method [{}]", method.getLongName());
                    transform(rule, ctClass, method);
                }
            }

            classfileBuffer = ctClass.toBytecode();
            ctClass.detach();
        } catch (Exception ex) {
            LOG.error("Failed to transform class [{}]", className, ex);
        }

        return classfileBuffer;
    }

    @Override
    public byte[] transform(Module module, ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer)
            throws IllegalClassFormatException {
        return transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
    }

    private void transform(Map.Entry<String, Rule> rule,
                           CtClass ctClass, CtMethod method) throws CannotCompileException, NotFoundException {

        method.addLocalVariable("perfUnit_Rule", ctClass.getClassPool().get("java.lang.String"));
        method.insertBefore(String.format("String perfUnit_Rule = \"%s\";", rule.getKey()));

        method.addLocalVariable("perfUnit_Timer", CtClass.longType);
        method.insertBefore("perfUnit_Timer = System.currentTimeMillis();");

        method.insertAfter("dev.techh.collector.PerfUnitCollector.getInstance().onInvoke(perfUnit_Rule, perfUnit_Timer);");
    }

    private Map.Entry<String, Rule> getRule(CtClass ctClass, CtMethod method) {
        String classKey = ctClass.getName();
        if (rules.containsKey(classKey)) return getRule(classKey);

        String methodKey = String.format("%s#%s", classKey, method.getName());
        if (rules.containsKey(methodKey)) return getRule(methodKey);

        String methodSignatureKey = String.format("%s#%s%s", classKey, method.getName(), Descriptor.toString(method.getSignature()));
        if (rules.containsKey(methodSignatureKey)) return getRule(methodSignatureKey);

        return null;
    }

    private Map.Entry<String, Rule> getRule(String key) {
        return Map.entry(key, rules.get(key));
    }


}
