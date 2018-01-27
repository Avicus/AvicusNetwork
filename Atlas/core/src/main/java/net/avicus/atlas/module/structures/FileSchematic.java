package net.avicus.atlas.module.structures;

import com.google.common.base.Preconditions;
import com.sk89q.jnbt.ByteArrayTag;
import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.NBTInputStream;
import com.sk89q.jnbt.NamedTag;
import com.sk89q.jnbt.ShortTag;
import com.sk89q.jnbt.StringTag;
import com.sk89q.jnbt.Tag;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import lombok.ToString;

/**
 * Represents a schematic that is loaded from a file.
 * <p>
 * Original load method from {@link com.sk89q.worldedit.extent.clipboard.io.SchematicReader}. <3
 */
@ToString(callSuper = true)
public class FileSchematic extends Schematic {

  /**
   * Source schematic file.
   */
  private File source;

  /**
   * Constructor.
   *
   * @param source source file of the schematic
   */
  public FileSchematic(String id, File source) {
    super(id);
    this.source = source;
  }

  /**
   * Gets a required tag with type bounding from a NBT map.
   *
   * @param items map to getFirst the item from
   * @param key key of the item
   * @param expected expected class of the tag
   * @param <T> type of tag expected
   * @return The tag retrieved from the map
   * @throws IOException if the tag is not in the map.
   */
  private static <T extends Tag> T requireTag(Map<String, Tag> items, String key, Class<T> expected)
      throws IOException {
    if (!items.containsKey(key)) {
      throw new IOException("Schematic file is missing a \"" + key + "\" tag");
    }

    Tag tag = items.get(key);
    if (!expected.isInstance(tag)) {
      throw new IOException(key + " tag is not of tag type " + expected.getName());
    }

    return expected.cast(tag);
  }

  @Override
  void load() {
    loadFromFile(this.source);
  }

  /**
   * Populates block and data arrays from a NBT schematic file.
   *
   * @param source file to load from
   */
  private void loadFromFile(File source) {
    Preconditions.checkNotNull(source);

    try {
      FileInputStream stream = new FileInputStream(source);
      NBTInputStream nbtStream = new NBTInputStream(new GZIPInputStream(stream));

      // Schematic tag
      NamedTag rootTag = nbtStream.readNamedTag();
      if (!rootTag.getName().equals("Schematic")) {
        throw new IOException("Tag 'Schematic' does not exist or is not first");
      }
      CompoundTag schematicTag = (CompoundTag) rootTag.getTag();

      // Check
      Map<String, Tag> schematic = schematicTag.getValue();
      if (!schematic.containsKey("Blocks")) {
        throw new IOException("Schematic file is missing a 'Blocks' tag");
      }

      // Check type of Schematic
      String materials = requireTag(schematic, "Materials", StringTag.class).getValue();
      if (!materials.equals("Alpha")) {
        throw new IOException("Schematic file is not an Alpha schematic");
      }

      short width = requireTag(schematic, "Width", ShortTag.class).getValue();
      short height = requireTag(schematic, "Height", ShortTag.class).getValue();
      short length = requireTag(schematic, "Length", ShortTag.class).getValue();

      byte[] blockId = requireTag(schematic, "Blocks", ByteArrayTag.class).getValue();
      byte[] blockData = requireTag(schematic, "Data", ByteArrayTag.class).getValue();
      byte[] addId = new byte[0];
      short[] blocks = new short[blockId.length]; // Have to later combine IDs

      // We support 4096 block IDs using the same method as vanilla Minecraft, where
      // the highest 4 bits are stored in a separate byte array.
      if (schematic.containsKey("AddBlocks")) {
        addId = requireTag(schematic, "AddBlocks", ByteArrayTag.class).getValue();
      }

      // Combine the AddBlocks data with the first 8-bit block ID
      for (int index = 0; index < blockId.length; index++) {
        if ((index >> 1) >= addId.length) { // No corresponding AddBlocks index
          blocks[index] = (short) (blockId[index] & 0xFF);
        } else {
          if ((index & 1) == 0) {
            blocks[index] = (short) (((addId[index >> 1] & 0x0F) << 8) + (blockId[index] & 0xFF));
          } else {
            blocks[index] = (short) (((addId[index >> 1] & 0xF0) << 4) + (blockId[index] & 0xFF));
          }
        }
      }

      this.blocks = blocks;
      this.data = blockData;
      this.width = width;
      this.length = length;
      this.height = height;

    } catch (IOException e) {
      e.printStackTrace();
      this.blocks = new short[]{};
      this.data = new byte[]{};
      this.width = 0;
      this.length = 0;
      this.height = 0;
    }
  }
}
