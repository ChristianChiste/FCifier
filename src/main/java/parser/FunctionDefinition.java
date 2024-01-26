package parser;

import java.util.List;

public class FunctionDefinition {

    private int order;

    private List<String> downloadUris;

    private List<String> uploadUris;

    private List<PassedVariable> inputVariables;

    private List<PassedVariable> outputVariables;

    private boolean parallel;

    private List<Object> forEachIterations;

    public int getOrder() {
        return order;
    }

    public List<String> getDownloadUris() {
        return downloadUris;
    }

    public List<String> getUploadUris() {
        return uploadUris;
    }

    public List<PassedVariable> getInputVariables() {
        return inputVariables;
    }

    public List<PassedVariable> getOutputVariables() {
        return outputVariables;
    }

    public boolean isParallel() {
        return parallel;
    }

    public List<Object> getForEachIterations() {
        return forEachIterations;
    }
}
