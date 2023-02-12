package net.firesilver.sandbox

import android.os.Bundle
import android.widget.Toast
import android.widget.TextView
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import android.content.pm.PackageManager
import android.widget.AbsListView
import android.content.pm.ApplicationInfo

class MainActivity : AppCompatActivity() {
    private lateinit var textDump: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textDump = findViewById(R.id.text_dump)

        var content = ""

        val pm = this.getPackageManager()
        val data = pm.getInstalledApplications(PackageManager.GET_META_DATA).filter { app -> 
            pm.getLaunchIntentForPackage(app.packageName) != null
        }
        textDump.text = content 

        val grid = findViewById<GridView>(R.id.grid) as GridView
        val adapter = GridAdapter(this, ArrayList<ApplicationInfo>(data))
        grid.setAdapter(adapter)

        Toast.makeText(this, "Hello!", Toast.LENGTH_SHORT).show()
    }
}