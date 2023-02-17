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

        for(item in apps){

            val cell = inflater.inflate(R.layout.cell, null)
            cell.layoutParams = LinearLayout.LayoutParams(cellHeight, cellWidth)

            var icon = cell.findViewById(R.id.icon_fg) as ImageView
            
            icon.setImageDrawable(item.icon)

            var matrix = ColorMatrix()
            matrix.setSaturation(0.0f)
            icon.setColorFilter(ColorMatrixColorFilter(matrix))

            cell.setOnClickListener(
                View.OnClickListener { view -> onClick(item) }
            )

            contentView.addView(cell)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val data = generateSystemList()

        val grid = findViewById<ScrollView>(R.id.grid) as ScrollView 

        val metrics = DisplayMetrics();
        return getWindowManager().getDefaultDisplay().getMetrics(metrics);

        val cellHeight = metrics.heightPixels / 10;
        val cellWidth = metrics.widthPixels / 6;

        fillGrid(grid, ArrayList<App>(data), cellHeight, cellWidth, { app ->
            this.getPackageManager().getLaunchIntentForPackage(app.info.packageName)
                ?.let { this.startActivity(it) }
        })
    }
}