package org.objectscape.ce.backend.model

class CategoryParent : Model {

    companion object {
        val TableName = "category_parents"
    }

    val parentId: Long
    val categoryId: Long

    constructor(id: Long, parentId: Long,  categoryId: Long) : super(id) {
        this.parentId = parentId
        this.categoryId = categoryId
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as CategoryParent

        if (parentId != other.parentId) return false
        if (categoryId != other.categoryId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + parentId.hashCode()
        result = 31 * result + categoryId.hashCode()
        return result
    }

}