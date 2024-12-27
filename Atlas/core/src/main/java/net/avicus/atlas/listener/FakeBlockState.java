package net.avicus.atlas.listener;

import java.util.List;
import java.util.Optional;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;


/**
 * A super simple wrapper class for {@link BlockState} that allows manipulation of data.
 *
 * This is purely to represent what a block is going to be after an event finishes firing and
 * should not be used unless absolutely necessary. As such, any update or metadata methods will
 * throw an {@link UnsupportedOperationException}.
 */
public class FakeBlockState implements BlockState {

  private final BlockState parent;

  private Optional<MaterialData> data = Optional.empty();
  private Optional<Material> type = Optional.empty();

  public FakeBlockState(BlockState parent) {
    this.parent = parent;
  }

  @Override
  public Block getBlock() {
    return parent.getBlock();
  }

  @Override
  public Material getMaterial() {
    return this.type.orElse(this.parent.getMaterial());
  }

  @Override
  public MaterialData getMaterialData() {
    return this.data.orElse(this.parent.getMaterialData());
  }

  @Override
  public void setMaterial(Material material) {
    this.type = Optional.ofNullable(material);
  }

  @Override
  public void setMaterialData(MaterialData materialData) {
    this.data = Optional.ofNullable(materialData);
  }

  @Override
  public MaterialData getData() {
    return this.data.orElse(this.parent.getData());
  }

  @Override
  public void setData(MaterialData materialData) {
    this.data = Optional.ofNullable(materialData);
  }

  @Override
  public Material getType() {
    return this.type.orElse(this.parent.getType());
  }

  @Override
  public void setType(Material material) {
    this.type = Optional.ofNullable(material);
  }

  @Override
  public int getTypeId() {
    return getType().getId();
  }

  @Override
  public byte getLightLevel() {
    return this.parent.getLightLevel();
  }

  @Override
  public World getWorld() {
    return this.parent.getWorld();
  }

  @Override
  public int getX() {
    return this.parent.getX();
  }

  @Override
  public int getY() {
    return this.parent.getY();
  }

  @Override
  public int getZ() {
    return this.parent.getZ();
  }

  @Override
  public Location getLocation() {
    return this.parent.getLocation();
  }

  @Override
  public Location getLocation(Location location) {
    return this.parent.getLocation(location);
  }

  @Override
  public Chunk getChunk() {
    return this.parent.getChunk();
  }

  @Override
  public boolean setTypeId(int i) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean update() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean update(boolean b) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean update(boolean b, boolean b1) {
    throw new UnsupportedOperationException();
  }

  @Override
  public byte getRawData() {
    return this.getData().getData();
  }

  @Override
  public void setRawData(byte b) {
    this.getData().setData(b);
  }

  @Override
  public boolean isPlaced() {
    return this.parent.isPlaced();
  }

  @Override
  public void setMetadata(String s, MetadataValue metadataValue) {
    throw new UnsupportedOperationException();
  }

  @Override
  public MetadataValue getMetadata(String s, Plugin plugin) {
    return null;
  }

  @Override
  public List<MetadataValue> getMetadata(String s) {
    return this.parent.getMetadata(s);
  }

  @Override
  public boolean hasMetadata(String s) {
    return this.parent.hasMetadata(s);
  }

  @Override
  public void removeMetadata(String s, Plugin plugin) {
    throw new UnsupportedOperationException();
  }
}
