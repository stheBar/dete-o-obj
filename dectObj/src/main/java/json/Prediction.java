package json;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

public class Prediction {
    public double x;
    public double y;
    public int width;
    public int height;
    @SerializedName("class")
    public String myclass;
    public double confidence;
}
