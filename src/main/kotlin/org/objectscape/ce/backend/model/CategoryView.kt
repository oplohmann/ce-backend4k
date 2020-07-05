package org.objectscape.ce.backend.model

class CategoryView  : Model {

    companion object {
        val TableName = "category_views"
    }

    val viewId: Long
    val categoryId: Long
    var position: Int

    constructor(id: Long, viewId: Long, categoryId: Long, position: Int) : super(id) {
        this.viewId = viewId
        this.categoryId = categoryId
        this.position = position;
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

    override fun toString(): String {
        return "CategoryView(id=$id, viewId=$viewId, categoryId=$categoryId, position=$position)"
    }


}