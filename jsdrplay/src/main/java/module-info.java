module io.github.dsheirer.sdrplay {
    exports io.github.dsheirer.sdrplay;
    exports io.github.dsheirer.sdrplay.device;
    exports io.github.dsheirer.sdrplay.callback;
    exports io.github.dsheirer.sdrplay.parameter.composite;
    exports io.github.dsheirer.sdrplay.parameter.device;
    exports io.github.dsheirer.sdrplay.parameter.tuner;
    exports io.github.dsheirer.sdrplay.parameter.control;
    exports io.github.dsheirer.sdrplay.error;
    exports io.github.dsheirer.sdrplay.parameter.event;
    exports io.github.dsheirer.sdrplay.util;

    requires ch.qos.logback.classic;
    requires jdk.incubator.foreign;
    requires org.slf4j;
    requires sdrplay.api;
}