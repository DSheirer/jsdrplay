package io.github.dsheirer.sdrplay;

/**
 * SDRPlay API Versions
 */
public enum Version
{
    UNKNOWN(0.0f, false),
    V3_00(3.00f, false),
    V3_01(3.01f, false),
    V3_02(3.02f, false),
    V3_03(3.03f, false),
    V3_04(3.04f, false),
    V3_05(3.05f, false),
    V3_06(3.06f, false),
    V3_07(3.07f, true),
    V3_08(3.08f, true);

    private float mValue;
    private boolean mSupported;

    Version(float value, boolean supported)
    {
        mValue = value;
        mSupported = supported;
    }

    /**
     * Indicates if the API version is supported by the jsdrplay library
     */
    public boolean isSupported()
    {
        return mSupported;
    }

    /**
     * Indicates if this version is greater than or equal to the specified version.
     * @param version to compare
     * @return true if this version is greater than or equal to
     */
    public boolean gte(Version version)
    {
        return this.ordinal() >= version.ordinal();
    }

    /**
     * Numeric API version/value
     */
    public float getVersion()
    {
        return mValue;
    }

    /**
     * Lookup the version from the specified value.
     * @param value to lookup
     * @return version or UNKNOWN
     */
    public static Version fromValue(float value)
    {
        for(Version version: values())
        {
            if(version.mValue == value)
            {
                return version;
            }
        }

        return UNKNOWN;
    }
}
