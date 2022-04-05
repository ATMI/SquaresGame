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

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

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

				Api.Api.getCarGame("Cars").enqueue(new Callback<GameModel>() {
					@Override
					public void onResponse(Call<GameModel> call, Response<GameModel> response) {
						final GameModel gameModel = response.body();
						Collections.shuffle(gameModel.cars);
						Collections.shuffle(gameModel.cargos);

						// теперь можно получить размеры игрового поля
						final int containerHeight = container.getHeight();

						// здесь считаем куда ставить следующую пару груз-машинка (это координата Y)
						int nextY = 100;

						for (CarModel car : gameModel.cars) {
							// создать imageView для машины
							final ImageView image = new ImageView(MainActivity.this);

							Glide.with(image).load("https://mya.su/" + car.source).into(image);

							// создать параметры расположения (запомнить!)
							FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(250, 250);
							// устанавливаем верхний край
							params.topMargin = nextY;
							// машинки относительно правого края
							params.rightMargin = 100;
							params.gravity = Gravity.RIGHT;

							// сразу посчитать координаты следующего ряда
							nextY += containerHeight / gameModel.cars.size();

							// добавляем машину на поле
							container.addView(image, params);
						}

						// начинаем опять сверху
						nextY = 100;

						for (CargoModel cargo : gameModel.cargos) {
							// создать imageView для груза
							final ImageView image = new ImageView(MainActivity.this);

							Glide.with(image).load("https://mya.su/" + cargo.source).into(image);

							// создать параметры расположения (запомнить!)
							FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(250, 250);
							// устанавливаем верхний край
							params.topMargin = nextY;
							// грузы ставим по левому краю
							params.leftMargin = 100;
							// ставим на поле
							container.addView(image, params);

							// сразу посчитать координаты следующего ряда
							nextY += containerHeight / gameModel.cargos.size();

							// установить обработчик нажатий для грузов
							image.setOnTouchListener((v, e) -> {
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

					@Override
					public void onFailure(Call<GameModel> call, Throwable t) {
						int a = 0;
					}
				});

			}
		});

	}
}