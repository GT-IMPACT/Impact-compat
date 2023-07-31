package space.impact.impact_compat.common.tiles

import gregtech.api.enums.GT_Values
import gregtech.api.interfaces.tileentity.IGregtechWailaProvider
import gregtech.api.net.GT_Packet_Block_Event
import mcp.mobius.waila.api.IWailaConfigHandler
import mcp.mobius.waila.api.IWailaDataAccessor
import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.Packet
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ChunkCoordinates
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection
import space.impact.impact_compat.common.network.Network.PacketChangeSide
import space.impact.impact_compat.common.network.Network.PacketStartBaseTile
import space.impact.impact_compat.core.WorldAround
import space.impact.impact_compat.core.WorldTick
import space.impact.impact_compat.core.WorldTick.of
import space.impact.packet_network.network.NetworkHandler.sendToAllAround
import space.impact.packet_network.network.NetworkHandler.sendToServer

abstract class BaseCompatTileEntity : TileEntity(), IGregtechWailaProvider, IModelTile {

    private var mFacing = ForgeDirection.DOWN
    private var oFacing: ForgeDirection = ForgeDirection.UNKNOWN

    private var isActive = false

    private var mBufferedTileEntities = arrayOfNulls<TileEntity>(6)
    protected var ticker: Long = 0
    protected var mNeedsUpdate: Boolean = true
    protected var mSendClientData: Boolean = false

    private var oTextureData = 0

    override fun isServerSide(): Boolean = if (worldObj == null) false else !worldObj.isRemote
    override fun isClientSide(): Boolean = worldObj.isRemote

    override fun isActive(): Boolean = isActive
    override fun setActive(isActive: Boolean) {
        this.isActive = isActive
        if (isServerSide()) sendToAllAround(PacketStartBaseTile.transaction(isActive), WorldAround.CHUNK_4)
        else sendToServer(PacketStartBaseTile.transaction(isActive))
    }

    override fun getFrontFacing(): ForgeDirection {
        return mFacing
    }

    override fun setFrontFacing(side: ForgeDirection) {
        if (isValidFacing(side)) {
            mFacing = side

        }
    }

    override fun getBackFacing(): ForgeDirection {
        return mFacing.opposite
    }

    override fun isValidFacing(side: ForgeDirection): Boolean {
        return side.ordinal >= 2 && side != ForgeDirection.UNKNOWN
    }

    override fun validate() {
        super.validate()
        ticker = 0
    }

    fun sendBlockEvent(aID: Byte, aValue: Byte) {
        when(aID.toInt()) {
            0 -> {
                mFacing = ForgeDirection.getOrientation(aValue.toInt())
            }
            else -> Unit
        }

    }

