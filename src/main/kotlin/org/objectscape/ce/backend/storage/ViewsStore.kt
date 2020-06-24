package org.objectscape.ce.backend.storage

import org.objectscape.ce.backend.model.Category
import org.objectscape.ce.backend.model.CategoryItem
import org.objectscape.ce.backend.model.Item
import org.objectscape.ce.backend.model.View
import java.sql.Connection

class ViewsStore(connection: Connection) : AbstractStore(connection) {

    fun addView(view: View) {
        execute("insert into ${tableName()}(name) values(${view.name};")
    }

    override fun tableName(): String = View.TableName

}