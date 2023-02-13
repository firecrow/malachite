package net.firesilver.sandbox

import android.os.Bundle
import android.widget.Toast
import android.widget.TextView
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import android.content.pm.PackageManager
import android.widget.AbsListView
import android.content.pm.ApplicationInfo
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.Bitmap
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.Color
import android.graphics.Color.*

data class App (
    val name: String,
    val info: ApplicationInfo,
    val icon: Drawable,
)

class MainActivity : AppCompatActivity() {
    private lateinit var textDump: TextView

    fun asAdaptive(icon: Drawable): AdaptiveIconDrawable{
        var defaultBackground = ColorDrawable(Color.WHITE)

        if(icon !is AdaptiveIconDrawable){
            return AdaptiveIconDrawable(defaultBackground, icon)
        }
        return icon
    }

    fun generateSystemList(): List<App>{
        val pm = this.getPackageManager()
        return pm.getInstalledApplications(PackageManager.GET_META_DATA).filter { app -> 
            pm.getLaunchIntentForPackage(app.packageName) != null
        }.map { app ->
           var foreground = asAdaptive(pm.getApplicationIcon(app)).foreground
           var iconImage = foreground
           if(foreground is BitmapDrawable){
                iconImage = BitmapDrawable(Bitmap.createScaledBitmap(foreground.getBitmap(), 100, 100, true)) 
           }

           App(pm.getApplicationLabel(app).toString(), app, iconImage)
        }.sortedBy { it.name }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val data = generateSystemList()

        val grid = findViewById<GridView>(R.id.grid) as GridView
        val adapter = GridAdapter(this, ArrayList<App>(data))
        grid.setAdapter(adapter)

        grid.setOnItemClickListener { parent, view, idx, id ->
            val app: App = adapter.getItem(idx)
            this.getPackageManager().getLaunchIntentForPackage(app.info.packageName)
                ?.let { this.startActivity(it) }
        }
    }
}