package UtilsPackage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.poussapoussi.R;

public class SpinnerAdapter extends ArrayAdapter<String> {

    private Context context;
    private String[] languages;
    private int[] flags;

    public SpinnerAdapter(Context context, String[] languages, int[] flags) {
        super(context, R.layout.spinner_item, languages);
        this.context = context;
        this.languages = languages;
        this.flags = flags;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.flag_image);
        TextView textView = convertView.findViewById(R.id.language_name);

        imageView.setImageResource(flags[position]);
        textView.setText(languages[position]);

        return convertView;
    }
}
