/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Ordinastie
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package net.malisis.doors.tileentity;

import java.util.Map;

import net.malisis.core.MalisisCore;
import net.malisis.core.util.SafeGet;
import net.malisis.doors.block.Door;
import net.malisis.doors.item.CustomDoorItem;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.apache.commons.lang3.tuple.Triple;

import com.google.common.collect.Maps;

/**
 * @author Ordinastie
 *
 */
public class CustomDoorTileEntity extends DoorTileEntity
{
	private IBlockState frame = Blocks.planks.getDefaultState();
	private IBlockState top = Blocks.glass.getDefaultState();
	private IBlockState bottom = Blocks.glass.getDefaultState();

	private Map<IBlockState, SafeGet<Integer>> safeMap = Maps.newHashMap();

	public CustomDoorTileEntity()
	{
		buildSafeMap();
	}

	private void buildSafeMap()
	{
		if (!MalisisCore.isClient())
			return;

		safeMap.clear();
		safeMap.put(frame, new SafeGet<>(() -> frame.getBlock().colorMultiplier(worldObj, pos), frame.getBlock().getBlockColor()));
		safeMap.put(top, new SafeGet<>(() -> top.getBlock().colorMultiplier(worldObj, pos), top.getBlock().getBlockColor()));
		safeMap.put(bottom, new SafeGet<>(() -> bottom.getBlock().colorMultiplier(worldObj, pos), bottom.getBlock().getBlockColor()));
	}

	//#region Getters/setters
	public IBlockState getFrame()
	{
		return frame;
	}

	public IBlockState getTop()
	{
		return top;
	}

	public IBlockState getBottom()
	{
		return bottom;
	}

	public int getLightValue()
	{
		if (frame == null || top == null || bottom == null)
			return 0;
		return Math.max(Math.max(frame.getBlock().getLightValue(), top.getBlock().getLightValue()), bottom.getBlock().getLightValue());
	}

	public int getColor(IBlockState state)
	{
		return safeMap.containsKey(state) ? safeMap.get(state).get() : 0xFFFFFF;
	}

	//#end Getters/setters

	@Override
	public void onBlockPlaced(Door door, ItemStack itemStack)
	{
		super.onBlockPlaced(door, itemStack);

		Triple<IBlockState, IBlockState, IBlockState> triple = CustomDoorItem.readNBT(itemStack.getTagCompound());
		frame = triple.getLeft();
		top = triple.getMiddle();
		bottom = triple.getRight();

		setCentered(shouldCenter());

		buildSafeMap();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);

		Triple<IBlockState, IBlockState, IBlockState> triple = CustomDoorItem.readNBT(nbt);
		frame = triple.getLeft();
		top = triple.getMiddle();
		bottom = triple.getRight();

		buildSafeMap();
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		CustomDoorItem.writeNBT(nbt, frame, top, bottom);
	}
}
