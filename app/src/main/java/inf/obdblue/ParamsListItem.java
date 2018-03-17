package inf.obdblue;

/**
 * Created by Inf on 2018-03-14.
 */

/* Klasa z danymi elementu ParamsListView */


public class ParamsListItem {
    private String name;
    private String value;

    public ParamsListItem(String name, String value){
        this.name = name;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
