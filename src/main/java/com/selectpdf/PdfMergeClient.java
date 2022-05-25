package com.selectpdf;

import java.io.*;

/**
 * Pdf Merge with SelectPdf Online API.
 * 
 * <pre>
 * {@code
package com.selectpdf;

public class PdfMerge {
    public static void main(String[] args) throws Exception {
        String testUrl = "https://selectpdf.com/demo/files/selectpdf.pdf";
        String testPdf = "Input.pdf";
        String localFile = "Result.pdf";
        String apiKey = "Your API key here";

        System.out.println(String.format("This is SelectPdf-%s.", ApiClient.CLIENT_VERSION));

        try {
            PdfMergeClient client = new PdfMergeClient(apiKey);

            // set parameters - see full list at https://selectpdf.com/pdf-merge-api/
            client
                // specify the pdf files that will be merged (order will be preserved in the final pdf)

                .addFile(testPdf) // add PDF from local file
                .addUrlFile(testUrl) // add PDF From public url
                // .addFile(testPdf, "pdf_password") // add PDF (that requires a password) from local file
                // .addUrlFile(testUrl, "pdf_password") // add PDF (that requires a password) from public url
            ;

            System.out.println("Starting pdf merge...");

            // merge pdfs to local file
            client.saveToFile(localFile);

            // merge pdfs to memory
            // byte[] pdf = client.save();

            System.out.println(String.format("Finished! Number of pages: %d.", client.getNumberOfPages()));

            // get API usage
            UsageClient usageClient = new UsageClient(apiKey);
            String usage = usageClient.getUsage(false);
            System.out.printf("Usage details: %s.\r\n", usage);

            // org.json.JSONObject usageObject = new org.json.JSONObject(usage);
            // int available = usageObject.getInt("available");
            // System.out.printf("Conversions remained this month: %d.\r\n", available);

        }
        catch (Exception ex) {
            System.out.println("An error occured: " + ex.getMessage());
        }
    }
        
}
 * }
 * </pre>
 */
public class PdfMergeClient extends ApiClient {
    private int fileIdx = 0;

    /**
     * Construct the Pdf Merge Client.
     * @param apiKey API Key.
     */
    public PdfMergeClient(String apiKey)
    {
        apiEndpoint = "https://selectpdf.com/api2/pdfmerge/";
        parameters.put("key", apiKey);
    }

    /**
     * Add local PDF document to the list of input files.
     * @param inputPdf Path to a local PDF file.
     * @return Reference to the current object.
     */
    public PdfMergeClient addFile(String inputPdf) {
        fileIdx++;

        files.put("file_" + fileIdx, inputPdf);
        parameters.remove("url_" + fileIdx);
        parameters.remove("password_" + fileIdx);

        return this;
    }

    /**
     * Add local PDF document to the list of input files.
     * @param inputPdf Path to a local PDF file.
     * @param userPassword User password for the PDF document.
     * @return Reference to the current object.
     */
    public PdfMergeClient addFile(String inputPdf, String userPassword) {
        fileIdx++;

        files.put("file_" + fileIdx, inputPdf);
        parameters.remove("url_" + fileIdx);
        parameters.put("password_" + fileIdx, userPassword);

        return this;
    }

    /**
     * Add remote PDF document to the list of input files.
     * @param inputUrl Url of a remote PDF file.
     * @return Reference to the current object.
     */
    public PdfMergeClient addUrlFile(String inputUrl) {
        fileIdx++;

        files.remove("file_" + fileIdx);
        parameters.put("url_" + fileIdx, inputUrl);
        parameters.remove("password_" + fileIdx);

        return this;
    }

    /**
     * Add remote PDF document to the list of input files.
     * @param inputUrl Url of a remote PDF file.
     * @param userPassword User password for the PDF document.
     * @return Reference to the current object.
     */
    public PdfMergeClient addUrlFile(String inputUrl, String userPassword) {
        fileIdx++;

        files.remove("file_" + fileIdx);
        parameters.put("url_" + fileIdx, inputUrl);
        parameters.put("password_" + fileIdx, userPassword);

        return this;
    }

    /**
     * Merge all specified input pdfs and return the resulted PDF.
     * @return Byte array containing the resulted PDF.
     */
    public byte[] save() {
        parameters.put("async", "False");
        parameters.put("files_no", Integer.toString(fileIdx));

        byte[] result = performPostAsMultipartFormData(null);

        fileIdx = 0;
        files.clear();

        return result;
    }

