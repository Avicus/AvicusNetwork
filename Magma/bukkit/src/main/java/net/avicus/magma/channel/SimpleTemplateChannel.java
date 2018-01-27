package net.avicus.magma.channel;

import net.avicus.magma.text.template.ComponentTemplate;

/**
 * A simple template channel.
 */
public abstract class SimpleTemplateChannel extends SimpleChannel {

  /**
   * The template.
   */
  protected final ComponentTemplate template;

  /**
   * Construct a new simple template channel with no permissions required to send or read messages.
   *
   * @param id the channel id
   * @param template the template
   */
  protected SimpleTemplateChannel(String id, ComponentTemplate template) {
    super(id);
    this.template = template;
  }

  /**
   * Construct a new simple template channel with a single permission required to send or read
   * messages.
   *
   * @param id the channel id
   * @param template the template
   * @param permission the permission required to send and read messages
   */
  protected SimpleTemplateChannel(String id, ComponentTemplate template, String permission) {
    super(id, permission);
    this.template = template;
  }

  /**
   * Construct a new simple template channel with a permission required to send and read messages.
   *
   * @param id the channel id
   * @param template the template
   * @param sendPermission the permission required to send messages to this channel
   * @param readPermission the permission required to read messages sent to this channel
   */
  protected SimpleTemplateChannel(String id, ComponentTemplate template, String sendPermission,
      String readPermission) {
    super(id, sendPermission, readPermission);
    this.template = template;
  }
}
