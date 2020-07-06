package org.objectscape.ce.backend.storage

import org.objectscape.ce.backend.model.Category
import org.objectscape.ce.backend.model.CategoryView
import org.objectscape.ce.backend.model.View
import java.sql.Connection
import java.sql.ResultSet

open class CategoryViewsStore(connection: Connection) : AbstractStore(connection) {

    override fun tableName() = "category_views"

    fun getCategoryViewsOrdered(viewId: Long): List<CategoryView> {
        val categoryViews = ArrayList<CategoryView>()
        val resultSet = executeQuery("select * from ${tableName()} where view_id = $viewId order by position")
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

    protected fun addRawCategoryView(categoryView : CategoryView): Boolean {
        return execute("insert into ${tableName()}(view_id, category_id, position) values(${categoryView.viewId}, ${categoryView.categoryId}, ${categoryView.position});")
    }

    fun updatePositions(changedCategoryViews: List<CategoryView>) {
        changedCategoryViews.forEach {
            execute("update ${tableName()} set position = ${it.position} where id = ${it.id};")
        }
    }

    fun addCategoryToView(view: View, newCategoryView: CategoryView, categoriesOfSameView: List<CategoryView>) {
        val changedCategoryViews = insertInOrder(newCategoryView, categoriesOfSameView)
        updatePositions(changedCategoryViews)
        addRawCategoryView(newCategoryView)
    }

    /**
     * Returns changed CategoryViews only
     */
    protected fun insertInOrder(cvNew: CategoryView, cvs: List<CategoryView>) : List<CategoryView> {
        val newCvs = ArrayList<CategoryView>()
        val updatedCvs = ArrayList<CategoryView>()
        if(cvs.isEmpty()) {
            cvNew.position = 0
            newCvs.add(cvNew)
            return newCvs
        }
        val cvsSorted = cvs.toSortedSet(compareBy { it.position })
        var added = false
        cvsSorted.forEach {
            if(it.position < cvNew.position) {
                newCvs.add(it)
                return@forEach
            }
            if(it.position == cvNew.position){
                newCvs.add(cvNew)
                added = true
                it.position++
                newCvs.add(it)
                updatedCvs.add(it)
                return@forEach
            }
            it.position++
            newCvs.add(it)
            updatedCvs.add(it)
        }

        if(!added) {
            if(cvNew.position == newCvs.last().position + 1) {
                newCvs.add(cvNew)
            } else {
                throw CategorySortException("Cannot insert categoryView $newCvs in correct order")
            }
        }

        return updatedCvs
    }

}