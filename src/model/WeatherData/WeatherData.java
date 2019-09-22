package model.WeatherData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherData {

    @SerializedName("data")
    @Expose
    private List<Data> data = null;

    @SerializedName("count")
    @Expose
    private Double count;

    //GETTERS & SETTERS
    public List<Data> getData() {
        return data;
    }
    public void setData(List<Data> data) {
        this.data = data;
    }
    public Double getCount() {
        return count;
    }
    public void setCount(Double count) {
        this.count = count;
    }

}
