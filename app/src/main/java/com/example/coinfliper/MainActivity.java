package com.example.coinfliper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    ImageView coinImage;
    TextView resultText, countText;
    Button flipButton, resetButton;

    Random random;
    int headsCount = 0, tailsCount = 0, totalFlips = 0;

    // Track last result for probabilistic repetition
    int lastResult = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coinImage = findViewById(R.id.coinImage);
        resultText = findViewById(R.id.resultText);
        countText = findViewById(R.id.countText);
        flipButton = findViewById(R.id.flipButton);
        resetButton = findViewById(R.id.resetButton);

        random = new Random();

        flipButton.setOnClickListener(view -> {
            MediaPlayer.create(getApplicationContext(), R.raw.sound).start();

            // Animate the coin flip
            ObjectAnimator rotate = ObjectAnimator.ofFloat(coinImage, "rotationY", 0f, 3600f);
            ObjectAnimator drop = ObjectAnimator.ofFloat(coinImage, "translationY", 0f, -300f);

            rotate.setDuration(2500);
            drop.setDuration(2500);

            AnimatorSet set = new AnimatorSet();
            set.playTogether(rotate, drop);

            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    int toss;

                    // 30% chance to repeat last result, 70% chance to switch
                    if (lastResult != -1 && random.nextInt(10) < 3) {
                        toss = lastResult;
                    } else {
                        toss = random.nextInt(2); // 0 = heads, 1 = tails
                    }

                    totalFlips++;

                    // Display the result and update counts
                    if (toss == 0) {
                        coinImage.setImageResource(R.drawable.heads);
                        resultText.setText("Heads");
                        headsCount++;
                    } else {
                        coinImage.setImageResource(R.drawable.tails);
                        resultText.setText("Tails");
                        tailsCount++;
                    }

                    // Update last result
                    lastResult = toss;

                    updateCountText();

                    // Reset coin position after the animation
                    coinImage.setTranslationY(0f);
                }
            });

            set.start();
        });

        resetButton.setOnClickListener(view -> {
            headsCount = 0;
            tailsCount = 0;
            totalFlips = 0;
            lastResult = -1;
            coinImage.setImageResource(R.drawable.heads);
            resultText.setText("Result");
            updateCountText();
        });

        updateCountText();

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void updateCountText() {
        countText.setText("Total: " + totalFlips + " | Heads: " + headsCount + " | Tails: " + tailsCount);
    }
}

