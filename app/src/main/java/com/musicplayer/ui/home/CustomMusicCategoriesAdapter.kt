package com.musicplayer.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.musicplayer.R
import com.musicplayer.listeners.IHome
import com.musicplayer.models.Music

class CustomMusicCategoriesAdapter(
    private val context: Context,
    private val listener: IHome
) : BaseExpandableListAdapter() {

    private var categoryTitles = mutableListOf<String>()
    private var musics = hashMapOf<String, List<Music>>()

    override fun getGroup(groupPosition: Int): String {
        return categoryTitles[groupPosition]
    }

    override fun getGroupCount(): Int {
        return categoryTitles.count()
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return this.musics[this.categoryTitles[groupPosition]]!!.count()
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Music {
        return this.musics[this.categoryTitles[groupPosition]]!![childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View? {
        var cv = convertView
        val groupTitle = getGroup(groupPosition)
        if (cv == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            cv = inflater.inflate(R.layout.layout_group, null)
        }

        val txtTitle = cv?.findViewById<TextView>(R.id.txtTitle)
        txtTitle?.text = groupTitle

        txtTitle?.setOnClickListener {
            listener.expandCollapseListView(groupPosition)
        }

        return cv
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var cv = convertView
        val music = getChild(groupPosition, childPosition)

        if (cv == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            cv = inflater.inflate(R.layout.layout_child, null)
        }

        val txtTitle = cv!!.findViewById<TextView>(R.id.txtTitle)
        txtTitle?.text = music.title

        val clMusic = cv.findViewById<ConstraintLayout>(R.id.clMusic)

        clMusic?.setOnClickListener {
            listener.onListViewItemClick(music)
        }

        return cv
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    fun submitHeaderList(list: MutableList<String>) {
        categoryTitles = list
        notifyDataSetChanged()
    }

    fun submitBodyList(list: HashMap<String, List<Music>>) {
        musics = list
        notifyDataSetChanged()
    }
}
