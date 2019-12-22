package sg.edu.nus.flipmeteam7;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class LeaderboardAdapter extends ArrayAdapter {
    Context context;

    public LeaderboardAdapter(Context context, int resourceId, List<LeaderboardRowItem> items){
        super(context, resourceId, items);
        this.context = context;
    }

    public class RowItemView{
        TextView rankView;
        TextView nameView;
        TextView scoreView;
    }


    @Override
    public View getView(int position, View view, ViewGroup parent){
        RowItemView row = null;
        LeaderboardRowItem rowItem = (LeaderboardRowItem) getItem(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(view == null){
            view = inflater.inflate(R.layout.leader_board_item, null);
            row = new RowItemView();
            row.rankView = view.findViewById(R.id.rankNo);
            row.nameView = view.findViewById(R.id.scoreboardName);
            row.scoreView = view.findViewById(R.id.scoreboardPoints);
            view.setTag(row);
        } else {
            row = (RowItemView) view.getTag();
        }
        row.rankView.setText(rowItem.rank);
        row.nameView.setText(rowItem.name);
        row.scoreView.setText(rowItem.score + "");
        return view;
    }
}

