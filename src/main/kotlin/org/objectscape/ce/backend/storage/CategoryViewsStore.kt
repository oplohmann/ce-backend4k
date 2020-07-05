package org.objectscape.ce.backend.storage

import org.objectscape.ce.backend.model.Category
import org.objectscape.ce.backend.model.CategoryView
import java.sql.Connection
import java.sql.ResultSet

class CategoryViewsStore(connection: Connection) : AbstractStore(connection) {

    override fun tableName() = "category_views"

    fun getCategories(viewId: Long): List<CategoryView> {
        val categoryViews = ArrayList<CategoryView>()
        val resultSet = executeQuery("select * from ${tableName()} where id = ${viewId}")
        resultSet.use {
            while(resultSet.next()) {
                categoryViews.add(fromResultSet(resultSet))
            }
        }
        return categoryViews
    }

    private fun fromResultSet(resultSet: ResultSet): CategoryView {
        return CategoryView(
            resultSet.getLong(1),
            resultSet.getLong(2),
            resultSet.getLong(3),
            resultSet.getInt(4))
    }

    fun addCategoryView(categoryView : CategoryView): Boolean {
        return execute("insert into ${tableName()}(view_id, category_id, position) values(${categoryView.viewId}, ${categoryView.categoryId}, ${categoryView.position});")
    }

    fun addCategoryView(viewId: Long, categoryId: Long, position: Int) {
        execute("insert into ${tableName()}(view_id, category_id, position) values(${viewId}, ${categoryId}, ${position});")
    }

    fun updatePositions(changedCategoryViews: List<CategoryView>) {
        changedCategoryViews.forEach {
            execute("update ${tableName()} set position = ${it.position} where id = ${it.id});")
        }
    }

}