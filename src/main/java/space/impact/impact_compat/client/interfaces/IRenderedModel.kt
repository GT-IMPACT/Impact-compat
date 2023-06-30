package space.impact.impact_compat.client.interfaces

import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import software.bernie.libs.vecmath.Vector3f

interface IRenderedModel {
    @SideOnly(Side.CLIENT) fun scaleItem(): Vector3f
}
