package Dragon.models.npc;

import Dragon.models.player.Player;

public interface IAtionNpc {

    void openBaseMenu(Player player);

    void confirmMenu(Player player, int select);

}
