package Dragon.models.player;

import Dragon.consts.ConstNpc;
import Dragon.models.npc.Npc;
import Dragon.models.shop.Shop;

public class IDMark {

    private int idItemUpTop;
    private int typeChangeMap; //capsule, ngọc rồng đen...
    private int indexMenu; //menu npc
    private int typeInput; //input
    private byte typeLuckyRound; //type lucky round

    private long idPlayThachDau; //id người chơi được mời thách đấu
    private int goldThachDau; //vàng thách đấu

    private long idEnemy; //id kẻ thù - trả thù

    private Shop shopOpen; //shop người chơi đang mở
    private String tagNameShop; //thẻ tên shop đang mở
    private boolean goToGas;
    private long lastTimeGotoGas;

    /**
     * loại tàu vận chuyển dùng ;0 - Không dùng ;1 - Tàu vũ trụ ;2 - Dịch chuyển
     * tức thời ;3 - Tàu tenis
     */
    private byte idSpaceShip;

    private long lastTimeBan;
    private boolean isBan;

    //giao dịch
    private int playerTradeId = -1;
    private Player playerTrade;
    private long lastTimeTrade;

    private long lastTimeNotifyTimeHoldBlackBall;
    private long lastTimeHoldBlackBall;
    private int tempIdBlackBallHold = -1;
    private boolean holdBlackBall;

    private int tempIdNamecBallHold = -1;
    private boolean holdNamecBall;

    private boolean loadedAllDataPlayer; //load thành công dữ liệu người chơi từ database

    private long lastTimeChangeFlag;

    //tới tương lai
    private boolean gotoFuture;
    private long lastTimeGoToFuture;

    private long lastTimeChangeZone;
    private long lastTimeChatGlobal;
    private long lastTimeChatPrivate;

    private long lastTimePickItem;

    private boolean goToBDKB;
    private long lastTimeGoToBDKB;
    private long lastTimeAnXienTrapBDKB;

    private boolean goToKG;
    private long lastTimeGoToKG;
    private long lastTimeAnXienTrapKG;

    private Npc npcChose; //npc mở

    private byte loaiThe; //loại thẻ nạp

    private long lastTimeSkillSpecial;

    public boolean isBaseMenu() {
        return this.indexMenu == ConstNpc.BASE_MENU;
    }

    public void dispose() {
        if (this.shopOpen != null) {
            this.shopOpen.dispose();
            this.shopOpen = null;
        }
        this.npcChose = null;
        this.tagNameShop = null;
        this.playerTrade = null;
    }

    // Getters and Setters
    public int getIdItemUpTop() {
        return idItemUpTop;
    }

    public void setIdItemUpTop(int idItemUpTop) {
        this.idItemUpTop = idItemUpTop;
    }

    public int getTypeChangeMap() {
        return typeChangeMap;
    }

    public void setTypeChangeMap(int typeChangeMap) {
        this.typeChangeMap = typeChangeMap;
    }

    public int getIndexMenu() {
        return indexMenu;
    }

    public void setIndexMenu(int indexMenu) {
        this.indexMenu = indexMenu;
    }

    public int getTypeInput() {
        return typeInput;
    }

    public void setTypeInput(int typeInput) {
        this.typeInput = typeInput;
    }

    public byte getTypeLuckyRound() {
        return typeLuckyRound;
    }

    public void setTypeLuckyRound(byte typeLuckyRound) {
        this.typeLuckyRound = typeLuckyRound;
    }

    public long getIdPlayThachDau() {
        return idPlayThachDau;
    }

    public void setIdPlayThachDau(long idPlayThachDau) {
        this.idPlayThachDau = idPlayThachDau;
    }

    public int getGoldThachDau() {
        return goldThachDau;
    }

    public void setGoldThachDau(int goldThachDau) {
        this.goldThachDau = goldThachDau;
    }

    public long getIdEnemy() {
        return idEnemy;
    }

    public void setIdEnemy(long idEnemy) {
        this.idEnemy = idEnemy;
    }

    public Shop getShopOpen() {
        return shopOpen;
    }

    public void setShopOpen(Shop shopOpen) {
        this.shopOpen = shopOpen;
    }

    public String getTagNameShop() {
        return tagNameShop;
    }

    public void setTagNameShop(String tagNameShop) {
        this.tagNameShop = tagNameShop;
    }

    public boolean isGoToGas() {
        return goToGas;
    }

    public void setGoToGas(boolean goToGas) {
        this.goToGas = goToGas;
    }

    public long getLastTimeGotoGas() {
        return lastTimeGotoGas;
    }

    public void setLastTimeGotoGas(long lastTimeGotoGas) {
        this.lastTimeGotoGas = lastTimeGotoGas;
    }

    public byte getIdSpaceShip() {
        return idSpaceShip;
    }

    public void setIdSpaceShip(byte idSpaceShip) {
        this.idSpaceShip = idSpaceShip;
    }

    public long getLastTimeBan() {
        return lastTimeBan;
    }

    public void setLastTimeBan(long lastTimeBan) {
        this.lastTimeBan = lastTimeBan;
    }

    public boolean isBan() {
        return isBan;
    }

    public void setBan(boolean isBan) {
        this.isBan = isBan;
    }

    public int getPlayerTradeId() {
        return playerTradeId;
    }

    public void setPlayerTradeId(int playerTradeId) {
        this.playerTradeId = playerTradeId;
    }

    public Player getPlayerTrade() {
        return playerTrade;
    }

    public void setPlayerTrade(Player playerTrade) {
        this.playerTrade = playerTrade;
    }

