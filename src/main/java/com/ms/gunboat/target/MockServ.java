package com.ms.gunboat.target;

public class MockServ {
    private final String dss_id;
    private final long major;
    private final long minor;
    private boolean up;

    public MockServ(String dss_id, long major, long minor, boolean up) {

        this.dss_id = dss_id;
        this.major = major;
        this.minor = minor;
        this.up = up;
    }

    public String getDss_id() {
        return dss_id;
    }

    public long getMajor() {
        return major;
    }

    public long getMinor() {
        return minor;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }
}
