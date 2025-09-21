package Dragon.models.npc.instances;

import Dragon.consts.ConstNpc;
import Dragon.models.npc.instances.NpcList.TutienNpcInstance;
import Dragon.models.npc.instances.NpcList.ThorenNpcInstance;
import Dragon.models.npc.instances.NpcList.LuyenDuocSuNpcInstance;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 * Auto-registration system cho NPC instances Sử dụng reflection để tự động tìm
 * và đăng ký các NPC instance classes
 */
public class NpcInstanceAutoRegistry {

    private static final Map<Integer, Class<? extends NpcInstance>> REGISTERED_INSTANCES = new HashMap<>();
    private static final Set<String> SCANNED_PACKAGES = new HashSet<>();

    static {
        SCANNED_PACKAGES.add("Dragon.models.npc.instances");
        SCANNED_PACKAGES.add("Dragon.models.npc.instances.NpcList");
    }

    public static void autoRegisterInstances() {
        System.out.println("=== AUTO REGISTERING NPC INSTANCES ===");

        registerInstance(ConstNpc.TUTIEN, TutienNpcInstance.class);
        registerInstance(ConstNpc.THOREN, ThorenNpcInstance.class);
        registerInstance(ConstNpc.LUYENDUOCSU, LuyenDuocSuNpcInstance.class);

        System.out.println("Registered NPC instances: " + REGISTERED_INSTANCES.keySet());
        System.out.println("Total instances: " + REGISTERED_INSTANCES.size());
        System.out.println("=====================================");
    }

    /**
     * Đăng ký một NPC instance
     */
    public static void registerInstance(int npcId, Class<? extends NpcInstance> instanceClass) {
        REGISTERED_INSTANCES.put(npcId, instanceClass);
        System.out.println("✓ Registered NPC " + npcId + " -> " + instanceClass.getSimpleName());
    }

    /**
     * Đăng ký một NPC instance (overload cho byte)
     */
    public static void registerInstance(byte npcId, Class<? extends NpcInstance> instanceClass) {
        registerInstance((int) npcId, instanceClass);
    }

    /**
     * Tạo NPC instance từ registered class
     */
    public static NpcInstance createInstance(int npcId, int mapId, int status, int cx, int cy, int tempId, int avatar) {
        Class<? extends NpcInstance> instanceClass = REGISTERED_INSTANCES.get(npcId);
        if (instanceClass == null) {
            return null;
        }

        try {
            Constructor<? extends NpcInstance> constructor = instanceClass.getConstructor(
                    int.class, int.class, int.class, int.class, int.class, int.class);
            return constructor.newInstance(mapId, status, cx, cy, tempId, avatar);
        } catch (Exception e) {
            System.err.println("Error creating NPC instance for ID " + npcId + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Kiểm tra xem NPC ID có được đăng ký không
     */
    public static boolean hasInstance(int npcId) {
        return REGISTERED_INSTANCES.containsKey(npcId);
    }

    /**
     * Lấy tất cả registered NPC IDs
     */
    public static Set<Integer> getRegisteredIds() {
        return REGISTERED_INSTANCES.keySet();
    }

    /**
     * Lấy class của NPC instance
     */
    public static Class<? extends NpcInstance> getInstanceClass(int npcId) {
        return REGISTERED_INSTANCES.get(npcId);
    }
}
