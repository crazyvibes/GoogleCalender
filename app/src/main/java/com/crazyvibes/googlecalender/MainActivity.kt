package com.crazyvibes.googlecalender

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {
    private val RC_SIGN_IN = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        signIn()
    }

    private fun signIn() {
        Log.e("TAG", "test1" )
        val signInClient = GoogleSignIn.getClient(this,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Scope("https://www.googleapis.com/auth/calendar"))
                .requestEmail()
                .build())

        startActivityForResult(signInClient.signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.e("TAG", "test2" )
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            Log.e("TAG", "test3" )
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            Log.e("TAG", "handleSignInResult: "+account.email )
            Log.e("TAG", "handleSignInResult: "+account.displayName )
            // You can now use the account to access Google APIs
            getCalendarEvents(account)
        } catch (e: ApiException) {
            // Handle sign-in failure
            Log.e("TAG", "handleSignInResult: "+e.message )
        }

    }
    private fun getCalendarEvents(account: GoogleSignInAccount) {
        val transport = AndroidHttp.newCompatibleTransport()
        val jsonFactory: JsonFactory = GsonFactory.getDefaultInstance()

        val service = com.google.api.services.calendar.Calendar.Builder(
           // transport, jsonFactory, account.account!!)
            transport, jsonFactory, null)
            .setApplicationName("Your Application Name")
            .build()

        val events = service.events().list("primary")
            .setMaxResults(10)
            .execute()

        Log.e("TAG", "getCalendarEvents: "+ Gson().toJson(events) )
        // Process the events retrieved
    }
}