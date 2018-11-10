import processing.core.*

class Applet : PApplet() {

    companion object {
        const val SCALE_FACTOR = 10.0F
        const val OFFSET_INC = 0.1F

        const val MIN_LIFE_SPAN = 1F
        const val MAX_LIFE_SPAN = 5F

        const val COLOR_WHITE = 255F
        const val FRAME_RATE = 60F
        const val STROKE_DEC_FACTOR = 0.001F
    }

    private lateinit var img: PImage

    lateinit var flowField: Array<PVector>
    lateinit var particles: Array<Particle>


    var weight = 50F

    private val rows: Int
        get() = Math.floor(height / SCALE_FACTOR.toDouble()).toInt()

    private val cols: Int
        get() = Math.floor(width / SCALE_FACTOR.toDouble()).toInt()

    override fun settings() {
        img = loadImage("assets/lake-original.jpg")
        size(img.width, img.height, PConstants.P2D)
    }

    override fun setup() {
        frameRate(FRAME_RATE)
        background(COLOR_WHITE)

        hint(DISABLE_DEPTH_MASK)

        particles = generateParticles()
        flowField = generateFlowField()
    }

    override fun draw() {
        particles.forEach(this@Applet::renderParticle)
    }

    private fun renderParticle(p: Particle) {
        if (p.life < 0) {
            p.reset(createVector(), life = getLifespan())
        }

        if (p.color == null) {
            p.color = img.get(p.position.x.toInt(), p.position.y.toInt())
        }

        applyFlowFieldToParticle(p, flowField)
        p.update()
        p.wrapEdgesToBounds(width.toFloat(), height.toFloat())
        matrixScope {
            fill(0)
            val color = p.color ?: 0
            stroke(red(color), green(color), blue(color), 100F)

            strokeWeight(weight)

            line(p.position.x, p.position.y, p.previousposition.x, p.previousposition.y)
            p.updatePrev()

            if (weight > 5F) {
                weight -= STROKE_DEC_FACTOR
            }
        }
    }

    private fun renderFields() {
        iter2(cols, rows) { x, y ->
            val index = x + y * cols
            matrixScope {
                stroke(0F, 50F)
                translate(x * SCALE_FACTOR, y * SCALE_FACTOR)
                rotate(flowField[index].heading())
                line(0F, 0F, SCALE_FACTOR, 0F)
            }
        }
    }

    private fun applyFlowFieldToParticle(particle: Particle, flowField: Array<PVector>) {
        val x = Math.floor((particle.position.x / SCALE_FACTOR).toDouble())
        val y = Math.floor((particle.position.y / SCALE_FACTOR).toDouble())

        val index = x + y * cols
        val force = flowField[index.toInt()]
        particle.applyForce(force)
    }

    private fun createVector(x: Float = random(width.toFloat()), y: Float = random(height.toFloat())) = PVector(x, y)

    private fun generateParticles(): Array<Particle> {
        return (0..100).map {
            Particle(
                    position = createVector(),
                    velocity = PVector.random2D(),
                    acceleration = createVector(0F, 0F),
                    life = getLifespan())
        }.toTypedArray()
    }

    var zOffset = 0F

    private fun generateFlowField(): Array<PVector> {
        val field = mutableListOf<PVector>()
        iter2(cols, rows) { x, y ->
            val noiseValue = noise(x * OFFSET_INC, y * OFFSET_INC, zOffset)
            val v = PVector.fromAngle((noiseValue * PConstants.TWO_PI).toFloat())
            v.setMag(6F)
            field.add(v)
        }
        zOffset += 0.0075F
        return field.toTypedArray()
    }

    private inline fun matrixScope(transaction: PApplet.() -> Unit) {
        this.pushMatrix()
        this.pushStyle()
        this.transaction()
        this.popMatrix()
        this.popStyle()
    }

    val getLifespan = { random(MIN_LIFE_SPAN, MAX_LIFE_SPAN).toInt() }
}
