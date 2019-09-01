package com.ref.fdr;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class FdrWrapper {

    private static FdrWrapper instance;
    private static boolean isLoaded;
    private URLClassLoader urlCl;
    private Class fdrClass;
    private Class<?> sessionClass;
    private Class<?> assertionClass;
    private Class<?> counterexampleClass;
    private Class<?> traceCounterexampleClass;
    private Class<?> debugContextClass;
    private Class<?> refinementCounterexampleClass;
    private Class<?> behaviourClass;
    private Class<?> irrelevantBehaviourClass;
    private Class<?> compiledEventListClass;
    private Class<?> TraceBehaviour;
    private Class<?> Node;
    private Class<?> ProcessName;
    private Class<?> Canceller;
    private Class<?> Machine;
    private Class<?> TransitionList;
    private Class<?> Transition;
    private File fdrJar;
    private List<String> classes;
    private Object session;
    private List<Object> counterExamples;
    private List<Object> assertions;

    public boolean loadFDR(String path) {

        File file = new File(path);
        if (file.exists()) {
            fdrJar = file;
            return true;
        } else
            return false;
    }

    private FdrWrapper() {

    }

    public static FdrWrapper getInstance() {
        if (instance == null) {
            instance = new FdrWrapper();
        }

        return instance;
    }

    public void loadClasses() throws MalformedURLException, ClassNotFoundException {

        classes = new ArrayList<String>();
        urlCl = new URLClassLoader(new URL[]{fdrJar.toURI().toURL()}, System.class.getClassLoader());
        fdrClass = urlCl.loadClass("uk.ac.ox.cs.fdr.fdr");
        sessionClass = urlCl.loadClass("uk.ac.ox.cs.fdr.Session");
        assertionClass = urlCl.loadClass("uk.ac.ox.cs.fdr.Assertion");
        counterexampleClass = urlCl.loadClass("uk.ac.ox.cs.fdr.Counterexample");
        traceCounterexampleClass = urlCl.loadClass("uk.ac.ox.cs.fdr.TraceCounterexample");
        debugContextClass = urlCl.loadClass("uk.ac.ox.cs.fdr.DebugContext");
        refinementCounterexampleClass = urlCl.loadClass("uk.ac.ox.cs.fdr.RefinementCounterexample");
        behaviourClass = urlCl.loadClass("uk.ac.ox.cs.fdr.Behaviour");
        irrelevantBehaviourClass = urlCl.loadClass("uk.ac.ox.cs.fdr.IrrelevantBehaviour");
        compiledEventListClass = urlCl.loadClass("uk.ac.ox.cs.fdr.CompiledEventList");
        TraceBehaviour = urlCl.loadClass("uk.ac.ox.cs.fdr.TraceBehaviour");
        Node = urlCl.loadClass("uk.ac.ox.cs.fdr.Node");
        ProcessName = urlCl.loadClass("uk.ac.ox.cs.fdr.ProcessName");
        Machine = urlCl.loadClass("uk.ac.ox.cs.fdr.Machine");
        TransitionList = urlCl.loadClass("uk.ac.ox.cs.fdr.TransitionList");
        Transition = urlCl.loadClass("uk.ac.ox.cs.fdr.Transition");
        classes.add(fdrClass.getName());
        classes.add(sessionClass.getName());
        classes.add(assertionClass.getName());
        classes.add(counterexampleClass.getName());
        classes.add(traceCounterexampleClass.getName());
        classes.add(debugContextClass.getName());
        classes.add(refinementCounterexampleClass.getName());
        classes.add(behaviourClass.getName());
        classes.add(TraceBehaviour.getName());
        classes.add(Node.getName());
        classes.add(ProcessName.getName());
        classes.add(TransitionList.getName());
        classes.add(Transition.getName());
        // Classes extras que são usadas como parametro
        Canceller = urlCl.loadClass("uk.ac.ox.cs.fdr.Canceller");
        classes.add(Canceller.getName());
    }

    public List<String> getClasses() {

        return classes;

    }

    public void loadFile(String filename) {
        try {
            this.session = sessionClass.newInstance();
            File f = new File(filename);
            if (f.exists()) {
                System.out.println("O arquivo existe");
            }
            invokeProperty(session.getClass(), this.session, "loadFile", String.class, filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Object> getAssertions() {
        List<Object> assertions = new ArrayList<>();
        try {
            for (Object assertion : (Iterable<?>) invokeProperty(session.getClass(), this.session, "assertions", null,
                    null)) {
                assertions.add(assertion);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return assertions;
    }

    public boolean executeAssertions(List<Object> assertions) {
        this.counterExamples = new ArrayList<>();
        boolean isRefinement = true;
        try {
            for (Object assertion : assertions) {
                invokeProperty(assertion.getClass(), assertion, "execute", Canceller, null);
                for (Object counterExample : (Iterable<?>) invokeProperty(assertion.getClass(), assertion,
                        "counterexamples", null, null)) {
                    this.counterExamples.add(counterExample);
                    isRefinement = false;
                }
            }
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isRefinement;
    }

    public List<Object> getCounterExamples() {
        return this.counterExamples;
    }

    public String getErrorEvent(Object counterExample) {
        String errorEvent = "";
        try {
            Object error = invokeProperty(traceCounterexampleClass, counterExample, "errorEvent", null, null);
            if ((Long) error != 1 && (Long) error != 0) {
                errorEvent = invokeProperty(sessionClass, this.session, "uncompileEvent", long.class, (Long) error).toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return errorEvent;
    }

    public String getTraceSpecificationBehaviour(Object counterExample) {
        String result = "";
        result = getBehaviour(counterExample, "specificationBehaviour");

        return result;
    }

    private String getBehaviour(Object counterExample, String type) {
        try {
            Field IMPL_LOOKUP = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            IMPL_LOOKUP.setAccessible(true);
            MethodHandles.Lookup lkp = (MethodHandles.Lookup) IMPL_LOOKUP.get(null);
            MethodHandle h1 = lkp.findSpecial(refinementCounterexampleClass, type,
                    MethodType.methodType(behaviourClass), traceCounterexampleClass);
            Object behaviour = h1.invoke(counterExample);
            type = traceBehaviour(behaviour);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return type;
    }

    public String getTraceImplementationBehaviour(Object counterExample) {
        String result = "";
        result = getBehaviour(counterExample, "implementationBehaviour");
        return result;
    }
//
//    public List<String> strictCounterExample(Object counterExample, List<String> trace) throws Exception {
//        StringBuilder sb = new StringBuilder();
//
//        // Adiciona o trace do contraExemplo
//        if (counterExample.getClass().getName().equals(traceCounterexampleClass.getName())) {
//            Field IMPL_LOOKUP = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
//            IMPL_LOOKUP.setAccessible(true);
//            MethodHandles.Lookup lkp = (MethodHandles.Lookup) IMPL_LOOKUP.get(null);
//            MethodHandle h1 = lkp.findSpecial(refinementCounterexampleClass, "specificationBehaviour",
//                    MethodType.methodType(behaviourClass), traceCounterexampleClass);
//            Object behaviour = null;
//            try {
//                behaviour = h1.invoke(counterExample);
//                traceBehaviour(behaviour, sb, session);
//                trace.add(sb.toString());
//                //System.out.println(sb.toString());
//                sb = new StringBuilder();
//                h1 = lkp.findSpecial(refinementCounterexampleClass, "implementationBehaviour",
//                        MethodType.methodType(behaviourClass), traceCounterexampleClass);
//                behaviour = h1.invoke(counterExample);
//                traceBehaviour(behaviour);
//                trace.add(sb.toString());
//            } catch (Throwable e) {
//                e.printStackTrace();
//            }
//        }
//        return trace;
//    }

    public List<String> traceCounterExample(Object counterExample) {
        List<String> trace = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        try {
            Constructor[] constructors = debugContextClass.getConstructors();
            Constructor constructor = null;
            for (int i = 0; i < constructors.length; i++) {
                Class[] parameters = constructors[i].getParameterTypes();
                if (parameters[0].getName().equals(refinementCounterexampleClass.getName())) {
                    constructor = constructors[i];
                }
            }
            Object debugContext = constructor.newInstance(counterExample, true);
            int iteration = 0;
            invokeProperty(debugContextClass, debugContext, "initialise", Canceller, null);
            for (Object behaviour : (Iterable<?>) invokeProperty(debugContextClass, debugContext, "rootBehaviours",
                    null, null)) {
                if(iteration == 1) {
                    System.out.println("Started behaviour " + iteration);
                    for (Object event : (Iterable<?>) invokeProperty(debugContextClass, debugContext, "revealTausInTrace",
                            behaviourClass, behaviour)) {
                        if ((Long) event != 0) {
                            Object result = invokeProperty(sessionClass, this.session, "uncompileEvent", long.class, event);
                            System.out.println("added " + result.toString());
                            sb.append(result.toString() + ", ");
                        }
//                    Object result = invokeProperty(sessionClass, this.session, "uncompileEvent", long.class, event);
//                    System.out.println("loop Tau " + event);
                    }
                }
                iteration++;
//                trace.add(describeBehaviour(behaviour));

//                break; só realiza uma assertion
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        trace.add(sb.toString());
        return trace;
    }

    private String describeBehaviour(Object behaviour) throws Exception {

        StringBuilder sb = new StringBuilder();


        for (Long event : (Iterable<Long>) invokeProperty(behaviourClass, behaviour, "trace", null, null)) {
            System.out.println("loop trace" + event);
            if (event == 1 || event == 0) {
                System.out.println("Tal :" + event);
//                Object result = invokeProperty(sessionClass, this.session, "uncompileEvent", long.class, event);
                // sb.append("-, ");
            } else {
                Object result = invokeProperty(sessionClass, this.session, "uncompileEvent", long.class, event);
                System.out.println(result.toString());
                sb.append(result.toString() + ", ");
            }
        }
        return sb.toString();
    }

    private String traceBehaviour(Object behaviour) throws Exception {
        StringBuilder sb = new StringBuilder();
        Object machine = invokeProperty(behaviourClass, behaviour, "machine", null, null);
        Object node = invokeProperty(Machine, machine, "rootNode", null, null);
        Object transitionList;
        while (true) {
            transitionList = invokeProperty(Machine, machine, "transitions", Node, node);
            Object evento = invokeProperty(TransitionList, transitionList, "get", int.class, 0);
            Object eventID = invokeProperty(Transition, evento, "event", null, null);
            Object result = invokeProperty(sessionClass, this.session, "uncompileEvent", long.class, (Long) eventID);
            // System.out.println(result.toString());
            if (!result.equals("τ")) {
                sb.append(result.toString());
            }
            if (result.toString().equals("endInteraction"))
                break;
            sb.append(", ");
            node = invokeProperty(Transition, evento, "destination", null, null);
        }
        return sb.toString();
    }

    private static Object invokeProperty(Class<?> dsClass, Object ds, String propertyName, Class<?> paramClass, Object paramValue) throws Exception {
        Method method;
        try {

            if (paramClass != null) {
                method = dsClass.getMethod(propertyName, paramClass);
                method.setAccessible(true);
                return method.invoke(ds, paramValue);

            } else {

                method = dsClass.getMethod(propertyName);
                method.setAccessible(true);
                return method.invoke(ds);

            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }
}