    public long getLastTimeTrade() {
        return lastTimeTrade;
    }

    public void setLastTimeTrade(long lastTimeTrade) {
        this.lastTimeTrade = lastTimeTrade;
    }

    public long getLastTimeNotifyTimeHoldBlackBall() {
        return lastTimeNotifyTimeHoldBlackBall;
    }

    public void setLastTimeNotifyTimeHoldBlackBall(long lastTimeNotifyTimeHoldBlackBall) {
        this.lastTimeNotifyTimeHoldBlackBall = lastTimeNotifyTimeHoldBlackBall;
    }

    public long getLastTimeHoldBlackBall() {
        return lastTimeHoldBlackBall;
    }

    public void setLastTimeHoldBlackBall(long lastTimeHoldBlackBall) {
        this.lastTimeHoldBlackBall = lastTimeHoldBlackBall;
    }

    public int getTempIdBlackBallHold() {
        return tempIdBlackBallHold;
    }

    public void setTempIdBlackBallHold(int tempIdBlackBallHold) {
        this.tempIdBlackBallHold = tempIdBlackBallHold;
    }

    public boolean isHoldBlackBall() {
        return holdBlackBall;
    }

    public void setHoldBlackBall(boolean holdBlackBall) {
        this.holdBlackBall = holdBlackBall;
    }

    public int getTempIdNamecBallHold() {
        return tempIdNamecBallHold;
    }

    public void setTempIdNamecBallHold(int tempIdNamecBallHold) {
        this.tempIdNamecBallHold = tempIdNamecBallHold;
    }

    public boolean isHoldNamecBall() {
        return holdNamecBall;
    }

    public void setHoldNamecBall(boolean holdNamecBall) {
        this.holdNamecBall = holdNamecBall;
    }

    public boolean isLoadedAllDataPlayer() {
        return loadedAllDataPlayer;
    }

    public void setLoadedAllDataPlayer(boolean loadedAllDataPlayer) {
        this.loadedAllDataPlayer = loadedAllDataPlayer;
    }

    public long getLastTimeChangeFlag() {
        return lastTimeChangeFlag;
    }

    public void setLastTimeChangeFlag(long lastTimeChangeFlag) {
        this.lastTimeChangeFlag = lastTimeChangeFlag;
    }

    public boolean isGotoFuture() {
        return gotoFuture;
    }

    public void setGotoFuture(boolean gotoFuture) {
        this.gotoFuture = gotoFuture;
    }

    public long getLastTimeGoToFuture() {
        return lastTimeGoToFuture;
    }

    public void setLastTimeGoToFuture(long lastTimeGoToFuture) {
        this.lastTimeGoToFuture = lastTimeGoToFuture;
    }

    public long getLastTimeChangeZone() {
        return lastTimeChangeZone;
    }

    public void setLastTimeChangeZone(long lastTimeChangeZone) {
        this.lastTimeChangeZone = lastTimeChangeZone;
    }

    public long getLastTimeChatGlobal() {
        return lastTimeChatGlobal;
    }

    public void setLastTimeChatGlobal(long lastTimeChatGlobal) {
        this.lastTimeChatGlobal = lastTimeChatGlobal;
    }

    public long getLastTimeChatPrivate() {
        return lastTimeChatPrivate;
    }

    public void setLastTimeChatPrivate(long lastTimeChatPrivate) {
        this.lastTimeChatPrivate = lastTimeChatPrivate;
    }

    public long getLastTimePickItem() {
        return lastTimePickItem;
    }

    public void setLastTimePickItem(long lastTimePickItem) {
        this.lastTimePickItem = lastTimePickItem;
    }

    public boolean isGoToBDKB() {
        return goToBDKB;
    }

    public void setGoToBDKB(boolean goToBDKB) {
        this.goToBDKB = goToBDKB;
    }

    public long getLastTimeGoToBDKB() {
        return lastTimeGoToBDKB;
    }

    public void setLastTimeGoToBDKB(long lastTimeGoToBDKB) {
        this.lastTimeGoToBDKB = lastTimeGoToBDKB;
    }

    public long getLastTimeAnXienTrapBDKB() {
        return lastTimeAnXienTrapBDKB;
    }

    public void setLastTimeAnXienTrapBDKB(long lastTimeAnXienTrapBDKB) {
        this.lastTimeAnXienTrapBDKB = lastTimeAnXienTrapBDKB;
    }

    public boolean isGoToKG() {
        return goToKG;
    }

    public void setGoToKG(boolean goToKG) {
        this.goToKG = goToKG;
    }

    public long getLastTimeGoToKG() {
        return lastTimeGoToKG;
    }

    public void setLastTimeGoToKG(long lastTimeGoToKG) {
        this.lastTimeGoToKG = lastTimeGoToKG;
    }

    public long getLastTimeAnXienTrapKG() {
        return lastTimeAnXienTrapKG;
    }

    public void setLastTimeAnXienTrapKG(long lastTimeAnXienTrapKG) {
        this.lastTimeAnXienTrapKG = lastTimeAnXienTrapKG;
    }

    public Npc getNpcChose() {
        return npcChose;
    }

    public void setNpcChose(Npc npcChose) {
        this.npcChose = npcChose;
    }

    public byte getLoaiThe() {
        return loaiThe;
    }

    public void setLoaiThe(byte loaiThe) {
        this.loaiThe = loaiThe;
    }

    public long getLastTimeSkillSpecial() {
        return lastTimeSkillSpecial;
    }

    public void setLastTimeSkillSpecial(long lastTimeSkillSpecial) {
        this.lastTimeSkillSpecial = lastTimeSkillSpecial;
    }
}
