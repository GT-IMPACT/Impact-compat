package space.impact.impact_compat.addon.gt.util.world

import gregtech.api.enums.ParticleFX
import gregtech.api.interfaces.tileentity.IGregTechTileEntity
import gregtech.api.objects.XSTR
import gregtech.api.util.WorldSpawnedEventBuilder
import net.minecraftforge.common.util.ForgeDirection

object GTParticles {

    fun createSparklesMainFace(te: IGregTechTileEntity, fx: ParticleFX) {
        if (te.isActive && XSTR.XSTR_INSTANCE.nextInt(3) == 0) {
            val mainFacing = te.frontFacing
            if (mainFacing.flag and (ForgeDirection.UP.flag or ForgeDirection.DOWN.flag) == 0 && te.getCoverIDAtSide(mainFacing) == 0 && !te.getOpacityAtSide(mainFacing)) {
                val oX = te.xCoord.toDouble()
                val oY = te.yCoord.toDouble()
                val oZ = te.zCoord.toDouble()
                val offset = 0.02
                val horizontal = 0.5 + XSTR.XSTR_INSTANCE.nextFloat() * 8.0 / 16.0 - 4.0 / 16.0
                val x: Double
                val z: Double
                val mX: Double
                val mZ: Double
                val y: Double = oY + XSTR.XSTR_INSTANCE.nextFloat() * 10.0 / 16.0 + 5.0 / 16.0
                when (mainFacing) {
                    ForgeDirection.WEST -> {
                        x = oX - offset
                        mX = -.05
                        z = oZ + horizontal
                        mZ = 0.0
                    }
                    ForgeDirection.EAST -> {
                        x = oX + offset
                        mX = .05
                        z = oZ + horizontal
                        mZ = 0.0
                    }
                    ForgeDirection.NORTH -> {
                        x = oX + horizontal
                        mX = 0.0
                        z = oZ - offset
                        mZ = -.05
                    }
                    else -> {
                        x = oX + horizontal
                        mX = 0.0
                        z = oZ + offset
                        mZ = .05
                    }
                }
                val particleEventBuilder = WorldSpawnedEventBuilder
                    .ParticleEventBuilder()
                    .setMotion(mX, 0.0, mZ)
                    .setPosition(x, y, z)
                    .setWorld(te.world)
                particleEventBuilder.setIdentifier(fx).run()
            }
        }
    }

}