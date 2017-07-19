/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Other;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author Nikos
 * @see <a href="http://www.rgagnon.com/javadetails/java-detect-windows-idle-state-jna.html">http://www.rgagnon.com/javadetails/java-detect-windows-idle-state-jna.html</a>
 */
public class Win32IdleTime {

    public interface Kernel32 extends StdCallLibrary {

        Kernel32 INSTANCE = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class);

        /**
         * Retrieves the number of milliseconds that have elapsed since the
         * system was started.
         *
         * @see <a href="http://msdn2.microsoft.com/en-us/library/ms724408.aspx">GetTickCount function</a>
         * @return number of milliseconds that have elapsed since the system was
         * started.
         */
        public int GetTickCount();
    };

    public interface User32 extends StdCallLibrary {

        User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class);

        /**
         * Contains the time of the last input.
         */
        public static class LASTINPUTINFO extends Structure {

            public int cbSize = 8;

            /// Tick count of when the last input event was received.
            public int dwTime;

            @SuppressWarnings("rawtypes")
            @Override
            protected List getFieldOrder() {
                return Arrays.asList(new String[]{"cbSize", "dwTime"});
            }
        }

        /**
         * Retrieves the time of the last input event.
         * @return time of the last input event, in milliseconds
         */
        public boolean GetLastInputInfo(LASTINPUTINFO result);
    };

    /**
     * Get the amount of milliseconds that have elapsed since the last input
     * event (mouse or keyboard)
     *
     * @return idle time in milliseconds
     */
    public static int getIdleTimeMillisWin32() {
        User32.LASTINPUTINFO lastInputInfo = new User32.LASTINPUTINFO();
        User32.INSTANCE.GetLastInputInfo(lastInputInfo);
        return Kernel32.INSTANCE.GetTickCount() - lastInputInfo.dwTime;
    }

    public static int getIdleTimeSeconds() {
        return getIdleTimeMillisWin32() / 1000;
    }

    enum State {
        UNKNOWN, ONLINE, IDLE, AWAY
    };

    // TEST
    public static void main(String[] args) throws Exception {
        if (!System.getProperty("os.name").contains("Windows")) {
            System.err.println("ERROR: Only implemented on Windows");
            System.exit(1);
        }
        State state = State.UNKNOWN;
        DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");

        for (;;) {
            int idleSec = getIdleTimeMillisWin32() / 1000;

            State newState
                    = idleSec < 30 ? State.ONLINE
                            : idleSec > 5 * 60 ? State.AWAY : State.IDLE;

            if (newState != state) {
                state = newState;
                System.out.println(dateFormat.format(new Date()) + " # " + state);
                //
                // just for fun, if the state is AWAY (screensaver is coming!)
                // we move the mouse wheel using java.awt.Robot just a little bit to change
                // the state and prevent the screen saver execution.
                //
                if (state == State.AWAY) {
                    System.out.println("Activate the mouse wheel to change state!");
                    java.awt.Robot robot = new java.awt.Robot();
                    robot.mouseWheel(-1);
                    robot.mouseWheel(1);
                }
            }
            try {
                Thread.sleep(1000);
            } catch (Exception ex) {
            }
        }
    }
}
