# jsdrplay
Java wrapper for [SDRplay](https://www.sdrplay.com/) API

**Note: this is a work in progress and is not yet released.**

## Requirements
* Java 17.  Uses [Project Panama](https://openjdk.java.net/projects/panama/) foreign function and foreign memory access incubator features.
* [SDRplay API](https://www.sdrplay.com/start-here/) installed in runtime environment

## Supported API Version(s): 
* [3.07](https://www.sdrplay.com/docs/SDRplay_API_Specification_v3.07.pdf)
* [3.08](https://www.sdrplay.com/docs/SDRplay_API_Specification_v3.08.pdf)

## Supported Operating Systems:
* Linux, MacOS, and Windows.

## JDK 17 Project Panama

This library uses the new incubator features from JDK 17 Project Panama and the jextract tool to auto-generate java code to interface with native libararies.  The 
jextract tool generates code from the native header file:

### Generating Source Code
1. Install the sdrplay api
2. Locate the library headers.
   * Linux: /usr/local/include/*.h
   * Windows: C:\Program Files\SDRplay\API\inc\*.h
   * MacOS: ??
4. cd into the library source directory: (project)/jsdrplay/sdrplay-api/src/main/java/
5. Run: **jextract -t io.github.dsheirer.sdrplay.api /usr/local/include/sdrplay_api.h -l libsdrplay_api --source**

### IntelliJ setup
1. Run configuration
2. JVM Options: --enable-native-access=ALL-UNNAMED,sdrplay.api,io.github.dsheirer.sdrplay -Djava.library.path=/usr/local/lib
3. Add the sdrplay library location to the java.library.path:
   Linux: /usr/local/lib
   Windows: ??
   MacOS: ??

