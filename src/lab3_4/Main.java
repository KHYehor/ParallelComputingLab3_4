package lab3_4;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public synchronized static void main(String[] args) throws InterruptedException, ExecutionException {
        /**
         * Variant20
         * O = SORT(P) * SORT(MR * MS);
         */
        ExecutorService service1 = Executors.newFixedThreadPool(2);
        Vector P = new Vector("./task1/P.txt").initWithRandomValues();
        Matrix MR = new Matrix("./task1/MR.txt").initWithRandomValues();
        Matrix MS = new Matrix("./task1/MS.txt").initWithRandomValues();
        // Create locker condition
        ReentrantLock locker = new ReentrantLock();
        // Void task1
        Runnable task1 = () -> {
            // start locking the flow
            locker.lock();
            // 1. SORT(P)
            P.sort();
            // unlock the flow
            locker.unlock();
        };
        // Copy values, because they are variables
        Matrix finalMR = MR;
        Matrix finalMS = MS;
        Callable<Vector> task2 = () -> {
            Matrix Result = finalMR
                    // 2. MR*MS;
                    .multiplyWithMatrix(finalMS)
                    // 3. SORT(MR*MS);
                    .sort();
            // Wait till sorting of P will be finished
            locker.lock();
            // 4. SORT(P) * SORT(MR*MS)
            P.multiplyWithMatrix(Result)
                    .saveFinalResult("./task1/O.txt");
            // Unlock and leave locker
            locker.unlock();
            return P;
        };
        // Add task 1 to execute threads pull
        service1.execute(task1);
        // Add task2 to execute threads pull + print result
        service1.submit(task2).get().printResult();
        /**
         * Variant20
         * MG = MB * MS + MC * (MR + MM);
         */
        ExecutorService service2 = Executors.newFixedThreadPool(2);
        Matrix MB = new Matrix("./task2/MB.txt").initWithRandomValues();
        MS = new Matrix("./task2/MS.txt").initWithRandomValues();
        Matrix MC = new Matrix("./task2/MC.txt").initWithRandomValues();
        MR = new Matrix("./task2/MR.txt").initWithRandomValues();
        Matrix MM = new Matrix("./task2/MM.txt").initWithRandomValues();
        // Copy values, because they are variables
        Matrix finalMS1 = MS;
        Runnable task3 = () -> {
            // start locking the flow
            locker.lock();
            // 1. MB * MS
            MB.multiplyWithMatrix(finalMS1);
            // unlock the flow
            locker.unlock();
        };
        Matrix finalMR1 = MR;
        Callable<Matrix> task4 = () -> {
            finalMR1
                    // 2. MR + MM
                    .sumWithMatrix(MM)
                    // 3. MC * (MR + MM)
                    .multiplyWithMatrix(MC);
            // Wait till MB * MS will be finished
            locker.lock();
            finalMR1
                    // 4. (MB * MS) + (MC * (MR + MM))
                    .sumWithMatrix(MB)
                    .printResult()
                    .saveFinalResult("./task2/MG.txt");
            // Unlock and leave locker
            locker.unlock();
            return finalMR1;
        };
        // Add task 3 to execute threads pull
        service2.execute(task3);
        // Add task 4 to execute threads pull + print result
        service2.submit(task4).get().printResult();
    }
}
