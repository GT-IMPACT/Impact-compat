package space.impact.impact_compat.common.tiles

import software.bernie.geckolib3.core.PlayState
import software.bernie.geckolib3.core.builder.AnimationBuilder
import software.bernie.geckolib3.core.builder.ILoopType
import software.bernie.geckolib3.core.controller.AnimationController
import software.bernie.geckolib3.core.manager.AnimationData

@Suppress("unused")
abstract class BaseTileRotationEntityModel : BaseTileEntityModel() {

    companion object {
        private const val WORK_R = "work.right"
        private const val STOP_R = "stop.right"
        private const val IDLE_R = "idle.right"

        private const val WORK_L = "work.left"
        private const val STOP_L = "stop.left"
        private const val IDLE_L = "idle.left"
    }

    override fun registerControllers(data: AnimationData) {
        data.addAnimationController(AnimationController(this, CONTROLLER, TICK) {
            if (isActive()) {
                it.controller.setAnimation(AnimationBuilder().addAnimation(WORK_L, ILoopType.EDefaultLoopTypes.LOOP))
                PlayState.CONTINUE
            } else {
                val builder = AnimationBuilder()
//                if (!isFirst) builder.addAnimation(STOP_L, ILoopType.EDefaultLoopTypes.PLAY_ONCE)
                it.controller.setAnimation(builder.addAnimation(IDLE_L, ILoopType.EDefaultLoopTypes.LOOP))
                PlayState.CONTINUE
            }
        })
    }
}
