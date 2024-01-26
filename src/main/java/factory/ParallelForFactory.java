package factory;

import at.uibk.dps.afcl.Function;
import at.uibk.dps.afcl.functions.ParallelFor;
import at.uibk.dps.afcl.functions.objects.*;
import enums.DataType;
import org.yaml.snakeyaml.Yaml;
import parser.FunctionDefinition;
import parser.PassedVariable;
import utils.ListUtils;
import utils.WorkflowData;
import utils.WorkflowStrings;

import java.util.ArrayList;
import java.util.List;

public class ParallelForFactory extends AbstractFunctionFactory {

    public Function createFunction(final FunctionDefinition functionDefinition, final String previousFunctionName) {
        final ParallelFor parallelFor = new ParallelFor();
        parallelFor.setName("parallelFor_" + functionDefinition.getOrder());

        parallelFor.setDataIns(this.createDataIns(functionDefinition, previousFunctionName));
        parallelFor.setDataOuts(this.createDataOuts(functionDefinition));

        int concurrencyLimit = WorkflowData.getConcurrencyLimit();
        if (concurrencyLimit != -1) {
            final PropertyConstraint concurrency = new PropertyConstraint("concurrency", String.valueOf(concurrencyLimit));
            parallelFor.setConstraints(List.of(concurrency));
        }

        final LoopCounter loopCounter = new LoopCounter("loopCounter", "number", "0",
                String.valueOf(functionDefinition.getForEachIterations().size()), "1");
        parallelFor.setLoopCounter(loopCounter);

        final AtomicFunctionFactory atomicFunctionFactory = new AtomicFunctionFactory();
        final Function function = atomicFunctionFactory.createFunction(functionDefinition, previousFunctionName, true);

        final List<Function> loopBody = new ArrayList<>();
        loopBody.add(function);
        parallelFor.setLoopBody(loopBody);

        return parallelFor;
    }

    private List<DataIns> createDataIns(final FunctionDefinition functionDefinition, final String previousFunctionName) {
        final List<DataIns> dataIns = new ArrayList<>();
        for (final PassedVariable passedVariable : functionDefinition.getInputVariables()) {
            if (!passedVariable.isParallel()) {
                DataIns dataIn = new DataIns(passedVariable.getIdentifier(), passedVariable.getType(),
                        previousFunctionName.isBlank() ? WorkflowData.getWorkflowName() + "/" + passedVariable.getIdentifier() :
                                previousFunctionName + "/" + passedVariable.getIdentifier());
                PropertyConstraint propertyConstraint = new PropertyConstraint("distribution", "REPLICATE(*)");
                dataIn.setConstraints(List.of(propertyConstraint));
                dataIns.add(dataIn);
            }
        }
        if (ListUtils.isNotEmpty(functionDefinition.getForEachIterations())) {
            DataIns dataIn = new DataIns("parallelFor_" + functionDefinition.getOrder(), DataType.COLLECTION.toString());
            dataIn.setValue(new Yaml().dump(functionDefinition.getForEachIterations()));
            PropertyConstraint propertyConstraint = new PropertyConstraint("distribution", "BLOCK(1)");
            dataIn.setConstraints(List.of(propertyConstraint));
            dataIns.add(dataIn);
        } else {
            DataIns dataIn = new DataIns(WorkflowStrings.DOWNLOAD_URIS, DataType.COLLECTION.toString());
            dataIn.setValue(new Yaml().dump(functionDefinition.getDownloadUris()));
            dataIns.add(dataIn);

            dataIn = new DataIns(WorkflowStrings.UPLOAD_URIS, DataType.COLLECTION.toString());
            dataIn.setValue(new Yaml().dump(functionDefinition.getUploadUris()));
            dataIns.add(dataIn);

        }
        return dataIns;
    }

    private List<DataOuts> createDataOuts(final FunctionDefinition functionDefinition) {
        final List<DataOuts> dataOuts = new ArrayList<>();
        for (final PassedVariable passedVariable : functionDefinition.getOutputVariables()) {
            dataOuts.add(new DataOuts(passedVariable.getIdentifier(), passedVariable.getType(), "parallelFor_" + functionDefinition.getOrder() + "/" + passedVariable.getIdentifier()));
        }
        return dataOuts;
    }

}
