package net.firesilver.sandbox

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Color.*
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.drawable.*
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

val ALL = 0
val EDIT = 1
val STARRED = 2

val APPSVIEW_BEHAVIOUR = 0
val REFRESH_BEHAVIOUR = 1
val ALL_BEHAVIOUR = 2
val STARRED_BEHAVIOUR = 3
val EDITVIEW_BEHAVIOUR = 4
val NOOP_BEHAVIOUR = 5

val COLOR_ADJ_BRIGHT = 6
val COLOR_ADJ_MUTED = 7

val COLOR_BG2 = "#FF222222"

data class App (
    val name: String,
    val info: ApplicationInfo?,
    val icon: Drawable,
    val behaviourIdx: Int?,
)

fun getTile(behaviour: Int): App {

    if(behaviour == EDITVIEW_BEHAVIOUR) {
        return App(
            "net.firesilver.custom.editview",
            null,
            ColorDrawable(Color.parseColor("#00000000")),
            EDITVIEW_BEHAVIOUR
        )
    }
    if(behaviour == APPSVIEW_BEHAVIOUR) {
        return App(
            "net.firesilver.custom.appsview",
            null,
            ColorDrawable(Color.parseColor("#00000000")),
            APPSVIEW_BEHAVIOUR
        )
    }
    if(behaviour == REFRESH_BEHAVIOUR) {
        return App(
            "net.firesilve.custom.refresh",
            null,
            ColorDrawable(0xffffff),
            REFRESH_BEHAVIOUR
        )
    }
    if(behaviour == ALL_BEHAVIOUR) {
        return App(
            "net.firesilve.custom.all",
            null,
            ColorDrawable(Color.parseColor("#00FF44")),
            ALL_BEHAVIOUR
        )
    }
    if(behaviour == STARRED_BEHAVIOUR) {
        return App(
            "net.firesilve.custom.starred",
            null,
            ColorDrawable(Color.parseColor("#FFFFFF")),
            STARRED_BEHAVIOUR
        )
    }

    return App(
        "default",
        null,
        ColorDrawable(0XFF0000),
        NOOP_BEHAVIOUR
    )
}

val fixedPositionMap = mutableMapOf(
    "net.firesilver.sandbox" to 0,
    "com.android.settings" to 1,
    "com.android.chrome" to 2,
    "com.google.android.apps.messaging" to 3,
    "com.google.android.calendar" to 4,
    "com.google.android.gm" to 5,
    "com.google.android.apps.maps" to 0,
    "com.google.android.apps.photos" to 7,
    "com.linkedin.android" to 8,
    "com.google.android.apps.tasks" to 9,
    "com.sec.android.app.camera" to 10,
    "com.transferwise.android" to 11,
    "com.x8bit.bitwarden" to 12,
    "com.samsung.android.dialer" to 13,
    "com.extreamsd.usbaudioplayerpro" to 14,
    "com.google.android.apps.authenticator2" to 15,
    "com.samsung.android.aremojieditor" to -1,
    "com.samsung.android.arzone" to -1,
    "com.samsung.accessibility" to -1,
    "com.samsung.android.bixby.agent" to -1,
    "com.samsung.android.app.tips" to -1,
    "com.samsung.android.app.taskedge" to -1,
    "com.android.traceur" to -1,
    "com.sec.android.easyMover" to -1,
    "com.sec.location.nfwlocationprivacy" to -1,
    "com.samsung.android.tvplus" to -1,
    "com.samsung.android.samsungpass" to -1,
    "com.samsung.android.app.spage" to -1,
    "com.sec.android.app.sbrowser" to -1,
    "com.samsung.android.privateshare" to -1,
    "com.samsung.android.messaging" to -1,
    "com.verizon.messaging.vzmsgs" to -1,
    "com.google.audio.hearing.visualization.accessibility.scribe" to -1,
    "com.microsoft.appmanager" to -1,
    "com.google.android.googlequicksearchbox" to -1,
    "com.samsung.android.game.gamehome" to -1,
    "com.sec.android.gallery3d" to -1,
    "com.sec.android.app.samsungapps" to -1,
    "com.samsung.android.gru" to -1,
    "com.samsung.android.app.galaxyfinder" to -1,
    "com.samsung.android.lool" to -1,
    "com.sec.android.app.clockpackage" to -1,
    "com.vcast.mediamanager" to -1,
    "com.vzw.ecid" to -1,
)

