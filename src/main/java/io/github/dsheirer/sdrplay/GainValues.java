package io.github.dsheirer.sdrplay;

/**
 * The gain the API calculates for the current settings of a tuner:
 * {@code sdrplay_api_GainValuesT}, in dB.
 *
 * @param current the current gain
 * @param max the highest gain available at the current frequency
 * @param min the lowest gain available at the current frequency
 */
public record GainValues(float current, float max, float min) {
}
