# jsdrplay
Java wrapper for [SDRplay](https://www.sdrplay.com/) API

**Note: this is a work in progress and is not yet released.**

## Requirements
* Java 18.  Uses [Project Panama](https://openjdk.java.net/projects/panama/) foreign function and foreign memory access 
  incubator features.
* [jextract](https://github.com/openjdk/panama-foreign/blob/foreign-jextract/doc/panama_jextract.md)
* [SDRplay API](https://www.sdrplay.com/start-here/) installed in runtime environment

## Supported API Version(s): 
* [3.07](https://www.sdrplay.com/docs/SDRplay_API_Specification_v3.07.pdf)
* [3.08](https://www.sdrplay.com/docs/SDRplay_API_Specification_v3.08.pdf)

## Supported Operating Systems:
* Linux, MacOS, and Windows.

## JDK 18 Project Panama

This library uses the new incubator features from JDK 18 [JEP 419](https://openjdk.java.net/jeps/419) Project Panama 
and the [jextract](https://github.com/openjdk/panama-foreign/blob/foreign-jextract/doc/panama_jextract.md) tool to auto-generate java code to interface with native libararies.  The jextract tool generates 
code from the native header file:

### Generating Source Code
1. Install the sdrplay api
2. Locate the library headers.
   * Linux: /usr/local/include/*.h
   * Windows: C:\Program Files\SDRplay\API\inc\*.h
   * MacOS: ??
3. cd into the library source directory: (project)/jsdrplay/sdrplay-api/src/main/java/
4. Run: **jextract -t io.github.dsheirer.sdrplay.api /usr/local/include/sdrplay_api.h -l libsdrplay_api --source**

**Note**: the jextract auto-generated code includes a class RuntimeHelper that loads the library from the default system
path.  The sdrplay api is installed to a non-default location and the auto-generated code can't find it.  Until I 
can figure out how to make that work, I've added a workaround where the SDRPlay class explicitly loads the library
from the installed location.  The auto-generated RuntimeHelper library load call is manually commented out.

### IntelliJ setup
1. Run configuration
2. JVM Options: --enable-native-access=ALL-UNNAMED,sdrplay.api,io.github.dsheirer.sdrplay -Djava.library.path=/usr/local/lib
3. Add the sdrplay library location to the java.library.path:
   * Linux: /usr/local/lib
   * Windows: C:\Program Files\SDRplay\API\inc\
   * MacOS: ??

