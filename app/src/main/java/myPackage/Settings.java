package myPackage;

public class Settings {
    
    private static Settings instance = null;

    private boolean allowPushBack = true;
    private boolean displayInTerminal = false;

    private Settings(boolean pushBack, boolean display) {
        this.allowPushBack = pushBack;
        this.displayInTerminal = display;
    }

    public static Settings getInstance(boolean pushBack, boolean display) {
        if (instance == null) {
            instance = new Settings(pushBack, display);
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

    public void setAllowPushBack(boolean pushBack) {
        this.allowPushBack = pushBack;
    }

    public void setDisplayInTerminal(boolean display) {
        this.displayInTerminal = display;
    }
}