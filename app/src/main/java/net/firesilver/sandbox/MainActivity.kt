package net.firesilver.sandbox

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Color.*
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ArrayAdapter
import android.widget.ScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.util.DisplayMetrics 
import android.util.Log

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

    fun fillGrid(
        contentView: ViewGroup,
        apps: ArrayList<App>,
        cellHeight: Int,
        cellWidth: Int, 
        onClick: (App) -> Unit,
    ) {

        val inflater: LayoutInflater =
            this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        Log.e("fcrow","filling grid")

        for(item in apps){
            Log.e("fcrow","filling grid "+item.name)

            val cell = inflater.inflate(R.layout.cell, null)
            cell.layoutParams = LinearLayout.LayoutParams(cellHeight, cellWidth)
            Log.e("fcrow","filling grid 2 "+item.name)

            var icon = cell.findViewById(R.id.icon_fg) as ImageView
            Log.e("fcrow","filling grid 3 "+item.name)
            
            icon.setImageDrawable(item.icon)
            Log.e("fcrow","filling grid 4 "+item.name)

            var matrix = ColorMatrix()
            matrix.setSaturation(0.0f)
            icon.setColorFilter(ColorMatrixColorFilter(matrix))
            Log.e("fcrow","filling grid 5 "+item.name)

            cell.setOnClickListener(
                View.OnClickListener { view -> onClick(item) }
            )

            Log.e("fcrow","filling grid 6 "+item.name)
            try {
                contentView.addView(cell)
            }catch(e: Exception){
                Log.e("fcrow","filling grid 7 "+e.toString())
            }
            Log.e("fcrow","filling grid 7 "+item.name)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.e("fcrow","hi there this is a test")

        val data = generateSystemList()

        val grid = findViewById<LinearLayout>(R.id.grid) as LinearLayout 

        val metrics = DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        val cellHeight = metrics.heightPixels / 10;
        val cellWidth = metrics.widthPixels / 6;

        Log.e("fcrow","about to fill grid")
        fillGrid(grid, ArrayList<App>(data), cellHeight, cellWidth, { app ->
            this.getPackageManager().getLaunchIntentForPackage(app.info.packageName)
                ?.let { this.startActivity(it) }
        })
    }
}