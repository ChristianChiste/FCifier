package factory;

import at.uibk.dps.afcl.Function;
import at.uibk.dps.afcl.functions.AtomicFunction;
import at.uibk.dps.afcl.functions.objects.DataIns;
import at.uibk.dps.afcl.functions.objects.DataOutsAtomic;
import at.uibk.dps.afcl.functions.objects.PropertyConstraint;
import enums.DataType;
import org.yaml.snakeyaml.Yaml;
import parser.FunctionDefinition;
import parser.PassedVariable;
import utils.WorkflowData;
import utils.WorkflowStrings;

import java.util.ArrayList;
import java.util.List;

public class AtomicFunctionFactory extends AbstractFunctionFactory {

    public Function createFunction(final FunctionDefinition functionDefinition, final String previousFunctionName) {
        return this.createFunction(functionDefinition, previousFunctionName, false);
    }

    public Function createFunction(final FunctionDefinition functionDefinition, final String previousFunctionName,
                                   final boolean isChild) {
        final AtomicFunction function = new AtomicFunction();

        function.setName("function_" + functionDefinition.getOrder());
        function.setType(DataType.COLLECTION.toString());

        function.setDataIns(this.createDataIns(functionDefinition, previousFunctionName, function.getName(), isChild));
        function.setDataOuts(this.createDataOuts(functionDefinition));

        PropertyConstraint propertyConstraint = new PropertyConstraint("resource", "{{function_" + functionDefinition.getOrder() + "_resource}}");
        function.setProperties(List.of(propertyConstraint));

        return function;
    }

    private List<DataIns> createDataIns(final FunctionDefinition functionDefinition, final String previousFunctionName,
                                          final String currentFunctionName, final boolean isChild) {
        final List<DataIns> dataIns = new ArrayList<>();
        for (final PassedVariable passedVariable : functionDefinition.getInputVariables()) {
            if (passedVariable.isParallel()) {
                dataIns.add(new DataIns(passedVariable.getIdentifier(), passedVariable.getType(),
                        "parallelFor_" + functionDefinition.getOrder() + "/" + passedVariable.getIdentifier()));
            } else {
                dataIns.add(new DataIns(passedVariable.getIdentifier(), passedVariable.getType(),
                        previousFunctionName.isBlank() ? WorkflowData.getWorkflowName() + passedVariable.getIdentifier() :
                                !isChild ? previousFunctionName + "/" + passedVariable.getIdentifier() :
                        "parallelFor_" + functionDefinition.getOrder() + "/" + passedVariable.getIdentifier()));
            }
        }

        DataIns dataIn = new DataIns("enableDirectoryMonitoring", "bool");
        dataIn.setValue("false");
        dataIns.add(dataIn);

        if (isChild) {

            DataIns dataInsDownload = new DataIns(WorkflowStrings.DOWNLOAD_URIS, DataType.COLLECTION.toString(),
                    "parallelFor_" + functionDefinition.getOrder() + "/" + "downloadUris");

            DataIns dataInsUpload =  new DataIns(WorkflowStrings.UPLOAD_URIS, DataType.COLLECTION.toString(),
                    "parallelFor_" + functionDefinition.getOrder() + "/" + "uploadUris");

            dataIns.addAll(List.of(dataInsDownload, dataInsUpload));

        } else {

            DataIns dataInsDownload = new DataIns("downloadUris", DataType.COLLECTION.toString());
            dataInsDownload.setValue(new Yaml().dump(functionDefinition.getDownloadUris()));

            DataIns dataInsUpload = new DataIns("uploadUris", DataType.COLLECTION.toString());
            dataInsUpload.setValue(new Yaml().dump(functionDefinition.getUploadUris()));

            dataIns.addAll(List.of(dataInsDownload, dataInsUpload));

        }

        return dataIns;
    }

    private List<DataOutsAtomic> createDataOuts(final FunctionDefinition functionDefinition) {
        final List<DataOutsAtomic> dataOuts = new ArrayList<>();
        for (final PassedVariable passedVariable : functionDefinition.getOutputVariables()) {
            dataOuts.add(new DataOutsAtomic(passedVariable.getIdentifier(), passedVariable.getType()));
        }
        return dataOuts;
    }

}
