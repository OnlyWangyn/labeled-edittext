package org.denis;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * @author Denis Zhdanov
 * @since 12/08/14 15:54
 */
public class StartActivity extends Activity {

    View mLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);

        mLabel = findViewById(R.id.test_label);
        View button = findViewById(R.id.my_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animation = AnimationUtils.loadAnimation(StartActivity.this, R.anim.my_scale);
                mLabel.startAnimation(animation);
            }
        });
    }
}
