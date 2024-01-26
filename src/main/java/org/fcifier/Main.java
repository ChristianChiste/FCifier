package org.fcifier;

import at.uibk.dps.afcl.Workflow;
import at.uibk.dps.afcl.utils.Utils;
import builder.WorkflowBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moandjiezana.toml.Toml;
import org.yaml.snakeyaml.Yaml;
import parser.FunctionDefinition;
import utils.WorkflowData;
import utils.WorkflowStrings;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        try {

            String inputFilePath = "./function_definitions.json";
            String outputFilePath = "./workflow.yaml";
            String workflowName = "montage";
            int concurrencyLimit = -1;

            if (args.length > 0) {
                inputFilePath = args[0];
                outputFilePath = args[1];
                workflowName = args.length > 2 ? args[2] : "workflowName";
                concurrencyLimit = args.length > 3 ? Integer.parseInt(args[3]) : -1;
            }

            WorkflowData.setWorkflowName(workflowName);
            WorkflowData.setConcurrencyLimit(concurrencyLimit);

            List<FunctionDefinition> functions = new Gson().fromJson(readFile(inputFilePath), new TypeToken<List<FunctionDefinition>>(){}.getType());

            final WorkflowBuilder workflowBuilder = new WorkflowBuilder(functions);
            final Workflow workflow = workflowBuilder.buildWorkflow();

            Utils.writeYaml(workflow, outputFilePath, "./schema.json");

            replacePlaceholdersWithResourceLinks(outputFilePath, "resources.toml");

        } catch (final Exception e) {
            System.out.println("Failure: " + Arrays.toString(e.getStackTrace()));
        }

    }

    private static String readFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    private static void replacePlaceholdersWithResourceLinks(final String inputFilePath, String configFilePath) throws Exception {
        Toml toml = new Toml();
        try (Reader tomlReader = new FileReader(configFilePath)) {
            Map<String, Object> tomlData = toml.read(tomlReader).toMap();

            Yaml yaml = new Yaml();
            try (Reader yamlTemplateReader = new FileReader(inputFilePath)) {
                Map<String, Object> yamlData = yaml.load(yamlTemplateReader);

                replacePlaceholders(yamlData, tomlData);

                YAMLFactory yf = new YAMLFactory();
                yf.disable(YAMLGenerator.Feature.USE_NATIVE_TYPE_ID);
                ObjectMapper objectMapper = new ObjectMapper(yf);
                byte[] bytes = objectMapper.writeValueAsBytes(yamlData);
                File file = new File(inputFilePath);

                writeBytes(file, bytes);
            }
        }
    }

    private static void replacePlaceholders(Object obj, Map<String, Object> tomlData) {
        if (obj instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) obj;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof String && ((String) value).contains("{{") && ((String) value).contains("}}")) {
                    String placeholder = ((String) value).replaceAll("\\{\\{\\s*([^\\s]+)\\s*}}", "$1");
                    if (tomlData.containsKey(placeholder)) {
                        map.put(entry.getKey(), tomlData.get(placeholder));
                    }
                } else if (value instanceof Map) {
                    replacePlaceholders(value, tomlData);
                } else if (value instanceof List) {
                    replacePlaceholdersInList((List<Object>) value, tomlData);
                }
            }
        } else if (obj instanceof List) {
            replacePlaceholdersInList((List<Object>) obj, tomlData);
        }
    }

    private static void replacePlaceholdersInList(List<Object> list, Map<String, Object> tomlData) {
        for (int i = 0; i < list.size(); i++) {
            Object element = list.get(i);
            if (element instanceof Map) {
                replacePlaceholders(element, tomlData);
            } else if (element instanceof List) {
                replacePlaceholdersInList((List<Object>) element, tomlData);
            }
        }
    }

    private static void writeBytes(File file, byte[] bytes) throws IOException {

        OutputStream fileOutputStream = Files.newOutputStream(Paths.get(file.getName()));
        Throwable var3 = null;

        try {
            fileOutputStream.write(bytes);
        } catch (Throwable var13) {
            var3 = var13;
            throw var13;
        } finally {
            if (fileOutputStream != null) {
                if (var3 != null) {
                    try {
                        fileOutputStream.close();
                    } catch (Throwable var12) {
                        var3.addSuppressed(var12);
                    }
                } else {
                    fileOutputStream.close();
                }
            }

        }
    }

}
