package space.impact.impact_compat.common.tiles.models

import software.bernie.geckolib3.core.PlayState
import software.bernie.geckolib3.core.builder.AnimationBuilder
import software.bernie.geckolib3.core.builder.ILoopType
import software.bernie.geckolib3.core.controller.AnimationController
import software.bernie.geckolib3.core.manager.AnimationData
import space.impact.impact_compat.common.tiles.base.BaseTileEntityModel

class SmelterContentModelTile : BaseTileEntityModel {

    companion object {
        private const val LAVA = "lava"
        private const val COAL = "coal"
    }

    constructor(name: String) : super(name)
    constructor() : super()

    override fun animation(data: AnimationData) {
        data.addAnimationController(AnimationController(this, CONTROLLER, TICK) {
            if (isActive()) {
                it.controller.setAnimation(AnimationBuilder().addAnimation(LAVA, ILoopType.EDefaultLoopTypes.LOOP))
                PlayState.CONTINUE
            } else {
                it.controller.setAnimation(AnimationBuilder().addAnimation(COAL, ILoopType.EDefaultLoopTypes.LOOP))
                PlayState.CONTINUE
            }
        })
    }
}
