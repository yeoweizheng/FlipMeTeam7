package sg.edu.nus.flipmeteam7;

public class LeaderboardRowItem implements Comparable<LeaderboardRowItem>{
    String rank;
    String name;
    int score;

    public LeaderboardRowItem(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public void setRank(String rank){
        this.rank = rank;
    }

    @Override
    public int compareTo(LeaderboardRowItem o){
        if(this.score < o.score) return 1;
        else return -1;
    }

}
