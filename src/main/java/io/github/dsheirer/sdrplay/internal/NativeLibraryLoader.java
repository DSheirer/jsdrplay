package io.github.dsheirer.sdrplay.internal;

import java.lang.foreign.Arena;
import java.lang.foreign.SymbolLookup;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Finds and loads the SDRplay API library installed on this machine.
 *
 * <p>jsdrplay does not bundle the library: the SDRplay API is closed source,
 * its licence does not permit redistribution, and it only works together with
 * the SDRplay API service that its installer sets up. The library is looked for
 * in this order:
 *
 * <ol>
 *   <li>the {@value #PATH_PROPERTY} system property, naming the library file or
 *       the directory that contains it; nothing else is tried when it is set
 *   <li>where the official SDRplay installer puts it
 *   <li>the operating system's library search path, such as
 *       {@code LD_LIBRARY_PATH}, {@code DYLD_LIBRARY_PATH} or {@code PATH}
 * </ol>
 */
public final class NativeLibraryLoader {
    /** System property naming the library file, or a directory containing it. */
    public static final String PATH_PROPERTY = "jsdrplay.library.path";

    private NativeLibraryLoader() {
    }

    /** A loaded library and where it was found. */
    public record Loaded(SymbolLookup lookup, String location) {
    }

    /** Identifies the operating system and CPU, such as {@code linux-x86_64}. */
    public static String platform(String osName, String osArch) {
        String name = osName.toLowerCase(Locale.ROOT);
        String os;
        if (name.contains("linux")) {
            os = "linux";
        } else if (name.contains("mac") || name.contains("darwin")) {
            os = "macos";
        } else if (name.contains("windows")) {
            os = "windows";
        } else {
            throw new UnsatisfiedLinkError("jsdrplay does not support operating system: " + osName);
        }
        String arch = switch (osArch.toLowerCase(Locale.ROOT)) {
            case "amd64", "x86_64", "x64" -> "x86_64";
            case "aarch64", "arm64" -> "aarch64";
            default -> throw new UnsatisfiedLinkError("jsdrplay does not support CPU architecture: " + osArch);
        };
        return os + "-" + arch;
    }

    /** File names the library has on a platform, the preferred one first. */
    public static List<String> libraryNames(String platform) {
        if (platform.startsWith("linux")) {
            return List.of("libsdrplay_api.so.3", "libsdrplay_api.so");
        }
        if (platform.startsWith("macos")) {
            // The macOS installer also names the library .so.
            return List.of("libsdrplay_api.so.3", "libsdrplay_api.so", "libsdrplay_api.dylib");
        }
        return List.of("sdrplay_api.dll");
    }

    /**
     * Returns the places to look for the library, in order. An absolute path
     * is a file; a bare name is passed to the operating system's own search.
     *
     * @param override the value of {@value #PATH_PROPERTY}, or null
     * @param environment the process environment, for Windows' {@code ProgramFiles}
     */
    public static List<String> candidates(String platform, String override, Map<String, String> environment) {
        List<String> names = libraryNames(platform);
        List<String> result = new ArrayList<>();
        if (override != null && !override.isBlank()) {
            Path path = Path.of(override);
            if (Files.isDirectory(path)) {
                names.forEach(name -> result.add(path.resolve(name).toString()));
            } else {
                result.add(path.toString());
            }
            return result;
        }
        if (platform.startsWith("linux")) {
            String multiarch = platform.endsWith("x86_64") ? "x86_64-linux-gnu" : "aarch64-linux-gnu";
            for (String directory : List.of("/usr/local/lib", "/usr/lib", "/usr/lib64", "/usr/lib/" + multiarch)) {
                names.forEach(name -> result.add(directory + "/" + name));
            }
        } else if (platform.startsWith("macos")) {
            for (String directory : List.of("/usr/local/lib", "/opt/homebrew/lib")) {
                names.forEach(name -> result.add(directory + "/" + name));
            }
        } else {
            String programFiles = environment.getOrDefault("ProgramW6432",
                environment.getOrDefault("ProgramFiles", "C:\\Program Files"));
            // The installer has a directory per CPU architecture. An ARM64 JVM
            // cannot load the x64 library; trying it anyway puts the reason in
            // the error when that is the only one installed.
            List<String> architectures = platform.endsWith("aarch64") ? List.of("ARM64", "x64") : List.of("x64");
            for (String architecture : architectures) {
                result.add(programFiles + "\\SDRplay\\API\\" + architecture + "\\sdrplay_api.dll");
            }
        }
        result.addAll(names);
        return result;
    }

    /** Loads the library, or throws an error that lists every place tried. */
    static Loaded load() {
        String platform = platform(System.getProperty("os.name"), System.getProperty("os.arch"));
        List<String> tried = new ArrayList<>();
        for (String candidate : candidates(platform, System.getProperty(PATH_PROPERTY), System.getenv())) {
            boolean isPath = candidate.contains("/") || candidate.contains("\\");
            if (isPath && !Files.isRegularFile(Path.of(candidate))) {
                tried.add(candidate + " (not found)");
                continue;
            }
            try {
                // The global arena keeps the library loaded for the life of the JVM.
                SymbolLookup lookup = isPath
                    ? SymbolLookup.libraryLookup(Path.of(candidate), Arena.global())
                    : SymbolLookup.libraryLookup(candidate, Arena.global());
                return new Loaded(lookup, candidate);
            } catch (IllegalArgumentException e) {
                tried.add(candidate + (isPath ? " (" + e.getMessage() + ")" : " (not on the library search path)"));
            }
        }
        throw new UnsatisfiedLinkError("SDRplay API library not found for " + platform
            + ". Install the SDRplay API from https://www.sdrplay.com/api/ or set -D" + PATH_PROPERTY
            + ". Tried: " + String.join(", ", tried));
    }
}
