
### Requirements before starting

To use this tool, you need to have a file that contains all the function definitions in json format. Also, the functions should already be deployed to a cloud provider and the corresponding resource links need to be provided in `resources.toml`.

### Generate an AFCL file

#### 1. Transform the filePaths to URIs in the function definition input file (necessary step):

`python3 pathsToUris.py <function-definitions-file(.json)> <bucketUriPrefix>`

#### 2. Generate the AFCL file:

`mvn clean install`

`java -jar target/FCifier-1.0-SNAPSHOT-jar-with-dependencies.jar <function-definitions-file(.json)> <AFCL-output-file(.yaml)> WORFKLOWNAME(optional) CONCURRENCY_LIMIT(optional)`

Example: `java -jar target/FCifier-1.0-SNAPSHOT-jar-with-dependencies.jar input.json workflow.yaml montage 100`

OR

Use main() method in org.fcifier.Main
