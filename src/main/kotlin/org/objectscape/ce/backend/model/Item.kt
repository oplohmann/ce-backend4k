package org.objectscape.ce.backend.model

import java.util.*

class Item : Model {

    companion object {
        val TableName = "items"
    }

    var text: String
    var note: String
    var entryDate : Date
    var lastChanged : Date

    constructor(id: Long, text: String, note: String = "", entryDate : Date, lastChanged : Date = entryDate) : super(id) {
        this.text = text
        this.note = note
        this.entryDate = entryDate
        this.lastChanged =lastChanged
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as Item

        if (text != other.text) return false
        if (note != other.note) return false
        if (entryDate != other.entryDate) return false
        if (lastChanged != other.lastChanged) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + text.hashCode()
        result = 31 * result + note.hashCode()
        result = 31 * result + entryDate.hashCode()
        result = 31 * result + lastChanged.hashCode()
        return result
    }


}