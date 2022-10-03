package whatsappclone.proyecto_javier_juan_uceda.whatsappclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainPageActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnLogOut, btnFindUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        setUI();

    }

    private void setUI() {
        btnLogOut = findViewById(R.id.logout);
        btnLogOut.setOnClickListener(this);
        btnFindUser = findViewById(R.id.btnFindUser);
        btnFindUser.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
            case R.id.btnFindUser:
                startActivity(new Intent(this, FindUserActivity.class));
                break;
        }
    }
}