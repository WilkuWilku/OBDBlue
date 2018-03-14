package inf.obdblue;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Inf on 2018-03-14.
 */

public class ParamsListAdapter extends ArrayAdapter<ParamsListItem> {

    private ArrayList<ParamsListItem> data;

    public ParamsListAdapter(ArrayList<ParamsListItem> data, Context context){
        super(context, R.layout.param_list_item, data);
        this.data = data;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ParamsListItem item = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.param_list_item, parent, false);
        }
        TextView paramLabel = (TextView) convertView.findViewById(R.id.paramLabel);
        paramLabel.setText(item.getName());

        TextView tvValue = (TextView) convertView.findViewById(R.id.valueTextView);
        tvValue.setText(String.valueOf(item.getValue()));

        return convertView;
    }
}
