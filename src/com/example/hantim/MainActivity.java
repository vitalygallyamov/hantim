package com.example.hantim;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.hantim.adapters.JobsAdapter;
import com.example.hantim.manage.JsonManager;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class MainActivity extends ListActivity {

	//Константы
	public static final int CONN_WIFI = ConnectivityManager.TYPE_WIFI;
	public static final int CONN_ANY = ConnectivityManager.TYPE_MOBILE;
	public static final String JSON_URL = "http://hantim.ru/search.json";
	
	private PullToRefreshListView mPullRefreshListView;
	
	public static final String LOG_TAG = "Hantim";
	
	private BroadcastReceiver br = null;
	private JobsAdapter jobAdapter = null;
	private ProgressDialog pg;
	
	private ArrayList<Job> currentArrayJobs = new ArrayList<Job>(); // Массив всех вакансий
	
	private int currentPage = 1; // Счетчик для отслеживания сколько страниц загружено
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);       
        mPullRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				new GetDataTask().execute();
			}
        	
		});
        
        //Ресивер для проверки подключения к сети
        br = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				ConnectivityManager conn =  (ConnectivityManager)
						context.getSystemService(Context.CONNECTIVITY_SERVICE);
				
				//Получаем инфу о подключении
				NetworkInfo networkInfo = conn.getActiveNetworkInfo();
				
				//Если подключен
				if (networkInfo != null && 
						(networkInfo.getType() == CONN_WIFI || networkInfo.getType() == CONN_ANY)) {
					pg = ProgressDialog.show(context, null, "Загрузка...", true);
					new GetDataTask().execute();
				}else{ //Не подключен
					Toast.makeText(getApplicationContext(), "Необходимо подключение к сети.", 
							Toast.LENGTH_LONG).show();
				}
			}
		};
		
		//Регистрируем ресивер
		IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(br, filter);
        
        jobAdapter = new JobsAdapter(getApplicationContext(), R.layout.job_item, currentArrayJobs);
        (mPullRefreshListView.getRefreshableView()).setAdapter(jobAdapter);
        
    }
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	if (br != null){
			this.unregisterReceiver(br);
		}
    }
    
    //Если выбрали один из пунктов списка
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	// TODO Auto-generated method stub
    	super.onListItemClick(l, v, position, id);
    	
    	Job j = (Job) v.getTag();
		
    	//Создаем интент на активити детального просмотра вакансии
		Intent intent = new Intent(this, DetailJobActivity.class);
		intent.putExtra("url", j.url);
		
		startActivity(intent);
    	
    }
    
    //Метод, который получает массив вакансий постранично
    private ArrayList<Job> getJobs(int page){
    	JsonManager jm = new JsonManager(JSON_URL + "?page=" + page);
    	String json_result = jm.getJsonResult();
    	
    	ArrayList<Job> arrayJobs = null;
    	
    	try {
			JSONObject jo = new JSONObject(json_result);
			
			//Получаем массив jobs из JSON
			JSONArray ja = jo.getJSONArray("jobs");
			
			if(ja.length() == 0){ //нет вакансий
				return new ArrayList<Job>();
			}
			
			Job job = null;
			arrayJobs = new ArrayList<Job>(ja.length());
			
			for(int i = 0; i < ja.length(); i++){
				jo = ja.getJSONObject(i);
				job = new Job(jo.getString("url"), jo.getString("title"), jo.getString("published"));
				arrayJobs.add(job);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return arrayJobs;
    }
    
    //Класс для хранения вакансии
    public class Job{
    	public String url;
    	public String title;
    	public String published;
    	
    	public Job(String url, String title, String published){
    		this.url = url;
    		this.title = title;
    		this.published = published;
    	}

    }
    
    //Для аснихронной подгрузки записей
    private class GetDataTask extends AsyncTask<Void, Void, ArrayList<Job>> {

        @Override
        protected ArrayList<Job> doInBackground(Void... params) {

            return getJobs(currentPage);
        }

        @Override
        protected void onPostExecute(ArrayList<Job> result) {
        	if(result.size() != 0){ // если есть новые вакансии
        		if(currentArrayJobs.size() == 0){ // если ничего нет в текущем массиве, то добавляем
            		currentArrayJobs.addAll(result);
            	}else{ //Добавляем новые вакансии
            		currentArrayJobs.addAll(currentArrayJobs.size(), result);
            	}
                
                currentPage++;
        	}
        	
        	jobAdapter.notifyDataSetChanged();
            mPullRefreshListView.onRefreshComplete();
        	
        	if(pg!= null && pg.isShowing()){
            	pg.dismiss();
            }
				
            super.onPostExecute(currentArrayJobs);
        }
    }

}
