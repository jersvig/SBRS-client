package net.urosk.sbrs;

import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.File;
import java.util.UUID;

/**
 * Created by jernej on 5/22/15.
 */
public class SBRSClient {


    private HttpClient httpClient;
    private String sbrsUrl;
    private String pathToWorkingFolder;

    public SBRSClient(String sbrsUrl, String pathToWorkingFolder) throws Exception{

        this.sbrsUrl = sbrsUrl;
        if (!this.sbrsUrl.endsWith("/")) {
            this.sbrsUrl += "/";
        }

        this.pathToWorkingFolder = pathToWorkingFolder;
        if(!this.pathToWorkingFolder.endsWith(File.separator)){
            this.pathToWorkingFolder += File.separator;
        }

        this.pathToWorkingFolder += UUID.randomUUID().toString();
        FileUtils.forceMkdir(new File(this.pathToWorkingFolder));

        // create http client
        this.httpClient = HttpClientBuilder.create().build();

    }

    public File getReport(String reportTemplatePath, String format, String dataSource) throws Exception {
        return getReport(reportTemplatePath, format, dataSource, null);
    }

    public File getReport(String reportTemplatePath, String format, String dataSource, String reportFileName) throws Exception {

        String urlSuffix = "?__report=" + reportTemplatePath;
        urlSuffix += "&__format=" + format;
        urlSuffix += "&ds=" + dataSource;

        String requestUrl = this.sbrsUrl + urlSuffix;

        // create end request
        HttpGet getRequest = new HttpGet(requestUrl);

        HttpResponse response = httpClient.execute(getRequest);

        if (response != null) {

            //
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                if (reportFileName == null) {

                    // get all headers
                    Header[] headers = response.getAllHeaders();

                    for (Header header : headers) {

                        if (header.getName().equals("Content-Disposition")) {
                            String valueItems[] = header.getValue().split("filename=");
                            if (valueItems.length > 1) {
                                reportFileName = valueItems[1];
                                break;
                            } else {
                                throw new Exception("Could not determine report file name.");
                            }

                        }
                    }


                }

                File reportFile = new File(pathToWorkingFolder + File.separator + reportFileName);
                FileUtils.copyInputStreamToFile(response.getEntity().getContent(), reportFile);
                return reportFile;

            } else {
                throw new Exception("Server error: " + response.getStatusLine().getStatusCode());
            }


        } else {

            throw new Exception("No response from server.");

        }

    }


    public void cleanWorkingFolder() throws Exception {
        FileUtils.deleteDirectory(new File(pathToWorkingFolder));
    }


}
