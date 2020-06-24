package org.objectscape.ce.backend.model

class CategoryItem : Model {

    companion object {
        val TableName = "category_items"
    }

    val categoryId: Long
    val itemId: Long

    constructor(id: Long, categoryId: Long,  itemId: Long) : super(id) {
        this.itemId = itemId
        this.categoryId = categoryId
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as CategoryItem

        if (categoryId != other.categoryId) return false
        if (itemId != other.itemId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + categoryId.hashCode()
        result = 31 * result + itemId.hashCode()
        return result
    }


}