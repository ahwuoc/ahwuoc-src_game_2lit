package Dragon.models.npc.instances.NpcList;

import Dragon.models.npc.Npc;
import Dragon.models.npc.instances.NpcInstance;
import Dragon.models.player.Player;
import Dragon.services.tutien.TutienService;

public class TutienNpcInstance extends NpcInstance {

    public TutienNpcInstance(int mapId, int status, int cx, int cy, int tempId, int avatar) {
        super(mapId, status, cx, cy, tempId, avatar);
    }

    @Override
    public Npc createNpc() {
        return new Npc(mapId, status, cx, cy, tempId, avatar) {

            @Override
            public void openBaseMenu(Player player) {
                if (TutienNpcInstance.this.canOpenNpc(player)) {
                    TutienService.gI().showMainMenu(this, player);
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (TutienNpcInstance.this.canOpenNpc(player)) {
                    TutienService.gI().handleMenuSelection(this, player, select);
                }
            }
        };

    }

    @Override
    public boolean canOpenNpc(Player player) {
        return true;
    }

    @Override
    public void onNpcOpened(Player player) {
        super.onNpcOpened(player);
    }

    @Override
    public void onMenuSelected(Player player, int select) {
        super.onMenuSelected(player, select);
    }
}
