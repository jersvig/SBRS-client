package net.urosk;

import net.urosk.util.ReportGenWorker;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * Created by jernej on 5/22/15.
 */
public class TestSBRSClient {

    @Test
    public void testClient() throws Exception {

        int threadCount = 5;
        int runs = 5;

        runThreads(threadCount, runs);

    }

    private void runThreads(int threadCount, int runs) throws Exception {
        List<Callable<Boolean>> tasks = Collections.nCopies(threadCount, (Callable<Boolean>) new ReportGenWorker(runs));
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        List futures = executorService.invokeAll(tasks);

        for (Object o : futures) {

            FutureTask ft = (FutureTask) o;
            Assert.assertTrue("Thread failed ...", (Boolean) ft.get());

        }
    }
}
