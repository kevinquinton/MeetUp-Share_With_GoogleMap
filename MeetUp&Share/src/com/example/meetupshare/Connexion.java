package com.example.meetupshare;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.models.User;
import com.example.webservice.Webservice;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

/**
 * Connexion d'un utilisateur
 * @author Romain
 *
 */
public class Connexion extends Activity{

	private EditText mEmail, mPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connexion);
		mEmail = (EditText) findViewById(R.id.editText1);
		mPassword = (EditText) findViewById(R.id.editText2);
	}

	/**
	 * Methode permettant la connexion d'un utilisateur a son compte
	 * @param view
	 */
	public void connectToAccount(View view) {
		if(isOnline()){
			RequestParams params = new RequestParams();
			params.put("email", mEmail.getText().toString());
			params.put("pwd", mPassword.getText().toString());

			String file = Webservice.usersMethod();

			Webservice.get(file+"?method=connexionuser", params, new JsonHttpResponseHandler(){
				public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
					Log.d("connexion_user", "success");
					Log.d("", ""+response.optString("connexion"));
					if(response.optString("connexion").equals("true")){
						User user = new User();
						user.setId(Long.parseLong(response.optString("id")));
						user.setEmail(response.optString("email"));
						user.setLastname(response.optString("lname"));
						user.setFirstname(response.optString("fname"));

						Intent intent = new Intent(Connexion.this, Home.class);
						Bundle bundle = new Bundle();
						bundle.putSerializable("currentUser", user);
						intent.putExtras(bundle);
						startActivity(intent);
						finish();
					}else{
						Toast toast = Toast.makeText(getApplicationContext(), "Identifiants incorrects", Toast.LENGTH_SHORT);
						toast.show();
					}
				}

				public void onFailure(int statusCode, Header[] headers, String s, Throwable e) {
					Log.d("connexion_user", "failure");
				}

			});	
		} else {
			Toast toast = Toast.makeText(getApplicationContext(), "Aucune connexion d�tect�e", Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	/**
	 * Passage activity pour l'inscription de l'user
	 * @param view
	 */
	public void signIn(View view){
		if(isOnline()){
			Intent intent = new Intent(Connexion.this, SignIn.class);
			startActivity(intent);
		} else {
			Toast toast = Toast.makeText(getApplicationContext(), "Aucune connexion d�tect�e", Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	/**
	 * Permet de verifier si l'utilisateur est en ligne ou non
	 * @return boolean
	 */
	private boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}
	
}
