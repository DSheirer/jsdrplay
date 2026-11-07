package io.github.dsheirer.sdrplay;

/**
 * The extended description of the last error on a device, from
 * {@code sdrplay_api_GetLastError}.
 *
 * @param file the API source file that reported it
 * @param function the API function that reported it
 * @param line the line in that file
 * @param message the description
 */
public record ErrorInfo(String file, String function, int line, String message) {
}
