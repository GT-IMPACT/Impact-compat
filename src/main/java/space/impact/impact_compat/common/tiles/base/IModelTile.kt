package space.impact.impact_compat.common.tiles.base

import net.minecraftforge.common.util.ForgeDirection

interface IModelTile {

    fun isActive(): Boolean
    fun setActive(isActive: Boolean)

    fun isClientSide(): Boolean
    fun isServerSide(): Boolean

    fun getFrontFacing(): ForgeDirection
    fun setFrontFacing(side: ForgeDirection)
    fun getBackFacing(): ForgeDirection

    fun isValidFacing(side: ForgeDirection): Boolean

    fun getValidFacings(): BooleanArray? {
        val validFacings = BooleanArray(6)
        for (facing in ForgeDirection.VALID_DIRECTIONS) {
            validFacings[facing.ordinal] = isValidFacing(facing)
        }
        return validFacings
    }
}