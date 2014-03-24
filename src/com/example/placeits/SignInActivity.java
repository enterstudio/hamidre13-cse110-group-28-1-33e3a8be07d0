package com.example.placeits;

import java.util.ArrayList;

import Database.MainDataSource;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/*
 * SignInActivity allows the user to log in or sign up
 */
public class SignInActivity extends Activity
{
	private EditText userNameEdit;
	private EditText passwordEdit;
	private Button signInButton;
	private Button signUpButton;
	protected MainDataSource dataSource = MainDataSource.getInstance(this);
	
	// displays the visual elements of the activity
	protected void onCreate (Bundle savedInstanceState)
	{
		 super.onCreate(savedInstanceState);
	     setContentView(R.layout.sign_in);
	     dataSource = MainDataSource.getInstance(this);
	     setUpUIElements();
	}
	
	//listener for the sign-in or sign-up buttons
	private View.OnClickListener onClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			switch(v.getId())
			{
				case R.id.sign_in_button:
					/** TODO
					 * 		1. Extract the typed-in user name from userNameEdit, and check to see if the user name exists.
					 * 			a. User name does not exist: Show a TOAST message displaying: "User name does not exist."
					 * 			b. User name exists: Move to #2.
					 * 		2. Extract the typed-in password from passwordEdit, and check to see if the password matches the user name.
					 * 			a. Password does not match the user name: Show a TOAST message displaying: "Password does not match user name."
					 * 			b. Password matches the user name: Move to #3.
					 * 		3. Create the intent which will also pass the appropriate MainDataSource database to MainActivity along with the
					 * 			appropriate Google App Engine (not sure if this how it should work exactly, but we need to get to MainActivity
					 * 			with the correct Place It information.
					 */
					String user_name = userNameEdit.getText().toString();
					String password = passwordEdit.getText().toString();
					
					// Query remote database to find specified user name.
					if(dataSource.authUser(user_name, password))
					{
						
						//populate datasource
						Intent intent = new Intent(getApplicationContext(), MainActivity.class);
						startActivity(intent);
						
					}
					else
					{
						Toast.makeText(getApplicationContext(), "Login Failed!", Toast.LENGTH_LONG).show();
					}
					
				break;
				
				case R.id.sign_up_button:
					signUp();
				break;
			}
		}
	};
	
	// creates a new intent for the sign up activity
	private void signUp()
	{
		Intent intent = new Intent(this, SignUpActivity.class);
		startActivity(intent);
	}
	
	//sets up the buttons and edit text elements
	private void setUpUIElements()
	{
		userNameEdit = (EditText) findViewById(R.id.user_name_edit);
		passwordEdit = (EditText) findViewById(R.id.password_edit);
		signInButton = (Button) findViewById(R.id.sign_in_button);
		signUpButton = (Button) findViewById(R.id.sign_up_button);
		
		signInButton.setOnClickListener(onClickListener);
		signUpButton.setOnClickListener(onClickListener);
	}
}
