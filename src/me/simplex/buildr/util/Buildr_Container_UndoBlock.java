package me.simplex.buildr.util;

import org.bukkit.Material;

public class Buildr_Container_UndoBlock {
	private Material material;
	private byte materialData;
	
	public Buildr_Container_UndoBlock(Material material, byte materialData) {
		this.material = material;
		this.materialData = materialData;
	}

	public Material getMaterial() {
		return material;
	}

	public byte getMaterialData() {
		return materialData;
	}
	
	
}
