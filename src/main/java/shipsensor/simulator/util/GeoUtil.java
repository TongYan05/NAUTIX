package shipsensor.simulator.util;

import shipsensor.simulator.model.Coordinate;

/**
 * GPS工具类
 */
public final class GeoUtil {

    /**
     * 地球半径(m)
     */
    private static final double EARTH_RADIUS = 6378137;

    private GeoUtil() {
    }

    /**
     * 根据当前位置、航向、移动距离
     * 计算下一坐标
     */
    public static Coordinate move(Coordinate current,
                                  double heading,
                                  double distanceMeter) {

        double lat = Math.toRadians(current.getLatitude());
        double lon = Math.toRadians(current.getLongitude());

        double headingRad = Math.toRadians(heading);

        double newLat = Math.asin(
                Math.sin(lat) * Math.cos(distanceMeter / EARTH_RADIUS)
                        + Math.cos(lat)
                        * Math.sin(distanceMeter / EARTH_RADIUS)
                        * Math.cos(headingRad));

        double newLon = lon +
                Math.atan2(
                        Math.sin(headingRad)
                                * Math.sin(distanceMeter / EARTH_RADIUS)
                                * Math.cos(lat),
                        Math.cos(distanceMeter / EARTH_RADIUS)
                                - Math.sin(lat)
                                * Math.sin(newLat));

        return new Coordinate(
                Math.toDegrees(newLat),
                Math.toDegrees(newLon));
    }

}