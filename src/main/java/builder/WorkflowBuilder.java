package builder;

import at.uibk.dps.afcl.Function;
import at.uibk.dps.afcl.Workflow;
import at.uibk.dps.afcl.functions.objects.DataIns;
import at.uibk.dps.afcl.functions.objects.DataOuts;
import enums.DataType;
import factory.AtomicFunctionFactory;
import factory.ParallelForFactory;
import parser.FunctionDefinition;
import parser.PassedVariable;
import utils.ListUtils;
import utils.WorkflowData;
import utils.WorkflowStrings;

import java.util.*;

public class WorkflowBuilder {

    private final List<FunctionDefinition> functionDefinitions;

    public WorkflowBuilder(final List<FunctionDefinition> functionDefinitions) {
        this.functionDefinitions = functionDefinitions;
    }

    public Workflow buildWorkflow() {

        final Workflow workflow = new Workflow();

        workflow.setName(WorkflowData.getWorkflowName());

        workflow.setDataIns(this.createDataIns());

        workflow.setWorkflowBody(this.createWorkflowBody());

        workflow.setDataOuts(this.createDataOuts());

        return workflow;

    }

    private List<DataIns> createDataIns() {
        final List<PassedVariable> inputs = functionDefinitions.get(0).getInputVariables();
        final List<DataIns> workflowInput = new ArrayList<>();
        for (final PassedVariable input : inputs) {
            workflowInput.add(new DataIns(input.getIdentifier(), input.getType()));
        }
        return workflowInput;
    }

    private List<Function> createWorkflowBody() {
        final List<Function> workflowBody = new ArrayList<>(functionDefinitions.size());
        final ParallelForFactory parallelForFactory = new ParallelForFactory();
        final AtomicFunctionFactory atomicFunctionFactory = new AtomicFunctionFactory();
        Function function = null;

        for (final FunctionDefinition functionDefinition : functionDefinitions) {
            if (functionDefinition.isParallel()) {
                function = parallelForFactory.createFunction(functionDefinition, function != null ? function.getName() : WorkflowData.getWorkflowName());
                workflowBody.add(function);
            } else {
                function = atomicFunctionFactory.createFunction(functionDefinition, function != null ? function.getName() : WorkflowData.getWorkflowName());
                workflowBody.add(function);
            }
        }
        return workflowBody;
    }

    private List<DataOuts> createDataOuts() {
        final List<PassedVariable> outputs = functionDefinitions.get(functionDefinitions.size() - 1).getOutputVariables();
        final List<DataOuts> workflowOutput = new ArrayList<>();
        for (final PassedVariable output : outputs) {
            workflowOutput.add(
                    new DataOuts(
                            output.getIdentifier(),
                            output.getType(),
                            String.valueOf(functionDefinitions.get(functionDefinitions.size() - 1).getOrder()))
            );
        }
        return workflowOutput;
    }

}
