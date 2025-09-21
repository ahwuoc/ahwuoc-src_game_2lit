package Dragon.models.npc.instances.NpcList;

import Dragon.consts.ConstNpc;
import Dragon.models.npc.Npc;
import Dragon.models.player.Player;
import Dragon.models.npc.instances.NpcInstance;
import Dragon.services.func.CombineServiceNew;

public class LuyenDuocSuNpcInstance extends NpcInstance {

    public LuyenDuocSuNpcInstance(int mapId, int status, int cx, int cy, int tempId, int avatar) {
        super(mapId, status, cx, cy, tempId, avatar);
    }

    @Override
    public Npc createNpc() {
        return new Npc(mapId, status, cx, cy, tempId, avatar) {
            @Override
            public void openBaseMenu(Player player) {
                if (LuyenDuocSuNpcInstance.this.canOpenNpc(player)) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "|7|Xin chào! Tôi là Luyện Độc Sư\n"
                            + "Tôi chuyên về luyện đan Tu Tiên\n"
                            + "Bạn muốn làm gì?",
                            "Ghép Mảnh Tàn Đan", "Nâng Cấp Đan", "Đóng");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (LuyenDuocSuNpcInstance.this.canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0:
                                CombineServiceNew.gI().openTabCombine(player,
                                        CombineServiceNew.COMBINE_TAN_DAN_FRAGMENT, this);
                                break;
                            case 1:
                                CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.UPGRADE_TUTIEN_DAN,
                                        this);
                                break;
                            default:
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                        switch (player.combineNew.typeCombine) {
                            case CombineServiceNew.COMBINE_TAN_DAN_FRAGMENT:
                                if (select == 0) {
                                    CombineServiceNew.gI().startCombine(player, 0);
                                }
                                break;
                            case CombineServiceNew.UPGRADE_TUTIEN_DAN:
                                // Chỉ cần 1 nút "Nâng Cấp" - tự động nhận biết cấp hiện tại
                                if (select == 0) {
                                    CombineServiceNew.gI().startCombine(player, 0);
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        };
    }
}
