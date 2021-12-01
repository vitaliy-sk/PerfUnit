package dev.techh.perfunit.transformer;

import dev.techh.perfunit.configuration.data.Configuration;
import dev.techh.perfunit.configuration.data.Rule;
import jakarta.inject.Singleton;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.Descriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.ClassFileTransformer;
import java.lang.invoke.MethodHandles;
import java.security.ProtectionDomain;
import java.util.Map;
import java.util.Set;

@Singleton
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
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {

        String javaClassName = Descriptor.toJavaName(className);

        if (!includedClasses.contains(javaClassName)) return classfileBuffer;

        LOG.info("|- Class [{}]", javaClassName);

        try {
            ClassPool cp = ClassPool.getDefault();
            cp.childFirstLookup = true;

            cp.insertClassPath(new LoaderClassPath(loader));

            CtClass ctClass = cp.get(javaClassName);
            CtMethod[] methods = ctClass.getDeclaredMethods();

            for (CtMethod method : methods) {
                Map.Entry<String, Rule> rule = getRule(ctClass, method);

                if (rule != null) {
                    if (rule.getValue().isOnlyPublic() && !isPublic(method)) continue;

                    LOG.info("\t|-[+] Method [{}]", method.getLongName());
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
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        return transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
    }

    private void transform(Map.Entry<String, Rule> rule,
                           CtClass ctClass, CtMethod method) throws CannotCompileException {

        // Skip methods without body
        if (method.getMethodInfo().getCodeAttribute() == null) return;

        method.addLocalVariable("perfUnit_Timer", CtClass.longType);
        method.insertBefore("perfUnit_Timer = System.currentTimeMillis();");

        String methodSignatureKey = getMethodSignatureKey(method, ctClass.getName());
        method.insertAfter(String.format(
                "dev.techh.perfunit.collector.PerfUnitCollector.getInstance().onInvoke(\"%s\", \"%s\", perfUnit_Timer);",
                rule.getKey(), methodSignatureKey));
    }

    private Map.Entry<String, Rule> getRule(CtClass ctClass, CtMethod method) {
        String classKey = ctClass.getName();
        if (rules.containsKey(classKey)) return getRule(classKey);

        String methodKey = String.format("%s#%s", classKey, method.getName());
        if (rules.containsKey(methodKey)) return getRule(methodKey);

        String methodSignatureKey = getMethodSignatureKey(method, classKey);
        if (rules.containsKey(methodSignatureKey)) return getRule(methodSignatureKey);

        return null;
    }

    private String getMethodSignatureKey(CtMethod method, String name) {
        return String.format("%s#%s%s", name, method.getName(), Descriptor.toString(method.getSignature()));
    }

    private boolean isPublic(CtMethod method) {
        return method.getMethodInfo() != null && AccessFlag.isPublic(method.getMethodInfo().getAccessFlags());
    }

    private Map.Entry<String, Rule> getRule(String key) {
        return Map.entry(key, rules.get(key));
    }


}
