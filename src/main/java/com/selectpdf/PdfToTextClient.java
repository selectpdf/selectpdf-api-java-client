package com.selectpdf;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Pdf To Text Conversion with SelectPdf Online API.
 * 
 * <pre>
 * {@code
package com.selectpdf;

public class PdfToText {
    public static void main(String[] args) throws Exception {
        String testUrl = "https://selectpdf.com/demo/files/selectpdf.pdf";
        String testPdf = "Input.pdf";
        String localFile = "Result.txt";
        String apiKey = "Your API key here";

        System.out.println(String.format("This is SelectPdf-%s.", ApiClient.CLIENT_VERSION));

        try {
            PdfToTextClient client = new PdfToTextClient(apiKey);

            // set parameters - see full list at https://selectpdf.com/pdf-to-text-api/
            client
                .setStartPage(1) // start page (processing starts from here)
                .setEndPage(0) // end page (set 0 to process file til the end)
                .setOutputFormat(ApiEnums.OutputFormat.Text) // set output format (0-Text or 1-HTML)
            ;

            System.out.println("Starting pdf to text...");

            // convert local pdf to local text file
            client.getTextFromFileToFile(testPdf, localFile);

            // extract text from local pdf to memory
            // String text = client.getTextFromFile(testPdf);
            // print text
            // System.out.println(text);

            // convert pdf from public url to local text file
            // client.getTextFromUrlToFile(testUrl, localFile);

            // extract text from pdf from public url to memory
            // String text = client.getTextFromUrl(testUrl);
            // print text
            // System.out.println(text);

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
public class PdfToTextClient extends ApiClient {
    /**
     * Construct the Pdf To Text Client.
     * @param apiKey API Key.
     */
    public PdfToTextClient(String apiKey)
    {
        apiEndpoint = "https://selectpdf.com/api2/pdftotext/";
        parameters.put("key", apiKey);
    }
    
    /**
     * Get the text from the specified pdf.
     * @param inputPdf Path to a local PDF file.
     * @return Extracted text.
     */
    public String getTextFromFile(String inputPdf) {
        parameters.put("async", "False");
        parameters.put("action", "Convert");
        parameters.remove("url");

        files.clear();
        files.put("inputPdf", inputPdf);

        byte[] result = performPostAsMultipartFormData(null);
        return new String(result, StandardCharsets.UTF_8);
    }
    
    /**
     * Get the text from the specified pdf and write it to the specified text file.
     * @param inputPdf Path to a local PDF file.
     * @param outputFilePath The output file where the resulted text will be written.
     */
    public void getTextFromFileToFile(String inputPdf, String outputFilePath) throws IOException  
    {
        String result = getTextFromFile(inputPdf);
        FileOutputStream outputFile = new FileOutputStream(outputFilePath);
        outputFile.write(result.getBytes("UTF-8"));
        outputFile.close();
    }

    /**
     * Get the text from the specified pdf and write it to the specified stream.
     * @param inputPdf Path to a local PDF file.
     * @param stream The output stream where the resulted PDF will be written.
     */
    public void getTextFromFileToStream(String inputPdf, OutputStream stream) throws IOException  
    {
        String result = getTextFromFile(inputPdf);
        stream.write(result.getBytes("UTF-8"));
    }    
    
    /**
     * Get the text from the specified pdf with an asynchronous call.
     * @param inputPdf Path to a local PDF file.
     * @return Extracted text.
     */
    public String getTextFromFileAsync(String inputPdf) {
        parameters.put("action", "Convert");
        parameters.remove("url");

        files.clear();
        files.put("inputPdf", inputPdf);

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

                files.clear();

                return new String(result, StandardCharsets.UTF_8);
            }

        } while (noPings <= AsyncCallsMaxPings);

        files.clear();
        throw new ApiException("Asynchronous call did not finish in expected timeframe.");    
    }
    
    /**
     * Get the text from the specified pdf with an asynchronous call and write it to the specified text file.
     * @param inputPdf Path to a local PDF file.
     * @param outputFilePath The output file where the resulted text will be written.
     */
    public void getTextFromFileToFileAsync(String inputPdf, String outputFilePath) throws IOException  
    {
        String result = getTextFromFileAsync(inputPdf);
        FileOutputStream outputFile = new FileOutputStream(outputFilePath);
        outputFile.write(result.getBytes("UTF-8"));
        outputFile.close();
    }

    /**
     * Get the text from the specified pdf with an asynchronous call and write it to the specified stream.
     * @param inputPdf Path to a local PDF file.
     * @param stream The output stream where the resulted PDF will be written.
     */
    public void getTextFromFileToStreamAsync(String inputPdf, OutputStream stream) throws IOException  
    {
        String result = getTextFromFileAsync(inputPdf);
        stream.write(result.getBytes("UTF-8"));
    }
    
    /**
     * Get the text from the specified pdf.
     * @param url Address of the PDF file.
     * @return Extracted text.
     */
    public String getTextFromUrl(String url) {
        if (!url.startsWith("http://", 0) && !url.startsWith("https://", 0))
        {
            throw new ApiException("The supported protocols for the PDFs available online are http:// and https://.");
        }
        if (url.startsWith("http://localhost", 0))
        {
            throw new ApiException("Cannot convert local urls via this method. Use getTextFromFile instead.");
        }

        parameters.put("async", "False");
        parameters.put("action", "Convert");
        parameters.put("url", url);

        files.clear();

        byte[] result = performPostAsMultipartFormData(null);
        return new String(result, StandardCharsets.UTF_8);
    }
    
    /**
     * Get the text from the specified pdf and write it to the specified text file.
     * @param url Address of the PDF file.
     * @param outputFilePath The output file where the resulted text will be written.
     */
    public void getTextFromUrlToFile(String url, String outputFilePath) throws IOException  
    {
        String result = getTextFromUrl(url);
        FileOutputStream outputFile = new FileOutputStream(outputFilePath);
        outputFile.write(result.getBytes("UTF-8"));
        outputFile.close();
    }

    /**
     * Get the text from the specified pdf and write it to the specified stream.
     * @param url Address of the PDF file.
     * @param stream The output stream where the resulted PDF will be written.
     */
    public void getTextFromUrlToStream(String url, OutputStream stream) throws IOException  
    {
        String result = getTextFromUrl(url);
        stream.write(result.getBytes("UTF-8"));
    }    
    
    /**
     * Get the text from the specified pdf with an asynchronous call.
     * @param url Address of the PDF file.
     * @return Extracted text.
     */
    public String getTextFromUrlAsync(String url) {
        if (!url.startsWith("http://", 0) && !url.startsWith("https://", 0))
        {
            throw new ApiException("The supported protocols for the PDFs available online are http:// and https://.");
        }
        if (url.startsWith("http://localhost", 0))
        {
            throw new ApiException("Cannot convert local urls via this method. Use getTextFromFileAsync instead.");
        }

        parameters.put("action", "Convert");
        parameters.put("url", url);

        files.clear();

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

                files.clear();

                return new String(result, StandardCharsets.UTF_8);
            }

        } while (noPings <= AsyncCallsMaxPings);

        files.clear();
        throw new ApiException("Asynchronous call did not finish in expected timeframe.");    
    }
    
    /**
     * Get the text from the specified pdf with an asynchronous call and write it to the specified text file.
     * @param url Address of the PDF file.
     * @param outputFilePath The output file where the resulted text will be written.
     */
    public void getTextFromUrlToFileAsync(String url, String outputFilePath) throws IOException  
    {
        String result = getTextFromUrlAsync(url);
        FileOutputStream outputFile = new FileOutputStream(outputFilePath);
        outputFile.write(result.getBytes("UTF-8"));
        outputFile.close();
    }

    /**
     * Get the text from the specified pdf with an asynchronous call and write it to the specified stream.
     * @param url Address of the PDF file.
     * @param stream The output stream where the resulted PDF will be written.
     */
    public void getTextFromUrlToStreamAsync(String url, OutputStream stream) throws IOException  
    {
        String result = getTextFromUrlAsync(url);
        stream.write(result.getBytes("UTF-8"));
    }

    
    /**
     * Search for a specific text in a PDF document. The search is case insensitive and returns partial words also.
     * Pages that participate to this operation are specified by setStartPage() and setEndPage() methods.
     * @param inputPdf Path to a local PDF file.
     * @param textToSearch Text to search.
     * @return List with text positions in the current PDF document.
     */
    public String searchFile(String inputPdf, String textToSearch)
    {
        return searchFile(inputPdf, textToSearch, false, false);
    }

    /**
     * Search for a specific text in a PDF document. 
     * Pages that participate to this operation are specified by setStartPage() and setEndPage() methods.
     * @param inputPdf Path to a local PDF file.
     * @param textToSearch Text to search.
     * @param caseSensitive If the search is case sensitive or not.
     * @param wholeWordsOnly If the search works on whole words or not.
     * @return List with text positions in the current PDF document.
     */
    public String searchFile(String inputPdf, String textToSearch, Boolean caseSensitive, Boolean wholeWordsOnly)
    {
        if (textToSearch == null || textToSearch.isBlank()) {
            throw new ApiException("Search text cannot be empty.");
        }

        parameters.put("async", "False");
        parameters.put("action", "Search");
        parameters.remove("url");
        parameters.put("search_text", textToSearch);
        parameters.put("case_sensitive", Boolean.toString(caseSensitive));
        parameters.put("whole_words_only", Boolean.toString(wholeWordsOnly));

        files.clear();
        files.put("inputPdf", inputPdf);

        headers.put("Accept", "text/json");

        try
        {
            byte[] result = performPostAsMultipartFormData(null);

            String textPositions = new String(result, StandardCharsets.UTF_8);
            return textPositions;
        }
        catch (Exception ex)
        {
            throw new ApiException("Could not get search results. " + ex.getMessage());
        }
    }

    /**
     * Search for a specific text in a PDF document with an asynchronous call. The search is case insensitive and returns partial words also.
     * Pages that participate to this operation are specified by setStartPage() and setEndPage() methods.
     * @param inputPdf Path to a local PDF file.
     * @param textToSearch Text to search.
     * @return List with text positions in the current PDF document.
     */
    public String searchFileAsync(String inputPdf, String textToSearch)
    {
        return searchFileAsync(inputPdf, textToSearch, false, false);
    }

    /**
     * Search for a specific text in a PDF document with an asynchronous call. 
     * Pages that participate to this operation are specified by setStartPage() and setEndPage() methods.
     * @param inputPdf Path to a local PDF file.
     * @param textToSearch Text to search.
     * @param caseSensitive If the search is case sensitive or not.
     * @param wholeWordsOnly If the search works on whole words or not.
     * @return List with text positions in the current PDF document.
     */
    public String searchFileAsync(String inputPdf, String textToSearch, Boolean caseSensitive, Boolean wholeWordsOnly)
    {
        if (textToSearch == null || textToSearch.isBlank()) {
            throw new ApiException("Search text cannot be empty.");
        }

        parameters.put("action", "Search");
        parameters.remove("url");
        parameters.put("search_text", textToSearch);
        parameters.put("case_sensitive", Boolean.toString(caseSensitive));
        parameters.put("whole_words_only", Boolean.toString(wholeWordsOnly));

        files.clear();
        files.put("inputPdf", inputPdf);

        headers.put("Accept", "text/json");

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

                files.clear();

                return new String(result, StandardCharsets.UTF_8);
            }

        } while (noPings <= AsyncCallsMaxPings);

        files.clear();
        throw new ApiException("Asynchronous call did not finish in expected timeframe.");    
    }

    /**
     * Search for a specific text in a PDF document. The search is case insensitive and returns partial words also.
     * Pages that participate to this operation are specified by setStartPage() and setEndPage() methods.
     * @param url Address of the PDF file.
     * @param textToSearch Text to search.
     * @return List with text positions in the current PDF document.
     */
    public String searchUrl(String url, String textToSearch)
    {
        return searchUrl(url, textToSearch, false, false);
    }

    /**
     * Search for a specific text in a PDF document. 
     * Pages that participate to this operation are specified by setStartPage() and setEndPage() methods.
     * @param url Address of the PDF file.
     * @param textToSearch Text to search.
     * @param caseSensitive If the search is case sensitive or not.
     * @param wholeWordsOnly If the search works on whole words or not.
     * @return List with text positions in the current PDF document.
     */
    public String searchUrl(String url, String textToSearch, Boolean caseSensitive, Boolean wholeWordsOnly)
    {
        if (!url.startsWith("http://", 0) && !url.startsWith("https://", 0))
        {
            throw new ApiException("The supported protocols for the PDFs available online are http:// and https://.");
        }
        if (url.startsWith("http://localhost", 0))
        {
            throw new ApiException("Cannot convert local urls via this method. Use searchFile instead.");
        }

        if (textToSearch == null || textToSearch.isBlank()) {
            throw new ApiException("Search text cannot be empty.");
        }

        parameters.put("async", "False");
        parameters.put("action", "Search");
        parameters.put("url", url);
        parameters.put("search_text", textToSearch);
        parameters.put("case_sensitive", Boolean.toString(caseSensitive));
        parameters.put("whole_words_only", Boolean.toString(wholeWordsOnly));

        files.clear();

        headers.put("Accept", "text/json");

        try
        {
            byte[] result = performPostAsMultipartFormData(null);

            String textPositions = new String(result, StandardCharsets.UTF_8);
            return textPositions;
        }
        catch (Exception ex)
        {
            throw new ApiException("Could not get search results. " + ex.getMessage());
        }
    }

    /**
     * Search for a specific text in a PDF document with an asynchronous call. The search is case insensitive and returns partial words also.
     * Pages that participate to this operation are specified by setStartPage() and setEndPage() methods.
     * @param url Address of the PDF file.
     * @param textToSearch Text to search.
     * @return List with text positions in the current PDF document.
     */
    public String searchUrlAsync(String url, String textToSearch)
    {
        return searchUrlAsync(url, textToSearch, false, false);
    }

    /**
     * Search for a specific text in a PDF document with an asynchronous call. 
     * Pages that participate to this operation are specified by setStartPage() and setEndPage() methods.
     * @param url Address of the PDF file.
     * @param textToSearch Text to search.
     * @param caseSensitive If the search is case sensitive or not.
     * @param wholeWordsOnly If the search works on whole words or not.
     * @return List with text positions in the current PDF document.
     */
    public String searchUrlAsync(String url, String textToSearch, Boolean caseSensitive, Boolean wholeWordsOnly)
    {
        if (!url.startsWith("http://", 0) && !url.startsWith("https://", 0))
        {
            throw new ApiException("The supported protocols for the PDFs available online are http:// and https://.");
        }
        if (url.startsWith("http://localhost", 0))
        {
            throw new ApiException("Cannot convert local urls via this method. Use searchFileAsync instead.");
        }

        if (textToSearch == null || textToSearch.isBlank()) {
            throw new ApiException("Search text cannot be empty.");
        }

        parameters.put("action", "Search");
        parameters.put("url", url);
        parameters.put("search_text", textToSearch);
        parameters.put("case_sensitive", Boolean.toString(caseSensitive));
        parameters.put("whole_words_only", Boolean.toString(wholeWordsOnly));

        files.clear();

        headers.put("Accept", "text/json");

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

                files.clear();

                return new String(result, StandardCharsets.UTF_8);
            }

        } while (noPings <= AsyncCallsMaxPings);

        files.clear();
        throw new ApiException("Asynchronous call did not finish in expected timeframe.");    
    }

    /**
     * Set Start Page number. Default value is 1 (first page of the document).
     * @param startPage Start page number (1-based).
     * @return Reference to the current object.
     */
    public PdfToTextClient setStartPage(int startPage)
    {
        parameters.put("start_page", Integer.toString((startPage)));
        return this;
    }

    /**
     * Set End Page number. Default value is 0 (process till the last page of the document).
     * @param endPage End page number (1-based).
     * @return Reference to the current object.
     */
    public PdfToTextClient setEndPage(int endPage)
    {
        parameters.put("end_page", Integer.toString((endPage)));
        return this;
    }

    /**
     * Set PDF user password.
     * @param userPassword PDF user password.
     * @return Reference to the current object.
     */
    public PdfToTextClient setUserPassword(String userPassword)
    {
        parameters.put("user_password", userPassword);
        return this;
    }

    /**
     * Set the text layout. The default value is TextLayout.Original.
     * @param textLayout The text layout.
     * @return Reference to the current object.
     */
    public PdfToTextClient setTextLayout(ApiEnums.TextLayout textLayout)
    {
        parameters.put("text_layout", textLayout.getValueAsString());
        return this;
    }

    /**
     * Set the output format. The default value is OutputFormat.Text.
     * @param outputFormat The output format.
     * @return Reference to the current object.
     */
    public PdfToTextClient setOutputFormat(ApiEnums.OutputFormat outputFormat)
    {
        parameters.put("output_format", outputFormat.getValueAsString());
        return this;
    }

    /**
     * Set the maximum amount of time (in seconds) for this job. 
     * The default value is 30 seconds. Use a larger value (up to 120 seconds allowed) for large documents.
     * @param timeout Timeout in seconds.
     * @return Reference to the current object.
     */
    public PdfToTextClient setTimeout(int timeout)
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
    public PdfToTextClient setCustomParameter(String parameterName, String parameterValue) {
        parameters.put(parameterName, parameterValue);
        return this;
    }

}
