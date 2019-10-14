package script.beg;

import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.Worlds;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.providers.RSWorld;
import org.rspeer.script.task.Task;
import script.Beggar;
import script.data.CheckInstances;
import script.data.CheckTutIsland;

public class StartupChecks extends Task {

    private Beggar main;

    public StartupChecks(Beggar main){
        this.main = main;
    }

    @Override
    public boolean validate() {
        return !main.startupChecks && Game.isLoggedIn() && Worlds.getCurrent() > 0;
    }

    @Override
    public int execute() {
        if (!Game.isLoggedIn() || Players.getLocal() == null)
            return 2000;

        tutIslandCheck();
        fighterCheck();
        instanceCheck();
        //addWorldToFile();
        setNextBotWorld(300);
        main.startupChecks = true;
        return 1000;
    }

    private void tutIslandCheck() {
        CheckTutIsland checkT = new CheckTutIsland(main);

        if (checkT.onTutIsland()) {
            checkT.execute();
        }
    }

    private void fighterCheck() {
        if (Players.getLocal().getCombatLevel() <= 3) {
            main.startFighter(false);
        }
        if (Beggar.OGRESS) {
            main.startFighter(false);
        }
    }

    public void instanceCheck() {
        CheckInstances checkI = new CheckInstances(main);
        while (checkI.validate()) {
            Time.sleep(checkI.execute(main.readAccount(true)));
        }

        main.runningClients = checkI.getRunningClients();
    }

    private void addWorldToFile() {
        main.currWorld = Worlds.getCurrent();
        main.writeWorldToFile(main.currWorld);
    }

    private void setNextBotWorld(int pop) {
        RSWorld newWorld = Worlds.get(x -> x != null && x.getPopulation() <= pop &&
                !x.isMembers() && !x.isBounty() && !x.isSkillTotal() && !x.isPVP());

        if (newWorld != null && newWorld.getId() > 0) {
            main.nextBotWorld = newWorld.getId();
        } else if (pop < 1000) {
            setNextBotWorld(pop + 100);
        } else {
            main.nextBotWorld = 0;
        }
    }
}
