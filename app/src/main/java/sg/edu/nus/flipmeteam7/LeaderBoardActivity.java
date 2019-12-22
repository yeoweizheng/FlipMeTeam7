package sg.edu.nus.flipmeteam7;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class LeaderBoardActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);
        sharedPreferences = getSharedPreferences("leaderBoard", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        showLeaderBoard();
    }

    void showLeaderBoard(){
        List<LeaderboardRowItem> list = getLeaderboardList();
        Collections.sort(list);
        for(int i = 0; i < list.size(); i++){
            list.get(i).setRank("#" + (i+1));
        }
        ListView listView =  findViewById(R.id.leaderBoard);
        LeaderboardAdapter adapter = new LeaderboardAdapter(this, R.layout.activity_leader_board, list);
        listView.setAdapter(adapter);
    }

    ArrayList<LeaderboardRowItem> getLeaderboardList(){
        ArrayList<LeaderboardRowItem> list = new ArrayList<LeaderboardRowItem>();
        int lastIndex, i = 0;
        while(true){
            if(!sharedPreferences.contains("name" + i)) break;
            i++;
        }
        lastIndex = i;
        for(i = 0; i < lastIndex; i++){
            LeaderboardRowItem rowItem = new LeaderboardRowItem(
                sharedPreferences.getString("name" + i, null), sharedPreferences.getInt("score" + i, 0));
            list.add(rowItem);
        }
        return list;
    }
}
