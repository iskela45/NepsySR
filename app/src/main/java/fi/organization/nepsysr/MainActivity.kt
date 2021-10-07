package fi.organization.nepsysr

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater

        // Inflate the menu; this adds items to the action bar if it's present.
        inflater.inflate(R.menu.my_menu, menu)
        return true

    }

    // Determine if action bar item was selected. If true do corresponding action.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        Log.d("TAG" , "works")

        // handle presses on the action bar menu.
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, Settings::class.java)
                startActivity(intent)
                true
            }
        else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
    }

}