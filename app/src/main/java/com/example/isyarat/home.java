import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainPage extends AppCompatActivity {
    private boolean isFromWelcomePage3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        isFromWelcomePage3 = getIntent().getBooleanExtra("isFromWelcomePage3", false);

        ImageButton backButton = findViewById(R.id.backButton);
        GridView gridView = findViewById(R.id.gridView);
        Button homeButton = findViewById(R.id.homeButton);
        Button profileButton = findViewById(R.id.profileButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        gridView.setAdapter(new GridAdapter(this));

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainPage.this, Page1.class);
                startActivity(intent);
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFromWelcomePage3) {
                    Intent intent = new Intent(MainPage.this, LoginPage.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainPage.this, ProfilePage.class);
                    startActivity(intent);
                }
            }
        });
    }

    public void showDevelopmentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Dalam Pengembangan");
        builder.setMessage("Fitur ini masih dalam tahap pengembangan.");
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
