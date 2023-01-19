package ru.aslastin;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public aspect ProfilingAspect {
    private final Map<String, Long> callCountMyMethodName = new HashMap<>();
    private final Map<String, Long> totalElapsedTimeByMethodName = new HashMap<>();

    private CallNode callParent = new CallNode(null);

    pointcut profileMethod(): execution(* *(..)) && within(@ru.aslastin.Profile *);

    Object around(): profileMethod() {
        boolean showCallTree = getShowCallTree(thisJoinPoint);
        String methodName = getMethodName(thisJoinPoint);

        CallNode prevCallParent = callParent;
        CallNode callNode = new CallNode(methodName);

        if (showCallTree) {
            callParent.addChild(callNode);
            callParent = callNode;
        }

        long startTimeNs = System.nanoTime();
        Object result = proceed();
        long elapsedTime = System.nanoTime() - startTimeNs;

        callCountMyMethodName.merge(methodName, 1L, Long::sum);
        totalElapsedTimeByMethodName.merge(methodName, elapsedTime, Long::sum);

        if (showCallTree) {
            callNode.setElapsedTime(elapsedTime);
            callParent = prevCallParent;
        }

        return result;
    }

    private boolean getShowCallTree(JoinPoint joinPoint) {
        Class<?> clazz = joinPoint.getStaticPart()
                .getSignature()
                .getDeclaringType();
        Profile profile = clazz.getAnnotation(Profile.class);
        return profile != null && profile.showCallTree();
    }

    private String getMethodName(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getStaticPart().getSignature();
        return methodSignature.toShortString();
    }

    pointcut mainMethod(): execution(public static void main(..));

    after(): mainMethod() {
        if (!callParent.getChildren().isEmpty()) {
            System.out.println();
            System.out.println("Method call tree:");
            printCallNode(callParent, 0, new ArrayList<>());
        }

        if (!callCountMyMethodName.isEmpty()) {
            System.out.println("Method profile summary:");

            List<String> orderedMethodNames = callCountMyMethodName.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .map(Map.Entry::getKey)
                    .toList();

            for (String methodName : orderedMethodNames) {
                long callCount = callCountMyMethodName.get(methodName);
                long totalElapsedTimed = totalElapsedTimeByMethodName.get(methodName);

                System.out.printf("- %s: %d call(s), sum %d ns, avg %d ns%n",
                        methodName, callCount, totalElapsedTimed, totalElapsedTimed / callCount);
            }
        }
    }

    private void printCallNode(CallNode callNode, int depth, List<Boolean> isLastAtDepthList) {
        for (int i = 0; i < isLastAtDepthList.size() - 1; i++) {
            System.out.print(isLastAtDepthList.get(i) ? "   " : "|  ");
        }
        if (depth > 0) {
            System.out.printf("+- %s: %d ns%n", callNode.getMethodName(), callNode.getElapsedTime());
        }

        List<CallNode> children = callNode.getChildren();
        for (int i = 0; i < children.size(); i++) {
            CallNode child = children.get(i);
            isLastAtDepthList.add(i == children.size() - 1);
            printCallNode(child, depth + 1, isLastAtDepthList);
            isLastAtDepthList.remove(isLastAtDepthList.size() - 1);
        }
    }

    private static class CallNode {
        final String methodName;
        long elapsedTime;
        final List<CallNode> children;

        private CallNode(String methodName) {
            this.methodName = methodName;
            this.children = new ArrayList<>();
        }

        void setElapsedTime(long timeElapsed) {
            this.elapsedTime = timeElapsed;
        }

        void addChild(CallNode callNode) {
            children.add(callNode);
        }

        String getMethodName() {
            return methodName;
        }

        long getElapsedTime() {
            return elapsedTime;
        }

        List<CallNode> getChildren() {
            return children;
        }
    }
}
