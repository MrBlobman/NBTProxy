# NBTProxy
An NBT API designed for Spigot plugins to interact with NBT tags.

A full tutorial on how to use the fancy serialization is on the wiki which
you can visit by clicking [here](https://github.com/MrBlobman/NBTProxy/wiki)
and the javadocs are over [here](http://mrblobman.github.io/NBTProxy/docs/)

Using it in your project
========================

There are 2 options:

1. Publish the project to your local maven repository and add it to your
   build script/pom.
   ```
   ./gradlew publishApiPublicationToMavenLocal
   ```
   Then add a gradle or maven dependency with the following:
       Gradle:
       ```gradle
          compile group: 'io.github.mrblobman', name: 'NBTProxy-api', version: '1.1.0'
       ```
       Maven:
       ```xml
       <dependency>
           <groupId>io.github.mrblobman</groupId>
           <artifactId>NBTProxy-api</artifactId>
           <version>1.1.0</version>
           <scope>provided</scope>
       </dependency>
       ```

2. Add the api jar to your classpath. The jars can be downloaded from the releases
   tab. [Link to releases](https://github.com/MrBlobman/NBTProxy/releases)
   
Checking if the server is running a supported version
=====================================================

Make sure you add "NBTProxy" as a dependency in your plugin.yml.

[TagFactory](http://mrblobman.github.io/NBTProxy/docs/io/github/mrblobman/nbt/TagFactory.html)
is one of the core utility classes and the `get()` method will retrieve the appropriate instance
for the nms version running on the server. The error message is fairly descriptive and printing
it and then disabling the plugin should be sufficient for informing the server administrator.
```java
try {
    TagFactory factory = TagFactory.get();
} catch (UnsupportedOperationException e) {
    //Code to run if there is not a compatible version running on the server
}
```