package Dragon.models.npc.instances.NpcList;

import Dragon.consts.ConstNpc;
import Dragon.models.npc.Npc;
import Dragon.models.npc.instances.NpcInstance;
import Dragon.models.player.Player;
import Dragon.services.Service;

public class ThorenNpcInstance extends NpcInstance {

    public ThorenNpcInstance(int mapId, int status, int cx, int cy, int tempId, int avatar) {
        super(mapId, status, cx, cy, tempId, avatar);
    }

    @Override
    public Npc createNpc() {
        return new Npc(mapId, status, cx, cy, tempId, avatar) {

            @Override
            public void openBaseMenu(Player player) {
                if (ThorenNpcInstance.this.canOpenNpc(player)) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "|7|Xin chào! Tôi là Thoren\nTôi có thể giúp gì cho bạn?",
                            "Xem thông tin", "Mua đồ", "Đóng");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (ThorenNpcInstance.this.canOpenNpc(player)) {
                    switch (select) {
                        case 0:
                            handleViewInfo(player);
                            break;
                        case 1:
                            handleShop(player);
                            break;
                        case 2:
                            // Đóng menu
                            break;
                    }
                }
            }
        };
    }

    private void handleViewInfo(Player player) {
        getNpc().createOtherMenu(player, ConstNpc.BASE_MENU,
                "Thông tin về Thoren:\n- NPC hỗ trợ\n- Có thể mua đồ\n- Luôn sẵn sàng giúp đỡ",
                "Quay lại", "Đóng");
    }

    private void handleShop(Player player) {
        Service.gI().sendThongBao(player, "Cửa hàng sẽ được mở sớm!");
    }

    @Override
    public boolean canOpenNpc(Player player) {
        return true;
    }
}
