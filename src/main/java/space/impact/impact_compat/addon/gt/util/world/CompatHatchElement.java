package space.impact.impact_compat.addon.gt.util.world;

import gregtech.api.interfaces.IHatchElement;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.util.IGT_HatchAdder;
import space.impact.impact_compat.addon.gt.base.multi.KineticMultiBlockBase;
import space.impact.impact_compat.addon.gt.features.steam_age.machines.hatch.SteamRotorHatch;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum CompatHatchElement implements IHatchElement<KineticMultiBlockBase<?>> {
	
	RotorHatch(KineticMultiBlockBase::addKineticHatch, SteamRotorHatch.class) {
		@Override
		public long count(KineticMultiBlockBase<?> t) {return t.mInputHatches.size();}
	},
	
	;
	
	private final List<Class<? extends IMetaTileEntity>> mteClasses;
	private final IGT_HatchAdder<KineticMultiBlockBase<?>> adder;
	
	@SafeVarargs
	CompatHatchElement(
			IGT_HatchAdder<KineticMultiBlockBase<?>> adder,
			Class<? extends IMetaTileEntity>... mteClasses
	) {
		this.mteClasses = Collections.unmodifiableList(Arrays.asList(mteClasses));
		this.adder      = adder;
	}
	
	public List<? extends Class<? extends IMetaTileEntity>> mteClasses() {
		return mteClasses;
	}
	
	public IGT_HatchAdder<? super KineticMultiBlockBase<?>> adder() {
		return adder;
	}
}
