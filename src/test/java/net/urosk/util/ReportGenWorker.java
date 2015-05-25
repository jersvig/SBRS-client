package net.urosk.util;

import net.urosk.sbrs.SBRSClient;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * Created by jernej on 5/25/15.
 */
public class ReportGenWorker implements Callable<Boolean> {


    private int runs;

    public ReportGenWorker(int runs) {
        this.runs = runs;
    }

    @Override
    public Boolean call() throws Exception {

        String id = UUID.randomUUID().toString();

        SBRSClientProperties properties = new SBRSClientProperties();

        String sbrsUrl = properties.getValue("url");
        String localWorkingFolder = properties.getValue("local.working.folder");
        String reportTemplate = properties.getValue("report.template");
        String dataSource = properties.getValue("data.source");

        System.out.println("Started thread: " + id);

        SBRSClient client = new SBRSClient(sbrsUrl, localWorkingFolder);

        try {
            for (int i = 0; i < runs; i++) {

                File report = client.getReport(reportTemplate, "PDF", dataSource, "report.pdf");

                if (report.getTotalSpace() == 0) {
                    throw new Exception("Report is empty.");
                }

            }
        } catch (Exception x) {

            x.printStackTrace();
            return false;

        } finally {

            client.cleanWorkingFolder();
            System.out.println("Thread stopped: " + id);
        }

        return true;
    }
}
