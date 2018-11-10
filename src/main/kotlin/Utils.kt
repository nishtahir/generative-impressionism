inline fun iter2(width: Int, height: Int, transform: (Int, Int) -> Unit) {
    for (x in 0..width) {
        for (y in 0..height) {
            transform(x, y)
        }
    }
}

inline fun iter2WithIndex(width: Int, height: Int, transform: (x: Int, y: Int, index: Int) -> Unit) {
    var index = 0
    for (x in 0..width) {
        for (y in 0..height) {
            transform(x, y, index++)
        }
    }
}

