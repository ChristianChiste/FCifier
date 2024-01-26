package utils;

public class WorkflowData {

    private static String workflowName;

    private static int concurrencyLimit;

    public static String getWorkflowName() {
        return workflowName;
    }

    public static void setWorkflowName(String name) {
        workflowName = name;
    }

    public static int getConcurrencyLimit() {
        return concurrencyLimit;
    }

    public static void setConcurrencyLimit(int limit) {
        concurrencyLimit = limit;
    }

}
