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

abstract class BaseTileEntityModel : TileEntity(), IAnimatable, ITileModel {

    companion object {
        fun registerTEModel(clazz: Class<out TileEntity>, tileId: String) {
            GameRegistry.registerTileEntity(clazz, tileId)
        }
        private const val NBT_ACTIVE = "NBT_ACTIVE"
        const val CONTROLLER = "controller"
        const val TICK = 0f
    }

    var isAnimated: Boolean = false

    private val factory = GeckoLibUtil.createFactory(this)
    override fun getFactory(): AnimationFactory = factory
    override fun canUpdate() = false

    override fun registerControllers(data: AnimationData) {
        data.addAnimationController(AnimationController(this, CONTROLLER, TICK) {
            it.controller.setAnimation(AnimationBuilder().addAnimation(animateName, ILoopType.EDefaultLoopTypes.LOOP))
            if (!it.controller.isJustStarting) {
                if (isAnimated) PlayState.CONTINUE else PlayState.STOP
            } else {
                PlayState.CONTINUE
            }
        })
    }

    override fun writeToNBT(data: NBTTagCompound) {
        super.writeToNBT(data)
        data.setBoolean(NBT_ACTIVE, isAnimated)
    }

    override fun readFromNBT(data: NBTTagCompound) {
        super.readFromNBT(data)
        isAnimated = data.getBoolean(NBT_ACTIVE)
    }
}

interface ITileModel {
    val animateName: String
}
