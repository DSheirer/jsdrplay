# Third-party notices

jsdrplay is licensed under the Apache License 2.0 and contains no third-party
code.

## SDRplay API

Website: <https://www.sdrplay.com/api/>

jsdrplay calls the SDRplay API, which is proprietary software of SDRplay
Limited under its own end user licence. The API is **not included** in jsdrplay
or its releases: each user installs it from SDRplay, which also installs the
service that the API needs to reach the devices. jsdrplay loads the installed
library at run time.

The build downloads the official Linux installer named in
[`native/versions.env`](native/versions.env) to read its C headers, against
which CI checks the Java bindings. The installer is not run, and neither the
headers nor any other part of it are committed to this repository or packaged
in the jar. The Java bindings were written from the API's public headers and
specification.

jsdrplay is an independent project and is not endorsed by SDRplay Limited.
SDRplay, RSP and the RSP model names are trademarks of SDRplay Limited.
