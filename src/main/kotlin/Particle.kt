import processing.core.PVector

data class Particle(
        var position: PVector,
        var velocity: PVector,
        var acceleration: PVector,
        var life: Int = 50,
        var color: Int? = null
) {

    var previousposition = position.copy()

    companion object {
        const val MAX_VELOCITY = 3F
    }

    fun update() {
        velocity.add(acceleration)
        velocity.limit(MAX_VELOCITY)
        position.add(velocity)
        acceleration.mult(0F)
        life--
    }

    fun reset(position: PVector, life: Int = 50) {
        this.position = position
        this.previousposition = position.copy()
        this.color = null
        this.life = life
    }

    fun applyForce(force: PVector) {
        acceleration.add(force)
    }

    fun wrapEdgesToBounds(width: Float, height: Float) {
        if (position.x > width) {
            position.x = 0F
            updatePrev()
        }
        if (position.x < 0F) {
            position.x = width;
            updatePrev()
        }

        if (position.y > height) {
            position.y = 0F
            updatePrev()
        }
        if (position.y < 0F) {
            position.y = height;
            updatePrev()
        }
    }

    fun updatePrev() {
        previousposition.x = position.x
        previousposition.y = position.y
    }
}
