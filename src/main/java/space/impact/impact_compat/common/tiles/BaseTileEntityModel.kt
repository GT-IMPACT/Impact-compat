package space.impact.impact_compat.common.tiles

import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import software.bernie.geckolib3.core.IAnimatable
import software.bernie.geckolib3.core.PlayState
import software.bernie.geckolib3.core.builder.AnimationBuilder
import software.bernie.geckolib3.core.builder.ILoopType
import software.bernie.geckolib3.core.controller.AnimationController
import software.bernie.geckolib3.core.manager.AnimationData
import software.bernie.geckolib3.core.manager.AnimationFactory
import software.bernie.geckolib3.util.GeckoLibUtil
import space.impact.impact_compat.core.NBT

abstract class BaseTileEntityModel : BaseCompatTileEntity(), IAnimatable {

    companion object {
        fun registerTEModel(clazz: Class<out TileEntity>, tileId: String) {
            GameRegistry.registerTileEntity(clazz, tileId)
        }

        const val CONTROLLER = "controller"
        const val TICK = 0f
        private const val WORK = "work"
        private const val START = "start"
        private const val STOP = "stop"
        private const val IDLE = "idle"
    }

    var isFirst: Boolean = true
    var isAnimated: Boolean = false
    var isWork: Boolean = false


    private val factory = GeckoLibUtil.createFactory(this)
    override fun getFactory(): AnimationFactory = factory
    override fun canUpdate() = false

    override fun registerControllers(data: AnimationData) {
        data.addAnimationController(AnimationController(this, CONTROLLER, TICK) {
            if (isAnimated) {
                it.controller.setAnimation(
                    AnimationBuilder().addAnimation(WORK, ILoopType.EDefaultLoopTypes.LOOP),
                )
                PlayState.CONTINUE
            } else {
                val builder = AnimationBuilder()
                if (!isFirst) builder.addAnimation(STOP, ILoopType.EDefaultLoopTypes.PLAY_ONCE)
                it.controller.setAnimation(
                    builder.addAnimation(IDLE, ILoopType.EDefaultLoopTypes.LOOP)
                )
                PlayState.CONTINUE
            }
        })
    }

    override fun writeToNBT(data: NBTTagCompound) {
        super.writeToNBT(data)
        data.setBoolean(NBT.NBT_ACTIVE, isAnimated)
    }

    override fun readFromNBT(data: NBTTagCompound) {
        super.readFromNBT(data)
        isAnimated = data.getBoolean(NBT.NBT_ACTIVE)
    }
}
