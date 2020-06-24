package org.objectscape.ce.backend.model

class CategoryView  : Model {

    companion object {
        val TableName = "category_views"
    }

    val viewId: Long
    val categoryId: Long

    constructor(id: Long, viewId: Long, categoryId: Long) : super(id) {
        this.viewId = viewId
        this.categoryId = categoryId
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as CategoryView

        if (viewId != other.viewId) return false
        if (categoryId != other.categoryId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + viewId.hashCode()
        result = 31 * result + categoryId.hashCode()
        return result
    }

}