package org.sourcei.xpns.database.room.models

import android.app.Activity
import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.sourcei.xpns.database.room.dao.CategoryDao
import org.sourcei.xpns.database.room.objects.CategoryObject
import org.sourcei.xpns.database.realtime.objects.IconPojo
import org.sourcei.xpns.database.room.RoomSource
import org.sourcei.xpns.utils.others.Config
import java.util.*

/**
 * @info -
 *
 * @author - Saksham
 * @tnote Last Branch Update - master
 *
 * @tnote Created on 2018-08-17 by Saksham
 * @tnote Updates :
 *  Saksham - 2018 08 17 - master - adding lifecycle
 */
class CategoryModel(private val lifecycle: Lifecycle, private val context: Context) : ViewModel() {
    private lateinit var activity: Activity

    init {
        activity = context as Activity
    }

    // dao instance
    private fun dao(): CategoryDao {
        return RoomSource
            .getInstance(context, Config.DbName)
            .categoryDao()
    }

    // insert a new category
    fun insert(
        name: String,
        icon: IconPojo,
        type: String,
        color: String,
        parent: String? = null,
        isParent: Boolean = true,
        isChild: Boolean = false,
        uuid: String = UUID.randomUUID().toString()
    ) {
        GlobalScope.launch {
            dao().insert(
                CategoryObject(
                    0,
                    uuid,
                    name,
                    isParent,
                    isChild,
                    parent,
                    null,
                    icon,
                    0,
                    type,
                    color,
                    false
                )
            )
        }
    }

    // fetching a single item
    fun getItem(id: String, callback: (CategoryObject?) -> Unit) {
        GlobalScope.launch {
            val item = dao().getItem(id)
            lifecycle.addObserver(object : LifecycleObserver {
                var once = true
                @OnLifecycleEvent(Lifecycle.Event.ON_START)
                fun onStart() {
                    if (once) {
                        once = false
                        activity.runOnUiThread {
                            callback(item)
                        }
                    }
                }
            })
        }
    }

    // get all items paginated
    fun getItems(type: String, callback: (List<CategoryObject>) -> Unit) {
        GlobalScope.launch {
            val items = dao().getItems(type)
            lifecycle.addObserver(object : LifecycleObserver {
                var once = true
                @OnLifecycleEvent(Lifecycle.Event.ON_START)
                fun onStart() {
                    if (once) {
                        once = false
                        activity.runOnUiThread {
                            callback(items)
                        }
                    }
                }
            })
        }
    }

    // get frequent items paginated
    fun getFrequentItems(type: String, callback: (List<CategoryObject>) -> Unit) {
        GlobalScope.launch {
            val items = dao().getFrequentItems(type)
            lifecycle.addObserver(object : LifecycleObserver {
                var once = true
                @OnLifecycleEvent(Lifecycle.Event.ON_START)
                fun onStart() {
                    if (once) {
                        once = false
                        activity.runOnUiThread {
                            callback(items)
                        }
                    }
                }
            })
        }
    }

    //update an item
    fun editItem(categoryObject: CategoryObject) {
        GlobalScope.launch { dao().insert(categoryObject) }
    }

    //delete an item
    fun deleteItem(categoryObject: CategoryObject) {
        GlobalScope.launch { dao().deleteItem(categoryObject) }
    }

    //delete an item
    fun deleteItem(id: String) {
        GlobalScope.launch { dao().deleteItem(id) }
    }
}