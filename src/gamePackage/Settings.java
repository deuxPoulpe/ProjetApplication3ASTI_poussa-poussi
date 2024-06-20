package gamePackage;

public class Settings {
    
    private static Settings instance = null;

    private boolean allowPushBack = false;
    private boolean displayInTerminal = true;
    private boolean mandatoryPush = false;

    private Settings(boolean pushBack, boolean display, boolean mandatoryPushRule) {
        this.allowPushBack = pushBack;
        this.mandatoryPush = mandatoryPushRule;
        this.displayInTerminal = display;
    }

    public static Settings getInstance(boolean pushBack, boolean display, boolean mandatoryPushRule) {
        if (instance == null) {
            instance = new Settings(pushBack, display, mandatoryPushRule);
        }
        return instance;
    }

    public static Settings getInstance() {
        return instance;
    }

    public boolean getAllowPushBack() {
        return allowPushBack;
    }

    public boolean getDisplayInTerminal() {
        return displayInTerminal;
    }

    public boolean getMandatoryPush() {
        return mandatoryPush;
    }

    public void setAllowPushBack(boolean pushBack) {
        this.allowPushBack = pushBack;
    }

    public void setDisplayInTerminal(boolean display) {
        this.displayInTerminal = display;
    }

    public void setMandatoryPush(boolean mandatoryPushRule) {
        this.mandatoryPush = mandatoryPushRule;
    }
}