package org.objectscape.ce.backend.storage

import java.lang.RuntimeException

class DatabaseException(message: String?) : RuntimeException(message) {
}