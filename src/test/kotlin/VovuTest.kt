import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

class VovuTest {

    @get:Rule
    val thrown = ExpectedException.none()

    @Test
    fun `throws on pulling from wrong direction`() {
        val trigger = PullTrigger()
        val target = ShieldedTarget(openTowards = Direction.LEFT)
        val pullPoint = PullPoint()
        Connection(trigger.top, pullPoint.bottom)
        Connection(pullPoint.top, target.bottom)

        thrown.expect(PullBlockedException::class.java)
        trigger.tap()
    }

    @Test
    fun `level 1`() {
        val trigger = PullTrigger()
        val target = ShieldedTarget(openTowards = Direction.BOTTOM)
        val pullPoint = PullPoint()
        Connection(trigger.top, pullPoint.bottom)
        Connection(pullPoint.top, target.bottom)

        trigger.tap()

        assert(target.isPulled)
    }

    @Test
    fun `level 2`() {
        val turnTrigger = TurnTrigger()
        val target = ShieldedTarget(openTowards = Direction.LEFT)
        val pullPoint = PullPoint()
        val pullTrigger = PullTrigger()
        Connection(turnTrigger.bottom, target.top)
        Connection(pullTrigger.top, pullPoint.bottom)
        Connection(pullPoint.top, target.bottom)

        turnTrigger.tap()
        pullTrigger.tap()

        assert(target.openTowards == Direction.BOTTOM)
        assert(target.isPulled)
    }

    @Test
    fun `level 3`() {
        val target = ShieldedTarget(openTowards = Direction.RIGHT)
        val pullPoint = PullPoint()
        val pullTrigger = PullTrigger()
        val turnTrigger = TurnTrigger()
        Connection(pullTrigger.top, pullPoint.left)
        Connection(target.left, pullPoint.right)
        Connection(target.bottom, turnTrigger.top)

        turnTrigger.tap()
        turnTrigger.tap()
        pullTrigger.tap()

        assert(target.openTowards == Direction.LEFT)
        assert(target.isPulled)
    }

    @Test
    fun `level 4`() {
        val turnTrigger = TurnTrigger()
        val targetLeft = ShieldedTarget(openTowards = Direction.LEFT)
        val targetRight = ShieldedTarget(openTowards = Direction.LEFT)
        val pullPointLeft = PullPoint()
        val pullPointRight = PullPoint()
        val pullTrigger = PullTrigger()
        Connection(pullTrigger.top, pullPointLeft.right, pullPointRight.left)
        Connection(pullPointLeft.top, targetLeft.bottom)
        Connection(pullPointRight.top, targetRight.bottom)
        Connection(turnTrigger.bottom, targetLeft.top, targetRight.top)

        turnTrigger.tap()
        pullTrigger.tap()

        assert(targetLeft.openTowards == Direction.BOTTOM)
        assert(targetRight.openTowards == Direction.BOTTOM)
        assert(targetLeft.isPulled)
        assert(targetRight.isPulled)
    }

    @Test
    fun `level 5`() {
        val turnTrigger = TurnTrigger()
        val targetTop = ShieldedTarget(openTowards = Direction.TOP)
        val targetBottom = ShieldedTarget(openTowards = Direction.BOTTOM)
        val pullPointTop = PullPoint()
        val pullPointBottom = PullPoint()
        val pullTriggerLeft = PullTrigger()
        val pullTriggerRight = PullTrigger()

        Connection(pullTriggerLeft.top, pullPointTop.left)
        Connection(pullTriggerRight.top, pullPointBottom.right)
        Connection(pullPointTop.right, targetTop.left)
        Connection(pullPointBottom.left, targetBottom.right)
        Connection(turnTrigger.top, targetTop.bottom)
        Connection(turnTrigger.bottom, targetBottom.top)

        turnTrigger.tap()
        pullTriggerLeft.tap()
        pullTriggerRight.tap()

        assert(targetTop.openTowards == Direction.LEFT)
        assert(targetBottom.openTowards == Direction.RIGHT)
        assert(targetTop.isPulled)
        assert(targetBottom.isPulled)
    }

}