val colorAdjMap = mutableMapOf(
    "com.transferwise.android" to COLOR_ADJ_BRIGHT,
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

    fun generateSystemList(filtered: Boolean): List<App>{
        val pm = this.getPackageManager()

        val tiles = pm.getInstalledApplications(PackageManager.GET_META_DATA).filter { app ->
            pm.getLaunchIntentForPackage(app.packageName) != null
        }.map { app ->
            var foreground = asAdaptive(pm.getApplicationIcon(app)).foreground
            var iconImage = foreground
            if (foreground is BitmapDrawable) {
                iconImage = BitmapDrawable(
                    Bitmap.createScaledBitmap(
                        foreground.getBitmap(),
                        100,
                        100,
                        true
                    )
                )
            }
            App(pm.getApplicationLabel(app).toString(), app, iconImage, null)
        }.filter { a ->
            val idx = fixedPositionMap.get(a.info?.packageName)
            (!filtered || idx != null) && idx != -1
        }.sortedWith(object : Comparator <App> {
            override fun compare (a: App, b: App) : Int {
                if(a.info == null){
                    // for now custom tiles do not sort
                    return 1
                }
                val positionA = fixedPositionMap.get(a.info?.packageName)
                val positionB = fixedPositionMap.get(b.info?.packageName)
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

        val mtiles = tiles.toMutableList()
        return listOf(getTile(EDITVIEW_BEHAVIOUR)) + mtiles
    }

    fun onClickInterceptor(app: App): Boolean {
        if(app.info == null){
            if (app.behaviourIdx == EDITVIEW_BEHAVIOUR) {
                updateAppList(EDIT)
            }else if (app.behaviourIdx == APPSVIEW_BEHAVIOUR) {
                updateAppList(ALL)
            }else if (app.behaviourIdx == STARRED_BEHAVIOUR) {
                updateAppList(STARRED)
            }
            return true
        }
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
        contentView.removeAllViews()

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

        var i = 0
        var r = 0
        for(item in apps){
            val cell = inflater.inflate(R.layout.cell, null)
            cell.layoutParams = LinearLayout.LayoutParams(cellWidth, cellHeight)

            var icon = cell.findViewById(R.id.icon_fg) as ImageView
            icon.setImageDrawable(item.icon)

            Log.d("fcrow", "fcrow " + item.info?.packageName)

            val colorAdj = colorAdjMap.get(item.info?.packageName)

            if(colorAdj == COLOR_ADJ_MUTED) {
                 var matrix = ColorMatrix()
                 matrix.setSaturation(0.0f)
                 icon.setColorFilter(ColorMatrixColorFilter(matrix))
            }

            if(colorAdj == COLOR_ADJ_BRIGHT) {
                val matrix = ColorMatrix(
                    floatArrayOf(
                        1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
                        0.0f, 1.0f, 0.0f, 0.0f, 165.0f,
                        0.0f, 0.0f, 1.0f, 0.0f, 100.0f,
                        0.0f, 0.0f, 0.0f, 1.0f, 0.0f
                    )
                )
                icon.setColorFilter(ColorMatrixColorFilter(matrix))
            }

            cell.setOnClickListener(
                View.OnClickListener { view -> onClick(item) }
            )

            if(pxPosition + cellWidth > totalWidth){
                contentView.addView(row)
                row = makeRow()
                r++
                pxPosition = 0
            }
            pxPosition += cellWidth

            /*
            i++
            if((i + r).mod(2) == 0) {
                val clr = ColorDrawable(Color.parseColor(COLOR_BG2))
                cell.background = clr
            }
            */

            row.addView(cell)
        }

        contentView.addView(row)
    }

    fun generateEditList(): List<App> {
        val tiles = mutableListOf<App>()
        tiles.add(getTile(APPSVIEW_BEHAVIOUR))
        tiles.add(getTile(REFRESH_BEHAVIOUR))
        tiles.add(getTile(ALL_BEHAVIOUR))
        tiles.add(getTile(STARRED_BEHAVIOUR))
        return tiles
    }

    fun updateAppList(state: Int) {
        var data: List<App> = mutableListOf()
        if(state == ALL) {
            data = generateSystemList(false)
        }else if (state == EDIT){
            data = generateEditList()
        }else if (state == STARRED){
            data = generateSystemList(true)
        }

        val grid = findViewById<LinearLayout>(R.id.grid) as LinearLayout

        val metrics = DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        val cellHeight = metrics.heightPixels / 10;
        val cellWidth = metrics.widthPixels / 6;

        fillGrid(grid, ArrayList<App>(data), metrics.widthPixels, cellHeight, cellWidth, { app ->
            if(!onClickInterceptor(app) && app.info != null){
                this.getPackageManager().getLaunchIntentForPackage(app.info.packageName)
                    ?.let { this.startActivity(it) }
            }
        })

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        updateAppList(ALL)
    }
}