package space.impact.impact_compat.addon.gt.util.world;

import gregtech.api.interfaces.IHatchElement;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.util.IGT_HatchAdder;
import space.impact.impact_compat.addon.gt.base.multi.PrimitiveMultiBlockBase;
import space.impact.impact_compat.addon.gt.features.steam_age.machines.metallurgy.hatch.PrimitiveInputBus;
import space.impact.impact_compat.addon.gt.features.steam_age.machines.metallurgy.hatch.PrimitiveInputHatch;
import space.impact.impact_compat.addon.gt.features.steam_age.machines.metallurgy.hatch.PrimitiveOutputBus;
import space.impact.impact_compat.addon.gt.features.steam_age.machines.metallurgy.hatch.PrimitiveOutputHatch;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum PrimitiveHatchElement implements IHatchElement<PrimitiveMultiBlockBase<?>> {
	
	InputHatch(PrimitiveMultiBlockBase::addInputHatch, PrimitiveInputHatch.class) {
		@Override
		public long count(PrimitiveMultiBlockBase<?> t) {return t.inputHatches.size();}
	},
	OutputHatch(PrimitiveMultiBlockBase::addOutputHatch, PrimitiveOutputHatch.class) {
		@Override
		public long count(PrimitiveMultiBlockBase<?> t) {return t.outputHatches.size();}
	},
	InputBus(PrimitiveMultiBlockBase::addInputBus, PrimitiveInputBus.class) {
		@Override
		public long count(PrimitiveMultiBlockBase<?> t) {return t.inputBusses.size();}
	},
	OutputBus(PrimitiveMultiBlockBase::addOutputBus, PrimitiveOutputBus.class) {
		@Override
		public long count(PrimitiveMultiBlockBase<?> t) {return t.outputBusses.size();}
	},
	;
	private final List<Class<? extends IMetaTileEntity>> mteClasses;
	private final IGT_HatchAdder<PrimitiveMultiBlockBase<?>> adder;
	
	@SafeVarargs
	PrimitiveHatchElement(
			IGT_HatchAdder<PrimitiveMultiBlockBase<?>> adder,
			Class<? extends IMetaTileEntity>... mteClasses
	) {
		this.mteClasses = Collections.unmodifiableList(Arrays.asList(mteClasses));
		this.adder      = adder;
	}
	
	public List<? extends Class<? extends IMetaTileEntity>> mteClasses() {
		return mteClasses;
	}
	
	public IGT_HatchAdder<? super PrimitiveMultiBlockBase<?>> adder() {
		return adder;
	}
}
