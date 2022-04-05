package su.mya.squaresgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.graphics.PointF;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
	private int count;

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
						count = gameModel.cargos.size();

						// теперь можно получить размеры игрового поля
						final int containerWidth = container.getWidth();
						final int containerHeight = container.getHeight();

						// здесь считаем куда ставить следующую пару груз-машинка (это координата Y)
						int nextY = 50;

						for (CarModel car : gameModel.cars) {
							// создать imageView для машины
							final ImageView view = new ImageView(MainActivity.this);

							Glide.with(view).load("https://mya.su/" + car.source).into(view);

							// создать параметры расположения (запомнить!)
							FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
									containerWidth / 3, containerWidth / 3);
							// устанавливаем верхний край
							params.topMargin = nextY;
							// машинки относительно правого края
							params.rightMargin = 50;
							params.gravity = Gravity.RIGHT;

							// сразу посчитать координаты следующего ряда
							nextY += containerHeight / gameModel.cars.size();

							// добавляем машину на поле
							container.addView(view, params);

							// запоминаем в модели ее вьюху
							car.view = view;
						}

						// начинаем опять сверху
						nextY = 50;

						for (CargoModel cargo : gameModel.cargos) {
							// создать imageView для груза
							final ImageView image = new ImageView(MainActivity.this);

							Glide.with(image).load("https://mya.su/" + cargo.source).into(image);

							// создать параметры расположения (запомнить!)
							FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
									containerWidth / 3, containerWidth / 3);
							// устанавливаем верхний край
							params.topMargin = nextY;
							// грузы ставим по левому краю
							params.leftMargin = 50;
							// ставим на поле
							container.addView(image, params);

							// сразу посчитать координаты следующего ряда
							nextY += containerHeight / gameModel.cargos.size();

							// установить обработчик нажатий для грузов
							image.setOnTouchListener(new View.OnTouchListener() {
								@Override
								public boolean onTouch(View view, MotionEvent motionEvent) {
									// если нажали - поднять картинку над остальными
									if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
										view.bringToFront();
									}
									// если тянут ...
									if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
										view.setX(view.getX() + motionEvent.getX() - view.getWidth() / 2f);
										view.setY(view.getY() + motionEvent.getY() - view.getHeight() / 2f);
									}
									// если отпустили
									if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
										// проверяем все машины
										for (CarModel car : gameModel.cars) {
											// если совпадают ид
											if (car.id == cargo.id) {
												// проверяем расстояния по иксам и игрекам что попали
												final float dx = Math.abs(car.view.getX() - view.getX());
												final float dy = Math.abs(car.view.getY() - view.getY());
												if (dx < 50 && dy < 50) {
													view.setOnTouchListener(null);
													// если надо удалить
													// container.removeView(car.view);
													// container.removeView(view);
													count--;
													if (count == 0) {
														Toast.makeText(MainActivity.this, "WIN!", Toast.LENGTH_SHORT).show();
													}
												}
											}
										}

									}
									return true;
								}
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