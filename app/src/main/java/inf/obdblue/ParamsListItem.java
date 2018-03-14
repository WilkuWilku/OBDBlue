package inf.obdblue;

/**
 * Created by Inf on 2018-03-14.
 */

public class ParamsListItem {
    private String name;
    private double value;

    public ParamsListItem(String name, double value){
        this.name = name;
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
