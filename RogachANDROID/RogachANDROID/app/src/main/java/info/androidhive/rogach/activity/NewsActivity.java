package info.androidhive.rogach.activity;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import info.androidhive.rogach.R;
import info.androidhive.rogach.config.Config;

import java.util.ArrayList;
import java.util.List;



@TargetApi(Build.VERSION_CODES.M)
public class NewsActivity extends AppCompatActivity implements RecyclerView.OnScrollChangeListener  {

    //Creating a List of superheroes
    private List<News> listofNews;

    //Creating Views
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;

    //Volley Request Queue
    private RequestQueue requestQueue;

    //The request counter to send ?page=1, ?page=2  requests
    private int requestCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        listofNews = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);

        getData();

        recyclerView.setOnScrollChangeListener(this);

        adapter = new CardAdapter(listofNews, this);

        recyclerView.setAdapter(adapter);
    }

    private JsonArrayRequest getDataFromServer(int requestCount) {
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);

        progressBar.setVisibility(View.VISIBLE);
        setProgressBarIndeterminateVisibility(true);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Config.DATA_URL + String.valueOf(requestCount),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        parseData(response);
                        progressBar.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(NewsActivity.this, "No More News Available", Toast.LENGTH_SHORT).show();
                    }
                });

        return jsonArrayRequest;
    }

    private void getData() {
        requestQueue.add(getDataFromServer(requestCount));
        requestCount++;
    }

    private void parseData(JSONArray array) {
        for (int i = 0; i < array.length(); i++) {
            News news = new News();
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);

                news.setImageUrl(json.getString(Config.TAG_IMAGE_URL));
                news.setName(json.getString(Config.TAG_NAME));
                news.setText(json.getString(Config.TAG_TEXT));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            listofNews.add(news);
        }

        adapter.notifyDataSetChanged();
    }

    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1)
                return true;
        }
        return false;
    }

    @Override
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        if (isLastItemDisplaying(recyclerView)) {
            getData();
        }
    }
}
