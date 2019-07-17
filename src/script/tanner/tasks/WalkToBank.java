package script.tanner.tasks;

import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;
import script.tanner.Main;

public class WalkToBank extends Task {

    private Main main;
    private CommonConditions cc;

    public WalkToBank (Main main) {
        this.main = main;
        cc = new CommonConditions(main);
    }

    @Override
    public boolean validate() {
        // True if player is far away from the bank
        return (!cc.atBank() && (!cc.gotCowhide() || !cc.gotEnoughCoins())) &&
                !main.restock && !main.isMuling;
    }

    @Override
    public int execute() {
        //Log.info("Walking to AK bank");
        if (WalkingHelper.shouldEnableRun()) {
            WalkingHelper.enableRun();
        }
        if (WalkingHelper.shouldSetDestination()) {
            if (Movement.walkToRandomized(BankLocation.AL_KHARID.getPosition())) {
                Time.sleepUntil(cc::atBank, Random.mid(1800, 2400));
            }
        }
        return 600;
    }
}