package shipsensor.simulator.util;


public final class GpsCalculator {


    private static final double EARTH_RADIUS =
            6371000; // 米



    private GpsCalculator() {

    }



    /**
     * 根据当前位置、航向、移动距离
     * 计算下一GPS坐标
     *
     * @param latitude 当前纬度
     * @param longitude 当前经度
     * @param heading 航向角 0-360
     * @param distanceMeters 移动距离(米)
     */
    public static double[] move(
            double latitude,
            double longitude,
            double heading,
            double distanceMeters) {



        double angularDistance =
                distanceMeters / EARTH_RADIUS;



        double headingRad =
                Math.toRadians(heading);



        double latRad =
                Math.toRadians(latitude);



        double lonRad =
                Math.toRadians(longitude);



        double newLat =
                Math.asin(
                        Math.sin(latRad)
                                *
                                Math.cos(angularDistance)
                                +
                                Math.cos(latRad)
                                        *
                                        Math.sin(angularDistance)
                                        *
                                        Math.cos(headingRad)
                );



        double newLon =
                lonRad
                        +
                        Math.atan2(
                                Math.sin(headingRad)
                                        *
                                        Math.sin(angularDistance)
                                        *
                                        Math.cos(latRad),

                                Math.cos(angularDistance)
                                        -
                                        Math.sin(latRad)
                                                *
                                                Math.sin(newLat)
                        );



        return new double[]{

                Math.toDegrees(newLat),

                Math.toDegrees(newLon)

        };

    }




    /**
     * 计算两点距离
     *
     * 单位: 米
     */
    public static double distance(
            double lat1,
            double lon1,
            double lat2,
            double lon2) {



        double dLat =
                Math.toRadians(lat2 - lat1);


        double dLon =
                Math.toRadians(lon2 - lon1);



        double a =
                Math.sin(dLat / 2)
                        *
                        Math.sin(dLat / 2)
                        +
                        Math.cos(Math.toRadians(lat1))
                                *
                                Math.cos(Math.toRadians(lat2))
                                *
                                Math.sin(dLon / 2)
                                *
                                Math.sin(dLon / 2);



        double c =
                2 *
                        Math.atan2(
                                Math.sqrt(a),
                                Math.sqrt(1 - a)
                        );


        return EARTH_RADIUS * c;

    }




    /**
     * 计算两个GPS点之间的航向
     *
     * 返回 0-360°
     */
    public static double bearing(
            double lat1,
            double lon1,
            double lat2,
            double lon2) {


        double dLon =
                Math.toRadians(lon2 - lon1);



        double y =
                Math.sin(dLon)
                        *
                        Math.cos(Math.toRadians(lat2));



        double x =
                Math.cos(Math.toRadians(lat1))
                        *
                        Math.sin(Math.toRadians(lat2))
                        -
                        Math.sin(Math.toRadians(lat1))
                                *
                                Math.cos(Math.toRadians(lat2))
                                *
                                Math.cos(dLon);



        double bearing =
                Math.toDegrees(
                        Math.atan2(y, x)
                );



        return (bearing + 360) % 360;

    }

}