package sg.edu.nus.flipmeteam7;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class LeaderBoardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);
        List<LeaderboardRowItem> list = new ArrayList<LeaderboardRowItem>();
        LeaderboardRowItem rowItem1 = new LeaderboardRowItem(1, "#1", "Wei Zheng", "9500");
        LeaderboardRowItem rowItem2 = new LeaderboardRowItem(2, "#2", "Mark", "9000");
        list.add(rowItem1);
        for(int i = 0; i < 50; i++) list.add(rowItem2);
        ListView listView =  findViewById(R.id.leaderBoard);
        LeaderboardAdapter adapter = new LeaderboardAdapter(this, R.layout.activity_leader_board, list);
        listView.setAdapter(adapter);
    }
}
