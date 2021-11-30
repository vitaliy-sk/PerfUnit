package dev.techh.perfunit.integration.service;

public class ExpensiveService {

    public long count5InvocationsAllowed() {
        return System.currentTimeMillis();
    }

    public long count0InvocationsAllowedNotFail() {
        return System.currentTimeMillis();
    }

    public void time10MsecSingleAllowed(int delay) {
        delay(delay);
    }

    public void time10MsecTotalAllowed(int delay) {
        delay(delay);
    }

    public long unknownCallerNotAllowed() {
        return System.currentTimeMillis();
    }

    public long unknownCallerAllowed() {
        return System.currentTimeMillis();
    }

    private void delay(int delay) {
        try {
            Thread.sleep(delay);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
