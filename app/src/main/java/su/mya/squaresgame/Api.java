package su.mya.squaresgame;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {
	Api Api = new Retrofit.Builder()
			.baseUrl("https://mya.su/")
			.addConverterFactory(GsonConverterFactory.create())
			.build()
			.create(Api.class);

	@GET("game")
	Call<GameModel> getCarGame(@Query("name") String name);
}