    /**
     * Merge all specified input pdfs and writes the resulted PDF to a local file.
     * @param filePath Local output file including path if necessary.
     */
    public void saveToFile(String filePath) throws IOException 
    {
        byte[] result = save();
        FileOutputStream outputFile = new FileOutputStream(filePath);
        outputFile.write(result);
        outputFile.close();
    }

    /**
     * Merge all specified input pdfs and writes the resulted PDF to a specified stream.
     * @param stream The output stream where the resulted PDF will be written.
     */
    public void saveToStream(OutputStream stream) throws IOException 
    {
        byte[] result = save();
        stream.write(result);
    }

    /**
     * Merge all specified input pdfs and return the resulted PDF. An asynchronous call is used.
     * @return Byte array containing the resulted PDF.
     */
    public byte[] saveAsync() {
        parameters.put("files_no", Integer.toString(fileIdx));

        String JobID = startAsyncJobMultipartFormData();

        if (JobID == null || JobID.isBlank()) {
            throw new ApiException("An error occurred launching the asynchronous call.");
        }

        int noPings = 0;

        do
        {
            noPings++;

            // sleep for a few seconds before next ping
            try {
                java.util.concurrent.TimeUnit.SECONDS.sleep(AsyncCallsPingInterval);
            }
            catch (InterruptedException ex) {}

            AsyncJobClient asyncJobClient = new AsyncJobClient(parameters.get("key"), JobID);
            asyncJobClient.setApiEndpoint(apiAsyncEndpoint);

            byte[] result = asyncJobClient.getResult();

            if (asyncJobClient.finished())
            {
                numberOfPages = asyncJobClient.getNumberOfPages();

                fileIdx = 0;
                files.clear();

                return result;
            }

        } while (noPings <= AsyncCallsMaxPings);

        fileIdx = 0;
        files.clear();
        throw new ApiException("Asynchronous call did not finish in expected timeframe.");    
    }

    /**
     * Merge all specified input pdfs and writes the resulted PDF to a local file. An asynchronous call is used.
     * @param filePath Local output file including path if necessary.
     */
    public void saveToFileAsync(String filePath) throws IOException 
    {
        byte[] result = saveAsync();
        FileOutputStream outputFile = new FileOutputStream(filePath);
        outputFile.write(result);
        outputFile.close();
    }

    /**
     * Merge all specified input pdfs and writes the resulted PDF to a specified stream. An asynchronous call is used.
     * @param stream The output stream where the resulted PDF will be written.
     */
    public void saveToStreamAsync(OutputStream stream) throws IOException 
    {
        byte[] result = saveAsync();
        stream.write(result);
    }

    /**
     * Set the PDF document title.
     * @param docTitle Document title.
     * @return Reference to the current object.
     */
    public PdfMergeClient setDocTitle(String docTitle)
    {
        parameters.put("doc_title", docTitle);
        return this;
    }

    /**
     * Set the subject of the PDF document.
     * @param docSubject Document subject.
     * @return Reference to the current object.
     */
    public PdfMergeClient setDocSubject(String docSubject)
    {
        parameters.put("doc_subject", docSubject);
        return this;
    }

    /**
     * Set the PDF document keywords.
     * @param docKeywords Document keywords.
     * @return Reference to the current object.
     */
    public PdfMergeClient setDocKeywords(String docKeywords)
    {
        parameters.put("doc_keywords", docKeywords);
        return this;
    }

    /**
     * Set the name of the PDF document author.
     * @param docAuthor Document author.
     * @return Reference to the current object.
     */
    public PdfMergeClient setDocAuthor(String docAuthor)
    {
        parameters.put("doc_author", docAuthor);
        return this;
    }

    /**
     * Add the date and time when the PDF document was created to the PDF document information. The default value is False.
     * @param docAddCreationDate Add creation date to the document metadata or not.
     * @return Reference to the current object.
     */
    public PdfMergeClient setDocAddCreationDate(Boolean docAddCreationDate)
    {
        parameters.put("doc_add_creation_date", Boolean.toString(docAddCreationDate));
        return this;
    }

    /**
     * Set the page layout to be used when the document is opened in a PDF viewer. The default value is PageLayout.OneColumn.
     * @param pageLayout Page layout.
     * @return Reference to the current object.
     */
    public PdfMergeClient setViewerPageLayout(ApiEnums.PageLayout pageLayout)
    {
        parameters.put("viewer_page_layout", pageLayout.getValueAsString());
        return this;
    }

