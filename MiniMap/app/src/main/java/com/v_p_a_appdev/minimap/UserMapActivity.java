package com.v_p_a_appdev.minimap;


import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.fragment.app.FragmentActivity;
public abstract class UserMapActivity extends FragmentActivity {
    private LinearLayout menuPopUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadActivity();
        menuPopUp = findViewById(R.id.helperMenu);
    }
    @Override
    protected void onStop() {
        super.onStop();
    }

    protected abstract void loadSetting();

    protected abstract void loadActivity();

    public void openMenu(){
        menuPopUp.setVisibility(View.VISIBLE);
    }
    public void closeMenu(){
        menuPopUp.setVisibility(View.GONE);
    }

}








