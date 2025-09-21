/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Dragon.models.skill;

/**
 *
 * @author Administrator
 */
public class SkillNotFocus extends Skill {

    private int timePre;

    private int timeDame;

    private short range;

    private long time;

    public SkillNotFocus(Skill skill) {
        super(skill);
        this.timePre = 2000;
        this.timeDame = 3000;
        this.range = 250;
    }

    // Getters/Setters
    public int getTimePre() {
        return timePre;
    }

    public void setTimePre(int timePre) {
        this.timePre = timePre;
    }

    public int getTimeDame() {
        return timeDame;
    }

    public void setTimeDame(int timeDame) {
        this.timeDame = timeDame;
    }

    public short getRange() {
        return range;
    }

    public void setRange(short range) {
        this.range = range;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
