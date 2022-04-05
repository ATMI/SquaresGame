package su.mya.squaresgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

	static class GameItem {
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

		// найти игровое поле
		final FrameLayout container = findViewById(R.id.container);

		// установить событие, когда размеры станут известны
		container.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				// !!! важно выполнить этот код только один раз - поэтому сразу убираем обработчик
				container.getViewTreeObserver().removeOnGlobalLayoutListener(this);

				// теперь можно получить размеры игрового поля
				final int containerWidth = container.getWidth();
				final int containerHeight = container.getHeight();

				// здесь считаем куда ставить следующую пару груз-машинка (это координата Y)
				int nextY = 50;

				for (GameItem item : items) {
					// создать imageView для груза и машины
					final ImageView cargo = new ImageView(MainActivity.this);
					final ImageView lorry = new ImageView(MainActivity.this);

					// todo: load with glide
					cargo.setImageResource(item.iconRes);
					lorry.setImageResource(item.iconRes);

					// создать параметры расположения (запомнить!)
					FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
							FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);

					// устанавливаем верхний край
					params.topMargin = nextY;
					// сразу посчитать координаты следующего ряда
					nextY += containerHeight / items.size();

					// грузы ставим по левому краю
					params.leftMargin = 50;
					container.addView(cargo, params);
					// создаем новые параметры на основе предыдущих
					params = new FrameLayout.LayoutParams(params);
					// машинки относительно правого края
					params.rightMargin = 50;
					params.gravity = Gravity.RIGHT;
					container.addView(lorry, params);

					// установить обработчик нажатий для грузов
					cargo.setOnTouchListener((v, e) -> {
						// если нажали - поднять картинку над остальными
						if (e.getAction() == MotionEvent.ACTION_DOWN) {
							v.bringToFront();
						}
						// если тянут ...
						if (e.getAction() == MotionEvent.ACTION_MOVE) {
							v.setX(v.getX() + e.getX() - v.getWidth() / 2f);
							v.setY(v.getY() + e.getY() - v.getHeight() / 2f);
						}
						return true;
					});
				}

			}
		});

	}
}