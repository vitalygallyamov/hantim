package com.example.hantim;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class DetailJobActivity extends Activity {
	
	private TextView title;
	private TextView date;
	private TextView text;
	private ProgressDialog pg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_job);
		
		//Получаем url
		Intent intent = getIntent();
		final String url = intent.getStringExtra("url");
		
		title = (TextView) findViewById(R.id.title);
		date = (TextView) findViewById(R.id.date);
		text = (TextView) findViewById(R.id.text);
		
		pg = ProgressDialog.show(this, null, "Загрузка...", true);
		
		new GetHTMLTask().execute(url);
	}
	
	private class GetHTMLTask extends AsyncTask<String, Void, Document>{

		@Override
		protected Document doInBackground(String... url) {
			// TODO Auto-generated method stub
			Document doc = null;
			try {
				doc = Jsoup.connect(url[0]).get();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return doc;
		}
		
		@Override
		protected void onPostExecute(Document doc) {
			// TODO Auto-generated method stub
			super.onPostExecute(doc);
			
			if(pg!= null && pg.isShowing()){
            	pg.dismiss();
            }
			
			if(doc != null){
				Elements elements = doc.select("h2"); // Достаем заголовок страницы
				for (Element el : elements) {
					title.setText(el.text());
				}
				
				elements = doc.select("#job .published"); // Достаем дату публикации
				for (Element el : elements) {
					date.setText("Опубликовано: " + el.text());
				}
				
				elements = doc.select("#job .requirements .text"); // Достаем html с текстом о вакансии
				for (Element el : elements) {
					text.setText(Html.fromHtml(el.html()));
				}
			}
		}
		
	}
}
