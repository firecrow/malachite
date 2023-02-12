package net.firesilver.sandbox

import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.ImageView
import android.widget.GridView
import android.widget.LinearLayout
import android.content.pm.ApplicationInfo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Context
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.ScaleDrawable
import android.graphics.drawable.Drawable
import android.graphics.Color
import android.graphics.Color.*

class GridAdapter(
    val ctx: MainActivity,
    var apps: ArrayList<ApplicationInfo>,
) :
    ArrayAdapter<ApplicationInfo>(ctx, R.layout.cell, apps) {

    fun asAdaptive(icon: Drawable): AdaptiveIconDrawable{
        var defaultBackground = ColorDrawable(Color.WHITE)

        if(icon !is AdaptiveIconDrawable){
            return AdaptiveIconDrawable(defaultBackground, icon)
        }
        return icon
    }


    val inflater: LayoutInflater =
        ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return apps.count()
    }

    override fun getItem(idx: Int): ApplicationInfo {
        return apps.get(idx)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val item = apps.get(position)

        val row = inflater.inflate(R.layout.cell, null)
        row.layoutParams = LinearLayout.LayoutParams(GridView.AUTO_FIT, 200)

        val pm = ctx.getPackageManager()
        var icon = row.findViewById(R.id.icon_fg) as ImageView
        var iconDrawable = asAdaptive(pm.getApplicationIcon(item))
        
        icon.setImageDrawable(iconDrawable.foreground)

        return row
    }
}

