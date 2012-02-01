package com.blastedstudios.crittercaptors.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.badlogic.gdx.Gdx;
import com.blastedstudios.crittercaptors.creature.AffinityCalculator;
import com.blastedstudios.crittercaptors.creature.AffinityEnum;

/**
 * Keep world location up to date. Since floats are too inaccurate, using doubles
 * up to render, and setting initial lat so as to shrink the error when converting
 */
public class WorldLocationManager {
	private double lat = 0.0, latInitial,lon = 0.0,lonInitial;
	private BufferedImage worldLocationLastImage;
	private float timeSinceLastUpdate = TIME_TO_UPDATE;
	private static final float TIME_TO_UPDATE = 60;
	private HashMap<AffinityEnum, Float> currentWorldAffinities;
	
	public WorldLocationManager(){
		latInitial = 0.0;//TODOGdx.input.getGPSLatitude();
		lonInitial = 0.0;//TODOGdx.input.getGPSLongitude();
	}

	public void update(){
		timeSinceLastUpdate += Gdx.graphics.getDeltaTime();
		if(timeSinceLastUpdate > TIME_TO_UPDATE){
			//TODOlat = Gdx.input.getGPSLatitude();
			//TODOlon = Gdx.input.getGPSLongitude();
			timeSinceLastUpdate = 0;
			try {
				worldLocationLastImage = ImageIO.read(
						new URL("http://ojw.dev.openstreetmap.org/StaticMap/?lat="+
								lat+"&lon="+lon+"&z=18&w=64&h=64&mode=Export&show=1"));
				currentWorldAffinities = AffinityCalculator.getAffinitiesFromTexture(worldLocationLastImage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public double getLatitude(){
		return lat - latInitial;
	}
	
	public double getLongitude(){
		return lon - lonInitial;
	}
	
	public HashMap<AffinityEnum, Float> getWorldAffinities(){
		return currentWorldAffinities;
	}
	
	/**
	 * Found at http://stackoverflow.com/questions/1995998/android-get-altitude-by-longitude-and-latitude
	 * Note alternative: http://www.earthtools.org/webservices.htm#height
	 */
	public static double getAltitude(Double longitude, Double latitude) {
	    double result = Double.NaN;
	    HttpClient httpClient = new DefaultHttpClient();
	    HttpContext localContext = new BasicHttpContext();
	    String url = "http://gisdata.usgs.gov/"
	            + "xmlwebservices2/elevation_service.asmx/"
	            + "getElevation?X_Value=" + String.valueOf(longitude)
	            + "&Y_Value=" + String.valueOf(latitude)
	            + "&Elevation_Units=METERS&Source_Layer=-1&Elevation_Only=true";
	    HttpGet httpGet = new HttpGet(url);
	    try {
	        HttpResponse response = httpClient.execute(httpGet, localContext);
	        HttpEntity entity = response.getEntity();
	        if (entity != null) {
	            InputStream instream = entity.getContent();
	            int r = -1;
	            StringBuffer respStr = new StringBuffer();
	            while ((r = instream.read()) != -1)
	                respStr.append((char) r);
	            String tagOpen = "<double>";
	            String tagClose = "</double>";
	            if (respStr.indexOf(tagOpen) != -1) {
	                int start = respStr.indexOf(tagOpen) + tagOpen.length();
	                int end = respStr.indexOf(tagClose);
	                String value = respStr.substring(start, end);
	                result = Double.parseDouble(value);
	            }
	            instream.close();
	        }
	    } catch (ClientProtocolException e) {} 
	    catch (IOException e) {}
	    return result;
	}
}