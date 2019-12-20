package sg.edu.nus.flipmeteam7;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button startGameButton = (Button) findViewById(R.id.startGameButton);
        startGameButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.startGameButton:
                Intent intent = new Intent(this, ChooseImageActivity.class);
                startActivity(intent);
                break;
        }
    }
}
