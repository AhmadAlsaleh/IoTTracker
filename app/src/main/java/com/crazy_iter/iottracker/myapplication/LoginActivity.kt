package com.crazy_iter.iottracker.myapplication

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginBTN.setOnClickListener {
            if (loginUsernameET.text.isNotEmpty() && loginPasswordET.text.isNotEmpty()) {
                login(loginUsernameET.text.toString(), loginPasswordET.text.toString())
            } else {
                Toast.makeText(this, "Check Your Input", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun login(username: String, password: String) {
        val loginObject = JSONObject()
        loginObject.put("UserName", username)
        loginObject.put("Password", password)

        LoginLoadingRL.visibility = View.VISIBLE
        val queue = Volley.newRequestQueue(this)
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, URLs.login, loginObject, {

            val sharedPreferences = getSharedPreferences("tracker", Context.MODE_PRIVATE).edit()
            sharedPreferences.putString("id", it.getString("accountID"))
            sharedPreferences.apply()
            Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show()

            startActivity(Intent(this, MapsActivity::class.java))
            finish()

        }, {
            LoginLoadingRL.visibility = View.GONE
            Toast.makeText(this, "Try Again", Toast.LENGTH_SHORT).show()
        })

        queue.add(jsonObjectRequest)
    }

}
