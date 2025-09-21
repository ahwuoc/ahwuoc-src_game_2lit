package Dragon.models.npc.instances;

import Dragon.models.npc.Npc;
import Dragon.models.player.Player;

/**
 * Abstract base class for NPC instances. This provides a better maintainable
 * approach compared to static methods. Each NPC type should extend this class
 * and implement its specific behavior.
 */
public abstract class NpcInstance {

    protected final int mapId;
    protected final int status;
    protected final int cx;
    protected final int cy;
    protected final int tempId;
    protected final int avatar;

    protected Npc npc;

    /**
     * Constructor for NPC instance
     *
     * @param mapId Map ID where NPC is located
     * @param status NPC status
     * @param cx X coordinate
     * @param cy Y coordinate
     * @param tempId NPC template ID
     * @param avatar NPC avatar ID
     */
    public NpcInstance(int mapId, int status, int cx, int cy, int tempId, int avatar) {
        this.mapId = mapId;
        this.status = status;
        this.cx = cx;
        this.cy = cy;
        this.tempId = tempId;
        this.avatar = avatar;
    }

    /**
     * Creates and returns the NPC instance This method should be implemented by
     * subclasses to create specific NPC types
     *
     * @return The created NPC instance
     */
    public abstract Npc createNpc();

    /**
     * Gets the NPC instance
     *
     * @return The NPC instance
     */
    public Npc getNpc() {
        if (npc == null) {
            npc = createNpc();
        }
        return npc;
    }

    /**
     * Gets the map ID
     *
     * @return Map ID
     */
    public int getMapId() {
        return mapId;
    }

    /**
     * Gets the status
     *
     * @return Status
     */
    public int getStatus() {
        return status;
    }

    /**
     * Gets the X coordinate
     *
     * @return X coordinate
     */
    public int getCx() {
        return cx;
    }

    /**
     * Gets the Y coordinate
     *
     * @return Y coordinate
     */
    public int getCy() {
        return cy;
    }

    /**
     * Gets the template ID
     *
     * @return Template ID
     */
    public int getTempId() {
        return tempId;
    }

    /**
     * Gets the avatar ID
     *
     * @return Avatar ID
     */
    public int getAvatar() {
        return avatar;
    }

    /**
     * Checks if this NPC instance can be opened by the player Override this
     * method to add custom conditions
     *
     * @param player The player trying to open the NPC
     * @return true if the NPC can be opened, false otherwise
     */
    public boolean canOpenNpc(Player player) {
        return true;
    }

    /**
     * Called when the NPC is opened by a player Override this method to add
     * custom behavior
     *
     * @param player The player opening the NPC
     */
    public void onNpcOpened(Player player) {
        // Default implementation - can be overridden
    }

    /**
     * Called when a menu option is selected Override this method to handle menu
     * selections
     *
     * @param player The player selecting the option
     * @param select The selected option index
     */
    public void onMenuSelected(Player player, int select) {
        // Default implementation - can be overridden
    }
}
