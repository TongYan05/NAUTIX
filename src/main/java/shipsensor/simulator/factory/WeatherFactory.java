package shipsensor.simulator.factory;

import org.springframework.stereotype.Component;
import shipsensor.entity.WeatherRegion;

import java.util.concurrent.ThreadLocalRandom;


@Component
public class WeatherFactory {


    private static final String[] REGION_NAMES = {


            // Pacific Ocean
            "North Pacific Ocean",
            "Central Pacific Ocean",
            "South Pacific Ocean",
            "Western Pacific Ocean",
            "Eastern Pacific Ocean",
            "Northwest Pacific",
            "Southwest Pacific",
            "Pacific Trade Wind Zone",
            "Pacific Equatorial Zone",


            // Atlantic Ocean
            "North Atlantic Ocean",
            "Central Atlantic Ocean",
            "South Atlantic Ocean",
            "Western Atlantic Ocean",
            "Eastern Atlantic Ocean",
            "North Atlantic Storm Zone",
            "Atlantic Trade Wind Zone",


            // Indian Ocean
            "North Indian Ocean",
            "Central Indian Ocean",
            "South Indian Ocean",
            "Arabian Sea",
            "Bay of Bengal",
            "Indian Ocean Monsoon Zone",


            // East Asia
            "South China Sea",
            "East China Sea",
            "Yellow Sea",
            "Bohai Sea",
            "Sea of Japan",
            "Philippine Sea",
            "Taiwan Strait",
            "Korea Strait",
            "Luzon Strait",
            "Java Sea",
            "Celebes Sea",
            "Sulu Sea",
            "Banda Sea",
            "Flores Sea",


            // Southeast Asia
            "Malacca Strait",
            "Singapore Strait",
            "Andaman Sea",
            "Gulf of Thailand",
            "South China Coastal Waters",


            // Middle East
            "Persian Gulf",
            "Gulf of Oman",
            "Red Sea",
            "Arabian Gulf",
            "Bab el Mandeb Strait",


            // Europe
            "Mediterranean Sea",
            "Western Mediterranean",
            "Eastern Mediterranean",
            "Adriatic Sea",
            "Aegean Sea",
            "Tyrrhenian Sea",
            "Baltic Sea",
            "North Sea",
            "Norwegian Sea",
            "Barents Sea",
            "Black Sea",
            "English Channel",
            "Irish Sea",


            // Africa
            "Gulf of Guinea",
            "Mozambique Channel",
            "Cape of Good Hope Waters",
            "South African Coastal Waters",
            "Somali Basin",


            // Australia
            "Coral Sea",
            "Tasman Sea",
            "Arafura Sea",
            "Timor Sea",
            "Great Australian Bight",
            "Australian East Coast Waters",


            // North America
            "Gulf of Mexico",
            "Caribbean Sea",
            "Bering Sea",
            "Hudson Bay",
            "Alaska Coastal Waters",
            "California Coastal Waters",
            "Atlantic Canada Waters",


            // South America
            "Brazil Coastal Waters",
            "South Atlantic Brazil Zone",
            "Cape Horn Waters",
            "Patagonian Shelf",
            "Peru Coastal Waters",


            // Polar
            "Arctic Ocean",
            "Greenland Sea",
            "Labrador Sea",
            "Antarctic Ocean",
            "Ross Sea",
            "Weddell Sea"


    };



    private static final String[] WEATHER_TYPES = {


            "CLEAR",

            "SUNNY",

            "PARTLY_CLOUDY",

            "OVERCAST",

            "LOW_CLOUD",

            "LIGHT_RAIN",

            "MODERATE_RAIN",

            "HEAVY_RAIN",

            "RAIN_SHOWER",

            "DRIZZLE",

            "FOG",

            "SEA_FOG",

            "MIST",

            "THUNDERSTORM",

            "LIGHTNING_STORM",

            "TROPICAL_STORM",

            "TROPICAL_DEPRESSION",

            "TYPHOON",

            "HURRICANE",

            "CYCLONE",

            "STRONG_WIND",

            "GALE",

            "STORM_FORCE_WIND",

            "ROUGH_SEA",

            "HIGH_WAVE",

            "VERY_HIGH_WAVE",

            "LOW_VISIBILITY",

            "ICE_WARNING",

            "SNOW",

            "FREEZING_FOG",

            "SAND_STORM",

            "EXTREME_WEATHER"


    };



    public WeatherRegion create(int index){


        ThreadLocalRandom random =
                ThreadLocalRandom.current();



        WeatherRegion weather =
                new WeatherRegion();



        String region =
                REGION_NAMES[
                        index % REGION_NAMES.length
                        ];



        weather.setRegionName(
                region
                        +
                        " Sector-"
                        +
                        (index + 1)
        );



        weather.setAirTemperature(
                random.nextDouble(
                        -20,
                        45
                )
        );


        weather.setSeaTemperature(
                random.nextDouble(
                        0,
                        35
                )
        );


        weather.setHumidity(
                random.nextDouble(
                        20,
                        100
                )
        );


        weather.setPressure(
                random.nextDouble(
                        950,
                        1050
                )
        );


        weather.setWindSpeed(
                random.nextDouble(
                        0,
                        45
                )
        );


        weather.setWindDirection(
                random.nextDouble(
                        0,
                        360
                )
        );


        weather.setWaveHeight(
                random.nextDouble(
                        0,
                        15
                )
        );



        weather.setWeatherType(
                WEATHER_TYPES[
                        random.nextInt(
                                WEATHER_TYPES.length
                        )
                        ]
        );


        return weather;

    }

}