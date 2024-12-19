package be.tobiridi.passwordsecurity.component;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import be.tobiridi.passwordsecurity.R;

public class SettingsListAdapter extends BaseAdapter {
    private final List<Integer> _settingsItemsResources;

    public SettingsListAdapter() {
        //get resources string for the list of settings
        //possible to translate in the resource file
        this._settingsItemsResources = List.of(
                R.string.settings_import,
                R.string.settings_export,
                R.string.settings_clear_all
        );
    }

    @Override
    public int getCount() {
        return this._settingsItemsResources.size();
    }

    @Override
    public Object getItem(int position) {
        return this._settingsItemsResources.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_item_settings, parent, false);
        }

        //get views id
        TextView tv = convertView.findViewById(R.id.list_settings_title);

        //set data of the view
        this.paramTexView(convertView, tv, position);

        return convertView;
    }

    private void paramTexView(View v, TextView tv, int position) {
        Resources resources = v.getResources();
        String txt = resources.getString(this._settingsItemsResources.get(position));

        tv.setText(txt);
        if (this._settingsItemsResources.get(position) == R.string.settings_clear_all) {
            tv.setTextColor(resources.getColor(R.color.red, null));
        }

        //set action on tap
        //TODO : not implemented
        //tv.setOnClickListener(clickListener());
        //tv.setOnTouchListener(touchListener());
    }

//    private View.OnTouchListener touchListener() {
//        return new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                //TODO : not implemented
//                Toast.makeText(v.getContext(), "click on view", Toast.LENGTH_SHORT).show();
//                return v.performClick();
//            }
//        };
//    }
}
