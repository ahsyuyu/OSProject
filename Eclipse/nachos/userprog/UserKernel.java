package nachos.userprog;

import java.io.IOException;
import java.util.zip.DataFormatException;

import nachos.machine.Coff;
import nachos.machine.Lib;
import nachos.machine.Machine;
import nachos.machine.Processor;
import nachos.threads.KThread;
import nachos.threads.ThreadedKernel;

/**
 * A kernel that can support multiple user processes.
 */
public class UserKernel extends ThreadedKernel {
    /**
     * Allocate a new user kernel.
     */
    public UserKernel() {
        super();
    }

    /**
     * Initialize this kernel. Creates a synchronized console and sets the processor's exception
     * handler.
     */
    @Override
    public void initialize(String[] args) {
        super.initialize(args);

        console = new SynchConsole(Machine.console());

        Machine.processor().setExceptionHandler(new Runnable() {
            @Override
            public void run() {
                try {
                    exceptionHandler();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (DataFormatException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Test the console device.
     */
    @Override
    public void selfTest() {
        // super.selfTest(); Not necessary to test threaded kernel for now
    	
    	// Disable console testing for now.
        /* System.out.println("Testing the console device. Typed characters");
        System.out.println("will be echoed until q is typed.");

        char c;

        do {
            c = (char) console.readByte(true);
            console.writeByte(c);
        } while (c != 'q');

        System.out.println("");*/
    }

    /**
     * Returns the current process.
     *
     * @return the current process, or <tt>null</tt> if no process is current.
     */
    public static UserProcess currentProcess() {
        if (!(KThread.currentThread() instanceof UThread))
            return null;

        return ((UThread) KThread.currentThread()).process;
    }

    /**
     * The exception handler. This handler is called by the processor whenever a user instruction
     * causes a processor exception.
     *
     * <p>
     * When the exception handler is invoked, interrupts are enabled, and the processor's cause
     * register contains an integer identifying the cause of the exception (see the
     * <tt>exceptionZZZ</tt> constants in the <tt>Processor</tt> class). If the exception involves a
     * bad virtual address (e.g. page fault, TLB miss, read-only, bus error, or address error), the
     * processor's BadVAddr register identifies the virtual address that caused the exception.
     * 
     * @throws DataFormatException
     * @throws IOException
     */
    public void exceptionHandler() throws IOException, DataFormatException {
        Lib.assertTrue(KThread.currentThread() instanceof UThread);

        UserProcess process = ((UThread) KThread.currentThread()).process;
        int cause = Machine.processor().readRegister(Processor.regCause);
        process.handleException(cause);
    }

    /**
     * Start running user programs, by creating a process and running a shell program in it. The
     * name of the shell program it must run is returned by <tt>Machine.getShellProgramName()</tt>.
     *
     * @see nachos.machine.Machine#getShellProgramName
     */
    @Override
    public void run() {
        super.run();

        UserProcess process = UserProcess.newUserProcess();

        String shellProgram = Machine.getShellProgramName();
        Lib.assertTrue(process.execute(shellProgram, new String[] {}));

        KThread.currentThread().finish();
    }

    /**
     * Terminate this kernel. Never returns.
     */
    @Override
    public void terminate() {
        super.terminate();
    }

    /** Globally accessible reference to the synchronized console. */
    public static SynchConsole console;

    // dummy variables to make javac smarter
    private static Coff dummy1 = null;
}