    override fun updateEntity() {
        ticker++

        if (isClientSide()) {
            if (mNeedsUpdate) {
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord)
                mNeedsUpdate = false
            }
        }
        if (isServerSide()) {
            if (ticker == 1L) onFirstTick()
            if (ticker of WorldTick.SECOND_HALF) sendClientData()
            if (ticker of WorldTick.SECOND_3)  issueClientUpdate() //TODO Костыль, надо пофиксить
            if (mFacing != oFacing) {
                oFacing = mFacing
                updateNeighbours()
            }
            onPostTick(ticker)
        }
    }

    private fun sendClientData() {
        if (mSendClientData) {
            sendToAllAround(PacketChangeSide.transaction(mFacing.ordinal), WorldAround.CHUNK_4)
            mSendClientData = false
        }
    }

    private fun issueClientUpdate() {
        mSendClientData = true
    }

    override fun getDescriptionPacket(): Packet? {
        issueClientUpdate()
        return null
    }

    private fun updateNeighbours() {
        val thisBlock: Block = worldObj.getBlock(xCoord, yCoord, zCoord)
        for (dir in ForgeDirection.VALID_DIRECTIONS) {
            val x1 = xCoord + dir.offsetX
            val y1 = yCoord + dir.offsetY
            val z1 = zCoord + dir.offsetZ
            if (worldObj.blockExists(x1, y1, z1)) {
                worldObj.notifyBlockOfNeighborChange(x1, y1, z1, thisBlock)
            }
        }
    }

    open fun onFirstTick() = Unit
    open fun onPostTick(tick: Long) = Unit

    override fun writeToNBT(data: NBTTagCompound) {
        super.writeToNBT(data)
        data.setInteger("mFacing", mFacing.ordinal)
        data.setBoolean("isActive", isActive)
    }

    override fun readFromNBT(data: NBTTagCompound) {
        super.readFromNBT(data)
        mFacing = ForgeDirection.getOrientation(data.getInteger("mFacing")).also { oFacing = it }
        isActive = data.getBoolean("isActive")
    }

    fun getTileEntity(aX: Int, aY: Int, aZ: Int): TileEntity? {
        return if (crossedChunkBorder(aX, aZ) && !worldObj.blockExists(aX, aY, aZ)) null
        else worldObj.getTileEntity(aX, aY, aZ)
    }

    fun getTileEntityOffset(aX: Int, aY: Int, aZ: Int): TileEntity? {
        return getTileEntity(xCoord + aX, yCoord + aY, zCoord + aZ)
    }

    fun getTileEntityAtSideAndDistance(side: ForgeDirection, aDistance: Int): TileEntity? {
        return if (aDistance == 1) getTileEntityAtSide(side) else getTileEntity(getOffsetX(side, aDistance), getOffsetY(side, aDistance).toInt(), getOffsetZ(side, aDistance))
    }

    fun getTileEntityAtSide(side: ForgeDirection): TileEntity? {
        val ordinalSide = side.ordinal
        if (side == ForgeDirection.UNKNOWN || mBufferedTileEntities[ordinalSide] == this) return null
        val tX = getOffsetX(side, 1)
        val tY = getOffsetY(side, 1).toInt()
        val tZ = getOffsetZ(side, 1)
        if (crossedChunkBorder(tX, tZ)) {
            mBufferedTileEntities[ordinalSide] = null
            if (!worldObj.blockExists(tX, tY, tZ)) return null
        }
        if (mBufferedTileEntities[ordinalSide] == null) {
            mBufferedTileEntities[ordinalSide] = worldObj.getTileEntity(tX, tY, tZ)
            if (mBufferedTileEntities[ordinalSide] == null) {
                mBufferedTileEntities[ordinalSide] = this
                return null
            }
            return mBufferedTileEntities[ordinalSide]
        }
        if (mBufferedTileEntities[ordinalSide]?.isInvalid() == true) {
            mBufferedTileEntities[ordinalSide] = null
            return getTileEntityAtSide(side)
        }
        return if (mBufferedTileEntities[ordinalSide]?.xCoord == tX && mBufferedTileEntities[ordinalSide]?.yCoord == tY && mBufferedTileEntities[ordinalSide]?.zCoord == tZ) {
            mBufferedTileEntities[ordinalSide]
        } else null
    }

    protected fun crossedChunkBorder(aX: Int, aZ: Int): Boolean {
        return aX shr 4 != xCoord shr 4 || aZ shr 4 != zCoord shr 4
    }

    fun crossedChunkBorder(aCoords: ChunkCoordinates): Boolean {
        return aCoords.posX shr 4 != xCoord shr 4 || aCoords.posZ shr 4 != zCoord shr 4
    }

    fun getOffsetX(side: ForgeDirection, aMultiplier: Int): Int {
        return xCoord + side.offsetX * aMultiplier
    }

    fun getOffsetY(side: ForgeDirection, aMultiplier: Int): Short {
        return (yCoord + side.offsetY * aMultiplier).toShort()
    }

    fun getOffsetZ(side: ForgeDirection, aMultiplier: Int): Int {
        return zCoord + side.offsetZ * aMultiplier
    }

    override fun getWailaBody(itemStack: ItemStack, currenttip: MutableList<String>, accessor: IWailaDataAccessor, config: IWailaConfigHandler) {
    }

    override fun getWailaNBTData(player: EntityPlayerMP, tile: TileEntity, tag: NBTTagCompound, world: World, x: Int, y: Int, z: Int) {
    }

    override fun getValidFacings(): BooleanArray? {
        val validFacings = BooleanArray(6)
        for (facing in ForgeDirection.VALID_DIRECTIONS) {
            validFacings[facing.ordinal] = isValidFacing(facing)
        }
        return validFacings
    }
}
