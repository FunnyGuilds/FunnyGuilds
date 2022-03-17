package net.dzikoysk.funnyguilds.shared;

import panda.std.Option;

public final class Position {

    public static final Position ZERO = new Position(0, 0, 0);

    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;
    private final String world;

    public Position(final double x, final double y, final double z, final float yaw, final float pitch, final String world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.world = world;
    }

    public Position(double x, double y, double z, String world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.yaw = 0.0f;
        this.pitch = 0.0f;
    }

    public Position(double x, double y, double z) {
        this(x, y, z, null);
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public Option<String> getWorld() {
        return Option.of(this.world);
    }

    public Position add(Position position) {
        return new Position(this.x + position.x, this.y + position.y, this.z + position.z, this.pitch, this.yaw, this.world);
    }

    public Position add(double x, double y, double z) {
        return new Position(this.x + x, this.y + y, this.z + z, this.pitch, this.yaw, this.world);
    }

    public Position subtract(Position position) {
        return new Position(this.x - position.x, this.y - position.y, this.z - position.z, this.pitch, this.yaw, this.world);
    }

    public Position subtract(double x, double y, double z) {
        return new Position(this.x - x, this.y - y, this.z - z, this.pitch, this.yaw, this.world);
    }

    public Position multiply(Position position) {
        return new Position(this.x * position.x, this.y * position.y, this.z * position.z, this.pitch, this.yaw, this.world);
    }

    public Position multiply(double x, double y, double z) {
        return new Position(this.x * x, this.y * y, this.z * z, this.pitch, this.yaw, this.world);
    }

    public Position divide(Position position) {
        return new Position(this.x / position.x, this.y / position.y, this.z / position.z, this.pitch, this.yaw, this.world);
    }

    public Position divide(double x, double y, double z) {
        return new Position(this.x / x, this.y / y, this.z / z, this.pitch, this.yaw, this.world);
    }

    public Position changeWorld(String world) {
        return new Position(this.x, this.y, this.z, this.pitch, this.yaw, world);
    }

}
