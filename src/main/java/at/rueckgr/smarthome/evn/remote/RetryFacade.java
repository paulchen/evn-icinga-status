package at.rueckgr.smarthome.evn.remote;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RetryFacade implements SmartHomeService {
    private static final Logger logger = LogManager.getLogger(RetryFacade.class);

    private SmartHomeService delegate;

    public RetryFacade(final SmartHomeService delegate) {
        this.delegate = delegate;
    }

    @Override
    public void login() {
        delegate.login();
    }

    @Override
    public SmartHomeState getState() {
        boolean alreadyTriedToLogin = checkLogin();

        try {
            return delegate.getState();
        }
        catch (SmartHomeException e) {
            if(alreadyTriedToLogin) {
                throw e;
            }

            relogin();
            return delegate.getState();
        }
    }

    @Override
    public void setSessionToken(final String sessionToken) {
        delegate.setSessionToken(sessionToken);
    }

    @Override
    public String getSessionToken() {
        return delegate.getSessionToken();
    }

    @Override
    public void setTemperatureSettings(final Room room, final TemperatureSettings oldSettings, final TemperatureSettings newSettings) {
        boolean alreadyTriedToLogin = checkLogin();

        try {
            delegate.setTemperatureSettings(room, oldSettings, newSettings);
        }
        catch (SmartHomeException e) {
            if(alreadyTriedToLogin) {
                throw e;
            }

            relogin();
            delegate.setTemperatureSettings(room, oldSettings, newSettings);
        }
    }

    private boolean checkLogin() {
        if(delegate.getSessionToken() != null) {
            return false;
        }

        login();
        return true;
    }

    private void relogin() {
        logger.info("Exception occurred during first service call, trying to login again");

        delegate.login();
    }
}