    /**
     * Set the document page mode when the pdf document is opened in a PDF viewer. The default value is PageMode.UseNone.
     * @param pageMode Page mode.
     * @return Reference to the current object.
     */
    public PdfMergeClient setViewerPageMode(ApiEnums.PageMode pageMode)
    {
        parameters.put("viewer_page_mode", pageMode.getValueAsString());
        return this;
    }

    /**
     * Set a flag specifying whether to position the document's window in the center of the screen. The default value is False.
     * @param viewerCenterWindow Center window or not.
     * @return Reference to the current object.
     */
    public PdfMergeClient setViewerCenterWindow(Boolean viewerCenterWindow)
    {
        parameters.put("viewer_center_window", Boolean.toString(viewerCenterWindow));
        return this;
    }

    /**
     * Set a flag specifying whether the window's title bar should display the document title taken from document information. The default value is False.
     * @param viewerDisplayDocTitle Display title or not.
     * @return Reference to the current object.
     */
    public PdfMergeClient setViewerDisplayDocTitle(Boolean viewerDisplayDocTitle)
    {
        parameters.put("viewer_display_doc_title", Boolean.toString(viewerDisplayDocTitle));
        return this;
    }

    /**
     * Set a flag specifying whether to resize the document's window to fit the size of the first displayed page. The default value is False.
     * @param viewerFitWindow Fit window or not.
     * @return Reference to the current object.
     */
    public PdfMergeClient setViewerFitWindow(Boolean viewerFitWindow)
    {
        parameters.put("viewer_fit_window", Boolean.toString(viewerFitWindow));
        return this;
    }

    /**
     * Set a flag specifying whether to hide the pdf viewer application's menu bar when the document is active. The default value is False.
     * @param viewerHideMenuBar Hide menu bar or not.
     * @return Reference to the current object.
     */
    public PdfMergeClient setViewerHideMenuBar(Boolean viewerHideMenuBar)
    {
        parameters.put("viewer_hide_menu_bar", Boolean.toString(viewerHideMenuBar));
        return this;
    }

    /**
     * Set a flag specifying whether to hide the pdf viewer application's tool bars when the document is active. The default value is False.
     * @param viewerHideToolbar Hide tool bars or not.
     * @return Reference to the current object.
     */
    public PdfMergeClient setViewerHideToolbar(Boolean viewerHideToolbar)
    {
        parameters.put("viewer_hide_toolbar", Boolean.toString(viewerHideToolbar));
        return this;
    }

    /**
     * Set a flag specifying whether to hide user interface elements in the document's window (such as scroll bars and navigation controls), leaving only the document's contents displayed. 
     * The default value is False.
     * @param viewerHideWindowUI Hide window UI or not.
     * @return Reference to the current object.
     */
    public PdfMergeClient setViewerHideWindowUI(Boolean viewerHideWindowUI)
    {
        parameters.put("viewer_hide_window_ui", Boolean.toString(viewerHideWindowUI));
        return this;
    }

    /**
     * Set PDF user password.
     * @param userPassword PDF user password.
     * @return Reference to the current object.
     */
    public PdfMergeClient setUserPassword(String userPassword)
    {
        parameters.put("user_password", userPassword);
        return this;
    }

    /**
     * Set PDF owner password.
     * @param ownerPassword PDF owner password.
     * @return Reference to the current object.
     */
    public PdfMergeClient setOwnerPassword(String ownerPassword)
    {
        parameters.put("owner_password", ownerPassword);
        return this;
    }

    /**
     * Set the maximum amount of time (in seconds) for this job. 
     * The default value is 30 seconds. Use a larger value (up to 120 seconds allowed) for large documents.
     * @param timeout Timeout in seconds.
     * @return Reference to the current object.
     */
    public PdfMergeClient setTimeout(int timeout)
    {
        parameters.put("timeout", Integer.toString(timeout));
        return this;
    }

    /**
     * Set a custom parameter. Do not use this method unless advised by SelectPdf.
     * @param parameterName Parameter name.
     * @param parameterValue Parameter value.
     * @return Reference to the current object.
     */
    public PdfMergeClient setCustomParameter(String parameterName, String parameterValue) {
        parameters.put(parameterName, parameterValue);
        return this;
    }

}
