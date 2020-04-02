package com.example.thomas.hackathonproject;

/**
 * Station base class to define a station returned from the sever
 */
public class Station {

    Double lat = 0.0;
    Double lng = 0.0;
    String name = "";

    /**
     * Will return Latitude of current station
     * @return Latitude of station
     */
    public Double getLat() {
        return lat;
    }

    /**
     * Gets current latitude to
      * @param lat
     */
    public void setLat(Double lat) {
        this.lat = lat;
    }

    /**
     * Will return Longitude of current station
     * @return Longitude of station
     */
    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    /**
     * Gets name set for station
     * @return name given
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name for the station
     * @param name name to give station
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Converts data in object to string for debugging
     * @return String of details
     */
    @Override
    public String toString()
    {
        return name+" is at lat:" +lat +" long:" +lng;
    }

    /**
     * Returns a string version of the class with a distance from the point its given
     * @param lat start point of distance away latitudinal
     * @param lng start point of distance away longitudinally
     * @return String of details with distance
     */
    public String toString(double lat,double lng)
    {
        return name+" is "+ distanceAway(lat,lng) + " km away";
    }

    /**
     * Calculates how far away something is from the start point in relation to the Stations latitude and longitude
     * @param lat2 the latitude of the final point
     * @param lon2 the longitude of the final point
     * @return Km away from the location
     */
    public double distanceAway(double lat2, double lon2) {
        final double R = 6372.8; //km radius
        double dLat = Math.toRadians(lat2-this.lat);
        double dLon = Math.toRadians(lon2-this.lng);
        double lat1 = Math.toRadians(this.lat);
        lat2 = Math.toRadians(lat2);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2* Math.asin(Math.sqrt(a));
        return Math.round((R*c) * 100.0) / 100.0;
    }
}
