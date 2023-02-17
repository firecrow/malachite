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
import android.view.ViewGroup.LayoutParams
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

val fixedPositionMap = mutableMapOf(
    "net.firesilver.sandbox" to 0,
    "com.google.android.deskclock" to 1,
    "com.android.chrome" to 2,
    "com.google.android.apps.messaging" to 3,
    "com.google.android.calendar" to 4,
    "com.lastpass.lpandroid" to 5,
    "com.lastpass.lpandroid" to 6,
    "com.google.android.gm" to 7,
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
        }.sortedWith(object : Comparator <App> {
            override fun compare (a: App, b: App) : Int {
                val positionA = fixedPositionMap.get(a.info.packageName)
                val positionB = fixedPositionMap.get(b.info.packageName)
                if(positionA != null && positionB != null){
                    return positionA - positionB 
                }
                if(positionA != null && positionB == null){
                    return -1 
                }
                if(positionA == null && positionB != null){
                    return 1 
                }
                return a.name.compareTo(b.name)
            }
        })
    }

    fun onClickInterceptor(app: App): Boolean {
        Log.e("fcrow", "clicked on "+app.info.packageName)
        if(app.info.packageName == "net.firesilver.sandbox"){
            Toast.makeText(this, "Hi its the master app", Toast.LENGTH_LONG).show()

            return true;
        }
        return false;
    }

    fun fillGrid(
        contentView: ViewGroup,
        apps: ArrayList<App>,
        totalWidth: Int,
        cellHeight: Int,
        cellWidth: Int, 
        onClick: (App) -> Unit,
    ) {

        val inflater: LayoutInflater =
            this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        fun makeRow(): LinearLayout {
            val row = LinearLayout(this)
            row.layoutParams = LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            return row
        }

        var pxPosition = 0;
        var row = makeRow();

        for(item in apps){
            val cell = inflater.inflate(R.layout.cell, null)
            cell.layoutParams = LinearLayout.LayoutParams(cellWidth, cellHeight)

            var icon = cell.findViewById(R.id.icon_fg) as ImageView
            icon.setImageDrawable(item.icon)

            var matrix = ColorMatrix()
            matrix.setSaturation(0.0f)
            icon.setColorFilter(ColorMatrixColorFilter(matrix))

            cell.setOnClickListener(
                View.OnClickListener { view -> onClick(item) }
            )

            if(pxPosition + cellWidth > totalWidth){
                contentView.addView(row)
                row = makeRow()
                pxPosition = 0
            }
            pxPosition += cellWidth

            Log.e("fcrow", "position is ("+pxPosition+") "+ pxPosition / cellWidth +" for: " + item.name);
            row.addView(cell)
        }
        contentView.addView(row)
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
        fillGrid(grid, ArrayList<App>(data), metrics.widthPixels, cellHeight, cellWidth, { app ->
            if(!onClickInterceptor(app)){
                this.getPackageManager().getLaunchIntentForPackage(app.info.packageName)
                   ?.let { this.startActivity(it) }
            }
        })
    }
}