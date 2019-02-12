
package adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.luis.qrscannerfrandreas.R;

import java.util.ArrayList;
import java.util.TreeMap;

class CustomExpandableListAdapterSub extends BaseExpandableListAdapter {

    private final Context context;
    private final ArrayList<Object>expandableListTitleSub;
    private final TreeMap<Object,ArrayList<ArrayList>> expandableListDetailSub;


    public CustomExpandableListAdapterSub(Context context, ArrayList<Object> expandableListTitle,
                                          TreeMap<Object,ArrayList<ArrayList>> expandableListDetail) {
        this.context = context;
        this.expandableListTitleSub = expandableListTitle;
        this.expandableListDetailSub = expandableListDetail;

    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetailSub.get(this.expandableListTitleSub.get(listPosition))
                .get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String expandedListText = (String) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item_2, parent,false);
        }
        TextView expandedListTextView = convertView
                .findViewById(R.id.expandedListItem2);
        expandedListTextView.setText(expandedListText);
        convertView.setTag(convertView);
        return convertView;

    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetailSub.get(this.expandableListTitleSub.get(listPosition))
                .size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitleSub.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitleSub.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }
        TextView listTitleTextView = convertView
                .findViewById(R.id.expandedListItem);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}
