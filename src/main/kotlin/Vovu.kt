
sealed class Node {
    val top = ConnectionPoint()
    val right = ConnectionPoint()
    val bottom = ConnectionPoint()
    val left = ConnectionPoint()

    fun getPoint(direction: Direction) = when (direction) {
        Direction.TOP -> top
        Direction.RIGHT -> right
        Direction.BOTTOM -> bottom
        Direction.LEFT -> left
    }

    val connectionPoints = setOf(top, right, bottom, left)

    val connections
        get() = connectionPoints
            .map { it.connectedTo }
            .filterNotNull()

    val connectedNodes
        get() = connectionPoints
            .map { it.connectedTo }
            .filterNotNull()
            .flatMap { it.nodes }
            .filter { it != this }

    inner class ConnectionPoint(
        var connectedTo: Connection? = null
    ) {
        val node: Node
            get() = this@Node
    }
}

class PullTrigger : Node(), InteractionPoint {

    override fun tap() {
        connectedNodes.forEach { node ->
            if(node is PullPoint) {
                node.pull()
            }
        }
    }
}

class TurnTrigger : Node(), InteractionPoint {

    override fun tap() {
        connections.forEach { connection ->
            connection.nodes.forEach { node ->
                if (node is ShieldedTarget) {
                    node.turn(connection)
                }
            }
        }
    }
}

class ShieldedTarget(
    var openTowards: Direction
) : Node() {

    val openPoint get() = getPoint(openTowards)

    var isPulled: Boolean = false
        private set

    fun pull(from: Connection) {
        val pulledPoints = from.points.filter { it in connectionPoints }
        if (pulledPoints.isEmpty()) throw IllegalStateException()
        if (pulledPoints.any { it != openPoint }) throw PullBlockedException()
        this.isPulled = true
    }

    fun turn(from: Connection) {
        val activePoints = from.points.filter { it in connectionPoints }
        if (activePoints.isEmpty()) throw IllegalStateException()
        if (activePoints.none { it != openPoint }) return
        this.openTowards = this.openTowards.next()
    }
}

class PullPoint : Node() {

    fun pull() {
        connections.forEach { connection ->
            connection.nodes.forEach { node ->
                if(node is ShieldedTarget) {
                    node.pull(from = connection)
                }
            }
        }
    }

}

interface InteractionPoint {
    fun tap()
}

class Connection(vararg val points: Node.ConnectionPoint) {

    init {
        points.forEach { it.connectedTo = this }
    }
    val nodes = points.map { it.node }
}