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
import android.graphics.drawable.Drawable

class GridAdapter(
    val ctx: MainActivity,
    var apps: ArrayList<App>,
) :
    ArrayAdapter<App>(ctx, R.layout.cell, apps) {



    val inflater: LayoutInflater =
        ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return apps.count()
    }

    override fun getItem(idx: Int): App {
        return apps.get(idx)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val item = apps.get(position)

        val row = inflater.inflate(R.layout.cell, null)
        row.layoutParams = LinearLayout.LayoutParams(GridView.AUTO_FIT, 200)

        var icon = row.findViewById(R.id.icon_fg) as ImageView
        
        icon.setImageDrawable(item.icon)

        return row
    }
}

