package com.example.placeits;

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
 * SignUp Activity allows the user to sign up for an account
 */
public class SignUpActivity extends Activity
{
	private EditText userNameEdit;
	private EditText passwordEdit;
	private EditText passwordConfirmEdit;
	private Button signUpButton;
	private Button cancelButton;
	protected MainDataSource dataSource = MainDataSource.getInstance(this);
	
	//displays the visual elements of this activity
	protected void onCreate (Bundle savedInstanceState)
	{
		 super.onCreate(savedInstanceState);
	     setContentView(R.layout.sign_up);
	     
	     setUpUIElements();
	}
	
	//listener for the signup and cancel button
	private View.OnClickListener onClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			switch(v.getId())
			{
				case R.id.sign_up_button:
					/** TODO
					 * 		1. Extract the typed-in user name from userNameEdit, and check to see if the user name exists.
					 * 			a. User name exists: Show a TOAST message displaying: "User name is already taken."
					 * 			b. User name does not exist: Move to #2.
					 * 		2. Extract the typed-in passwords from passwordEdit and passwordConfirmEdit, and check to see if the passwords match.
					 * 			a. Passwords do not match: Show a TOAST message displaying: "Different passwords were entered."
					 * 			b. Passwords match: Move to #3.
					 * 		3. Store the user name and password into whatever is holding that information, and return to the SignInActivity.
					 * 			Also, display a TOAST message which notifies the user that his/her account was created.
					 */
					String user_name = userNameEdit.getText().toString();
					String password = passwordEdit.getText().toString();
					
					String password_confirm = passwordConfirmEdit.getText().toString();
					if(password.equals(password_confirm))
					{
					  if(!dataSource.createUser(user_name, password))
					  {
						  Toast.makeText(getApplicationContext(), "ERROR: User already exists!", Toast.LENGTH_LONG).show();
					  }
					  else
					  {
						  Toast.makeText(getApplicationContext(), "User created successfully!", Toast.LENGTH_LONG).show();
						  Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
							startActivity(intent);
					  }
					}
					else
					{
						Toast.makeText(getApplicationContext(), "ERROR: Passwords match! Try again", Toast.LENGTH_LONG).show();
					}
				break;
				
				case R.id.cancel_button:
					finish();
				break;
			}
		}
	};

	// sets up the buttons and edit text elements
	private void setUpUIElements()
	{
		userNameEdit = (EditText) findViewById(R.id.user_name_edit);
		passwordEdit = (EditText) findViewById(R.id.password_edit);
		passwordConfirmEdit = (EditText) findViewById(R.id.password_confirm_edit);
		signUpButton = (Button) findViewById(R.id.sign_up_button);
		cancelButton = (Button) findViewById(R.id.cancel_button);
		
		signUpButton.setOnClickListener(onClickListener);
		cancelButton.setOnClickListener(onClickListener);
	}
}
