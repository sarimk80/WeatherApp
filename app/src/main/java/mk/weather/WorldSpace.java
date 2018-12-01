package mk.weather;

/**
 * Created by abbott on 27-Aug-17.
 */

public class WorldSpace {

   public double Latitude;

   public double Longitude;

  /*  public WorldSpace(double latitude, double longitude) {
       this.Latitude = latitude;
        this.Longitude = longitude;
    }*/


    public double getLatitude() {
        return this.Latitude;
    }

    public void setLatitude(double latitude) {
        this.Latitude = latitude;
    }

    public double getLongitude() {
        return this.Longitude;
    }

    public void setLongitude(double longitude) {
        this.Longitude = longitude;
    }
}
