/**
 *   This file is part of Skript.
 *
 *  Skript is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Skript is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 * Copyright 2011-2017 Peter Güttinger and contributors
 */
package ch.njol.skript.util;

import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.bukkitutil.block.BlockCompat;
import ch.njol.skript.bukkitutil.block.MagicBlockCompat;

/**
 * A block that gets all data from the world, but either delays
 * any changes by 1 tick of reflects them on a given BlockState
 * depending on which constructor is used.
 * 
 */
@NonNullByDefault(false)
public class DelayedChangeBlock implements Block {
	
	private static final boolean ISPASSABLE_METHOD_EXISTS = Skript.methodExists(Block.class, "isPassable");
	
	final Block b;
	@Nullable
	private final BlockState newState;
	private final boolean isPassable;
	
	public DelayedChangeBlock(final Block b) {
		this(b, null);
	}
	
	public DelayedChangeBlock(final Block b, final BlockState newState) {
		assert b != null;
		this.b = b;
		this.newState = newState;
		if (ISPASSABLE_METHOD_EXISTS && newState != null)
			this.isPassable = newState.getBlock().isPassable();
		else
			this.isPassable = false;
	}
	
	@Override
	public void setMetadata(final String metadataKey, final MetadataValue newMetadataValue) {
		b.setMetadata(metadataKey, newMetadataValue);
	}
	
	@Override
	public List<MetadataValue> getMetadata(final String metadataKey) {
		return b.getMetadata(metadataKey);
	}
	
	@Override
	public boolean hasMetadata(final String metadataKey) {
		return b.hasMetadata(metadataKey);
	}
	
	@Override
	public void removeMetadata(final String metadataKey, final Plugin owningPlugin) {
		b.removeMetadata(metadataKey, owningPlugin);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public byte getData() {
		return b.getData();
	}
	
	public void setData(byte data) throws Throwable {
		MagicBlockCompat.setDataMethod.invokeExact(b, data);
	}
	
	@Override
	public Block getRelative(final int modX, final int modY, final int modZ) {
		return b.getRelative(modX, modY, modZ);
	}
	
	@Override
	public Block getRelative(final BlockFace face) {
		return b.getRelative(face);
	}
	
	@Override
	public Block getRelative(final BlockFace face, final int distance) {
		return b.getRelative(face, distance);
	}
	
	@Override
	public Material getType() {
		return b.getType();
	}
	
	@Override
	public byte getLightLevel() {
		return b.getLightLevel();
	}
	
	@Override
	public byte getLightFromSky() {
		return b.getLightFromSky();
	}
	
	@Override
	public byte getLightFromBlocks() {
		return b.getLightFromBlocks();
	}
	
	@Override
	public World getWorld() {
		return b.getWorld();
	}
	
	@Override
	public int getX() {
		return b.getX();
	}
	
	@Override
	public int getY() {
		return b.getY();
	}
	
	@Override
	public int getZ() {
		return b.getZ();
	}
	
	@Override
	public Location getLocation() {
		return b.getLocation();
	}
	
	@Override
	public Chunk getChunk() {
		return b.getChunk();
	}
	
	@Override
	public void setType(final Material type) {
		if (newState != null) {
			newState.setType(type);
		} else {
			Bukkit.getScheduler().scheduleSyncDelayedTask(Skript.getInstance(), new Runnable() {
				@Override
				public void run() {
					b.setType(type);
				}
			});
		}
	}
	
	@Override
	public BlockFace getFace(final Block block) {
		return b.getFace(block);
	}
	
	@Override
	public BlockState getState() {
		return b.getState();
	}	

	@Override
	public BlockState getState(boolean useSnapshot) {
		return b.getState(useSnapshot);
	}
	
	@Override
	public Biome getBiome() {
		return b.getBiome();
	}
	
	@Override
	public void setBiome(final Biome bio) {
		b.setBiome(bio);
	}
	
	@Override
	public boolean isBlockPowered() {
		return b.isBlockPowered();
	}
	
	@Override
	public boolean isBlockIndirectlyPowered() {
		return b.isBlockIndirectlyPowered();
	}
	
	@Override
	public boolean isBlockFacePowered(final BlockFace face) {
		return b.isBlockFacePowered(face);
	}
	
	@Override
	public boolean isBlockFaceIndirectlyPowered(final BlockFace face) {
		return b.isBlockFaceIndirectlyPowered(face);
	}
	
	@Override
	public int getBlockPower(final BlockFace face) {
		return b.getBlockPower(face);
	}
	
	@Override
	public int getBlockPower() {
		return b.getBlockPower();
	}
	
	@Override
	public boolean isEmpty() {
		Material type = getType();
		assert type != null;
		return BlockCompat.INSTANCE.isEmpty(type);
	}
	
	@Override
	public boolean isLiquid() {
		Material type = getType();
		assert type != null;
		return BlockCompat.INSTANCE.isLiquid(type);
	}
	
	@Override
	public double getTemperature() {
		return b.getTemperature();
	}
	
	@Override
	public double getHumidity() {
		return b.getHumidity();
	}
	
	@Override
	public PistonMoveReaction getPistonMoveReaction() {
		return b.getPistonMoveReaction();
	}
	
	@Override
	public boolean breakNaturally() {
		if (newState != null) {
			return false;
		} else {
			Bukkit.getScheduler().scheduleSyncDelayedTask(Skript.getInstance(), new Runnable() {
				@Override
				public void run() {
					b.breakNaturally();
				}
			});
			return true;
		}
	}
	
	@Override
	public boolean breakNaturally(final ItemStack tool) {
		if (newState != null) {
			return false;
		} else {
			Bukkit.getScheduler().scheduleSyncDelayedTask(Skript.getInstance(), new Runnable() {
				@Override
				public void run() {
					b.breakNaturally(tool);
				}
			});
			return true;
		}
	}
	
	@Override
	public Collection<ItemStack> getDrops() {
		return b.getDrops();
	}
	
	@Override
	public Collection<ItemStack> getDrops(final ItemStack tool) {
		return b.getDrops(tool);
	}
	
	@Override
	public Location getLocation(final Location loc) {
		if (loc != null) {
			loc.setWorld(getWorld());
			loc.setX(getX());
			loc.setY(getY());
			loc.setZ(getZ());
			loc.setPitch(0);
			loc.setYaw(0);
		}
		return loc;
	}

	@Override
	public void setType(Material type, boolean applyPhysics) {
		if (newState != null) {
			newState.setType(type);
		} else {
			Bukkit.getScheduler().scheduleSyncDelayedTask(Skript.getInstance(), new Runnable() {
				@Override
				public void run() {
					b.setType(type, applyPhysics);
				}
			});
		}
	}

	@Override
	public BlockData getBlockData() {
		return b.getBlockData();
	}

	@Override
	public void setBlockData(BlockData data) {
		setBlockData(data, true);
	}

	@Override
	public void setBlockData(BlockData data, boolean applyPhysics) {
		if (newState != null) {
			newState.setBlockData(data);
		} else {
			b.setBlockData(data, applyPhysics);
		}
	}
	
	@Override
	public RayTraceResult rayTrace(Location start, Vector direction, double maxDistance, FluidCollisionMode fluidCollisionMode) {
		return b.rayTrace(start, direction, maxDistance, fluidCollisionMode);
	}
	
	@Override
	public boolean isPassable() {
		return isPassable;
	}

	@Override
	public BoundingBox getBoundingBox() {
		return b.getBoundingBox();
	}
}
