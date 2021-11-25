package dev.techh.transformer;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.bytecode.Descriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.invoke.MethodHandles;
import java.security.ProtectionDomain;
import java.util.Set;

public class PerfUnitTransformer implements ClassFileTransformer {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final Set<String> TO_TRANSFORM = Set.of(
            "com.mysql.cj.jdbc.ClientPreparedStatement", "dev.techh.integration.service.ExpensiveService"
    );

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer)
            throws IllegalClassFormatException {


        String javaClassName = Descriptor.toJavaName(className);

        if (!TO_TRANSFORM.contains(javaClassName)) return classfileBuffer;

        try {
            ClassPool cp = ClassPool.getDefault();
            cp.childFirstLookup = true;

            cp.insertClassPath(new LoaderClassPath(loader));

            CtClass ctClass = cp.get(javaClassName);
            LOG.info("Transforming class {}", javaClassName);

            CtMethod[] methods = ctClass.getDeclaredMethods();

            for (CtMethod method : methods) {
                transform(ctClass, method);
            }

            classfileBuffer = ctClass.toBytecode();
            ctClass.detach();
        } catch (Exception ex) {
            LOG.error("Failed to transform {}", className, ex);
        }

        return classfileBuffer;
}

    private void transform(CtClass ctClass, CtMethod method) throws CannotCompileException {
        method.insertAfter(String.format("dev.techh.collector.PerfUnitCollector.getInstance().onInvoke(\"%s\");", method.getName()));
    }

    @Override
    public byte[] transform(Module module, ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer)
            throws IllegalClassFormatException {
        return transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
    }


}
