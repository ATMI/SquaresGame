package su.mya.squaresgame;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

	static class GameItem {
/*
		todo: url + glide
		public final String iconRes;

		GameItem(String iconRes) {
			this.iconRes = iconRes;
		}
*/

		public final int iconRes;

		GameItem(int iconRes) {
			this.iconRes = iconRes;
		}
	}

	static final ArrayList<GameItem> items = new ArrayList<GameItem>() {{
		add(new GameItem(R.drawable.ic_launcher_background));
		add(new GameItem(R.drawable.ic_launcher_foreground));
		add(new GameItem(R.drawable.ic_launcher_background));
	}};

	@SuppressLint("ClickableViewAccessibility")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final GridLayout container = findViewById(R.id.container);

		for (GameItem item : items) {
			final GridLayout.LayoutParams params = new GridLayout.LayoutParams(
					GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1.f),
					GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1.f)
			);

			final ImageView imageView = new ImageView(this);
			// todo: load with glide
			imageView.setImageResource(item.iconRes);
			// todo: touch listener
			// imageView.setOnTouchListener(null);
			imageView.setOnTouchListener((v, e) -> {
				if (MotionEvent.ACTION_MOVE == e.getAction()) {
					v.setX(v.getX() + 1);
					v.setY(v.getY() + 1);
				}
				return true;
			});
			container.addView(imageView, params);
		}
	}
}