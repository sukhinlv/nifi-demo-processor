# Sample of a custom NiFi processor

Here you can find examples:
- an example of setting up processor properties and relationships;
- use of the nifi service as a processor property;
- reading the contents of the incoming flow file, writing attributes and data to the outgoing flow file;
- example of error handling;
- example of a test using a stub service.

The example skeleton was created using the command
```shell
mvn archetype:generate -DarchetypeGroupId=org.apache.nifi -DarchetypeArtifactId=nifi-processor-bundle-archetype -DarchetypeVersion=1.22.0 -DnifiVersion=1.22.0
```
See also [Custom Processors for Apache NiFi](https://bryanbende.com/development/2015/02/04/custom-processors-for-apache-nifi)

# Build

Build should be made by two separate commands:
```shell
mvn clean verify
mvn nifi-nar:nar
```

First you need to build the project and then the NiFi .nar processor file. You should put this file in the /extensions folder in your NiFi path. After that you can add the test processor to your process groups.

Keep in mind that if you overwrite processor that already existed in the /extensions folder, you will have to restart NiFi server to force NiFi to reload processors.
