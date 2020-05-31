package pam.poluxion.services;

import org.jetbrains.annotations.NotNull;

public class SatelliteStatus {

  public int id;
  private float azimuth;
  private float elevation;
  float snr;
  private boolean used;
  private int constellation;

  SatelliteStatus(int id,boolean used,float azimuth,float elevation,float snr,int constellation) {
    this.id = id;
    this.used = used;
    this.azimuth = azimuth;
    this.elevation = elevation;
    this.snr = snr;
    this.constellation = constellation;
  }

  @NotNull
  @Override
  public String toString() {
    return "" + id + "," + used + "," + azimuth + "," + elevation + "," + snr + "," + constellation +"";
  }
}
