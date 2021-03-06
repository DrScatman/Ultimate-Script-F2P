package script.tanner.tasks;

import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.GrandExchange;
import org.rspeer.runetek.api.component.GrandExchangeSetup;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;
import script.tanner.Main;

public class CheckRestock extends Task {

    private Main main;
    private Banking banking;

    public CheckRestock(Main main) {
        this.main = main;
        banking = new Banking(main);
    }

    @Override
    public boolean validate() {
        return main.restock && main.checkRestock && !main.isMuling;
    }

    @Override
    // Having issues accessing inventory while banking, which is why so many damn conditions
    public int execute() {
        Log.info("Restock check");
        if(Bank.isOpen()){
            Bank.close();
        }
        if(GrandExchange.isOpen() || GrandExchangeSetup.isOpen()){
            main.closeGE();
        }

        boolean hasH = false;
        boolean hasM = true;

        if (Inventory.contains(main.COWHIDE) || Inventory.contains(main.COWHIDE + 1)) {
            hasH = true;
            if (main.lootCows || main.killCows) {
                if (Inventory.getCount(true, main.COWHIDE) < main.lootAmount) {
                    hasH = false;
                }
            }
        } else {
            banking.openAndDepositAll(0);

            if (Bank.contains(main.COWHIDE)) {
                hasH = true;
            }
            if (Bank.getCount(995) < 1) {
                hasM = false;
            }
        }

        if (!hasH || !hasM) {
            Log.fine("Restocking: ->");
            Time.sleep(1000);
            if (!hasH)
                main.printHide();

            if (Bank.getCount(995) < 1) {
                Log.fine("Out of money");
                if (!main.killCows && !main.lootCows) {
                    Log.fine("Selling Leathers");
                }
            }

            Bank.close();
            Time.sleepUntil(() -> !Bank.isOpen(), 5000);
            /*if(BankLocation.getNearest().equals(BankLocation.AL_KHARID)) {
                Time.sleep(1500);
                main.teleportHome();
            }*/

            main.restock = true;
        } else {
            Log.fine("Restock not necessary");
            main.restock = false;
        }
        main.checkRestock = false;
        return 1000;
    }
}
