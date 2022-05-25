package com.selectpdf;

import java.io.*;
import java.util.*;

/**
 * Html To Pdf Conversion with SelectPdf Online API.
 * 
 * <pre>
 * {@code
package com.selectpdf;

public class HtmlToPdfMain {
    public static void main(String[] args) throws Exception {
        String url = "https://selectpdf.com";
        String localFile = "Test.pdf";
        String apiKey = "Your API key here";

        System.out.println(String.format("This is SelectPdf-%s.", ApiClient.CLIENT_VERSION));

        try {
            HtmlToPdfClient client = new HtmlToPdfClient(apiKey);

            // set parameters - see full list at https://selectpdf.com/html-to-pdf-api/
            client
                // main properties

                .setPageSize(ApiEnums.PageSize.A4) // PDF page size
                .setPageOrientation(ApiEnums.PageOrientation.Portrait) // PDF page orientation
                .setMargins(0) // PDF page margins
                .setRenderingEngine(ApiEnums.RenderingEngine.WebKit) // rendering engine
                .setConversionDelay(1) // conversion delay
                .setNavigationTimeout(30) // navigation timeout 
                .setShowPageNumbers(false) // page numbers
                .setPageBreaksEnhancedAlgorithm(true) // enhanced page break algorithm

                // additional properties

                // .setUseCssPrint(true) // enable CSS media print
                // .setDisableJavascript(true) // disable javascript
                // .setDisableInternalLinks(true) // disable internal links
                // .setDisableExternalLinks(true) // disable external links
                // .setKeepImagesTogether(true) // keep images together
                // .setScaleImages(true) // scale images to create smaller pdfs
                // .setSinglePagePdf(true) // generate a single page PDF
                // .setUserPassword("password") // secure the PDF with a password

                // generate automatic bookmarks

                // .setPdfBookmarksSelectors("H1, H2") // create outlines (bookmarks) for the specified elements
                // .setViewerPageMode(ApiEnums.PageMode.UseOutlines) // display outlines (bookmarks) in viewer
            ;

            System.out.println("Starting conversion...");

            // convert url to file
            client.convertUrlToFile(url, localFile);

            // convert url to memory
            // byte[] pdf = client.convertUrl(url);

            // convert html string to file
            // client.convertHtmlStringToFile("This is some <b>html</b>.", localFile);

            // convert html string to memory
            // byte[] pdf = client.convertHtmlString("This is some <b>html</b>.");

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
public class HtmlToPdfClient extends ApiClient {
    /**
     * Construct the Html To Pdf Client.
     * @param apiKey API key.
     */
    public HtmlToPdfClient(String apiKey) {
        apiEndpoint = "https://selectpdf.com/api2/convert/";
        parameters.put("key", apiKey);
    }

    /**
     * Convert the specified url to PDF. SelectPdf online API can convert http:// and https:// publicly available urls. 
     * @param url Address of the web page being converted.
     * @return Byte array containing the resulted PDF.
     */
    public byte[] convertUrl(String url)
    {
        if (!url.startsWith("http://", 0) && !url.startsWith("https://", 0))
        {
            throw new ApiException("The supported protocols for the converted webpage are http:// and https://.");
        }
        if (url.startsWith("http://localhost", 0))
        {
            throw new ApiException("Cannot convert local urls. SelectPdf online API can only convert publicly available urls.");
        }
        parameters.put("url", url);
        parameters.put("html", "");
        parameters.put("base_url", "");
        parameters.put("async", "False");

        return performPost(null);
    }

    /**
     * Convert the specified url to PDF and writes the resulted PDF to an output stream. SelectPdf online API can convert http:// and https:// publicly available urls.
     * @param url Address of the web page being converted.
     * @param stream The output stream where the resulted PDF will be written.
     */
    public void convertUrlToStream(String url, OutputStream stream)
    {
        if (!url.startsWith("http://", 0) && !url.startsWith("https://", 0))
        {
            throw new ApiException("The supported protocols for the converted webpage are http:// and https://.");
        }
        if (url.startsWith("http://localhost", 0))
        {
            throw new ApiException("Cannot convert local urls. SelectPdf online API can only convert publicly available urls.");
        }
        parameters.put("url", url);
        parameters.put("html", "");
        parameters.put("base_url", "");
        parameters.put("async", "False");

        performPost(stream);
    }

    /**
     * Convert the specified url to PDF and writes the resulted PDF to a local file. SelectPdf online API can convert http:// and https:// publicly available urls.
     * @param url Address of the web page being converted.
     * @param filePath Local file including path if necessary.
     * @throws IOException
     */
    public void convertUrlToFile(String url, String filePath) throws IOException
    {
        FileOutputStream outputFile = new FileOutputStream(filePath);

        try {
            convertUrlToStream(url, outputFile);
            outputFile.close();
        }
        catch(ApiException ex) {
            outputFile.close();
            new File(filePath).delete();
            throw ex;
        }

    }

    /**
     * Convert the specified url to PDF using an asynchronous call. 
     * SelectPdf online API can convert http:// and https:// publicly available urls. 
     * @param url Address of the web page being converted.
     * @return Byte array containing the resulted PDF.
     */
    public byte[] convertUrlAsync(String url)
    {
        if (!url.startsWith("http://", 0) && !url.startsWith("https://", 0))
        {
            throw new ApiException("The supported protocols for the converted webpage are http:// and https://.");
        }
        if (url.startsWith("http://localhost", 0))
        {
            throw new ApiException("Cannot convert local urls. SelectPdf online API can only convert publicly available urls.");
        }
        parameters.put("url", url);
        parameters.put("html", "");
        parameters.put("base_url", "");

        String JobID = startAsyncJob();

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

                return result;
            }

        } while (noPings <= AsyncCallsMaxPings);

        throw new ApiException("Asynchronous call did not finish in expected timeframe.");    
    }

    /**
     * Convert the specified url to PDF using an asynchronous call and writes the resulted PDF to an output stream. 
     * SelectPdf online API can convert http:// and https:// publicly available urls.
     * @param url Address of the web page being converted.
     * @param stream The output stream where the resulted PDF will be written.
     */
    public void convertUrlToStreamAsync(String url, OutputStream stream) throws IOException
    {
        byte[] result = convertUrlAsync(url);
        stream.write(result);
    }

    /**
     * Convert the specified url to PDF using an asynchronous call and writes the resulted PDF to a local file. 
     * SelectPdf online API can convert http:// and https:// publicly available urls.
     * @param url Address of the web page being converted.
     * @param filePath Local file including path if necessary.
     * @throws IOException
     */
    public void convertUrlToFileAsync(String url, String filePath) throws IOException
    {
        FileOutputStream outputFile = new FileOutputStream(filePath);

        try {
            byte[] result = convertUrlAsync(url);
            outputFile.write(result);
            outputFile.close();
        }
        catch(ApiException ex) {
            outputFile.close();
            new File(filePath).delete();
            throw ex;
        }

    }

    /**
     * Convert the specified HTML string to PDF.
     * @param htmlString HTML string with the content being converted.
     * @return Byte array containing the resulted PDF.
     */
    public byte[] convertHtmlString(String htmlString)
    {
        return convertHtmlString(htmlString, "");
    }

    /**
     * Convert the specified HTML string to PDF. Use a base url to resolve relative paths to resources.
     * @param htmlString HTML string with the content being converted.
     * @param baseUrl Base url used to resolve relative paths to resources (css, images, javascript, etc). Must be a http:// or https:// publicly available url.
     * @return Byte array containing the resulted PDF.
     */
    public byte[] convertHtmlString(String htmlString, String baseUrl)
    {
        parameters.put("html", htmlString);
        parameters.put("url", "");
        parameters.put("async", "False");

        if (baseUrl != null && !baseUrl.isBlank())
        {
            parameters.put("base_url", baseUrl);
        }

        return performPost(null);
    }

    /**
     * Convert the specified HTML string to PDF and writes the resulted PDF to an output stream.
     * @param htmlString HTML string with the content being converted.
     * @param stream The output stream where the resulted PDF will be written.
     */
    public void convertHtmlStringToStream(String htmlString, OutputStream stream)
    {
        convertHtmlStringToStream(htmlString, "", stream);
    }

    /**
     * Convert the specified HTML string to PDF and writes the resulted PDF to an output stream. Use a base url to resolve relative paths to resources.
     * @param htmlString HTML string with the content being converted.
     * @param baseUrl Base url used to resolve relative paths to resources (css, images, javascript, etc). Must be a http:// or https:// publicly available url.
     * @param stream The output stream where the resulted PDF will be written.
     */
    public void convertHtmlStringToStream(String htmlString, String baseUrl, OutputStream stream)
    {
        parameters.put("html", htmlString);
        parameters.put("url", "");
        parameters.put("async", "False");

        if (baseUrl != null && !baseUrl.isBlank())
        {
            parameters.put("base_url", baseUrl);
        }

        performPost(stream);
    }

    /**
     * Convert the specified HTML string to PDF and writes the resulted PDF to a local file.
     * @param htmlString HTML string with the content being converted.
     * @param filePath Local file including path if necessary.
     * @throws IOException
     */
    public void convertHtmlStringToFile(String htmlString, String filePath) throws IOException
    {
        convertHtmlStringToFile(htmlString, "", filePath);
    }

    /**
     * Convert the specified HTML string to PDF and writes the resulted PDF to a local file. Use a base url to resolve relative paths to resources.
     * @param htmlString HTML string with the content being converted.
     * @param baseUrl Base url used to resolve relative paths to resources (css, images, javascript, etc). Must be a http:// or https:// publicly available url.
     * @param filePath Local file including path if necessary.
     * @throws IOException
     */
    public void convertHtmlStringToFile(String htmlString, String baseUrl, String filePath) throws IOException
    {
        parameters.put("html", htmlString);
        parameters.put("url", "");
        parameters.put("async", "False");

        if (baseUrl != null && !baseUrl.isBlank())
        {
            parameters.put("base_url", baseUrl);
        }

        FileOutputStream outputFile = new FileOutputStream(filePath);

        try
        {
            performPost(outputFile);
            outputFile.close();
        }
        catch(ApiException ex) {
            outputFile.close();
            new File(filePath).delete();
            throw ex;
        }
    }

    /**
     * Convert the specified HTML string to PDF with an asynchronous call.
     * @param htmlString HTML string with the content being converted.
     * @return Byte array containing the resulted PDF.
     */
    public byte[] convertHtmlStringAsync(String htmlString)
    {
        return convertHtmlStringAsync(htmlString, "");
    }

    /**
     * Convert the specified HTML string to PDF with an asynchronous call. Use a base url to resolve relative paths to resources.
     * @param htmlString HTML string with the content being converted.
     * @param baseUrl Base url used to resolve relative paths to resources (css, images, javascript, etc). Must be a http:// or https:// publicly available url.
     * @return Byte array containing the resulted PDF.
     */
    public byte[] convertHtmlStringAsync(String htmlString, String baseUrl)
    {
        parameters.put("html", htmlString);
        parameters.put("url", "");

        if (baseUrl != null && !baseUrl.isBlank())
        {
            parameters.put("base_url", baseUrl);
        }

        String JobID = startAsyncJob();

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

                return result;
            }

        } while (noPings <= AsyncCallsMaxPings);

        throw new ApiException("Asynchronous call did not finish in expected timeframe.");    
    }

    /**
     * Convert the specified HTML string to PDF with an asynchronous call and writes the resulted PDF to an output stream.
     * @param htmlString HTML string with the content being converted.
     * @param stream The output stream where the resulted PDF will be written.
     */
    public void convertHtmlStringToStreamAsync(String htmlString, OutputStream stream) throws IOException
    {
        convertHtmlStringToStreamAsync(htmlString, "", stream);
    }

    /**
     * Convert the specified HTML string to PDF with an asynchronous call and writes the resulted PDF to an output stream. Use a base url to resolve relative paths to resources.
     * @param htmlString HTML string with the content being converted.
     * @param baseUrl Base url used to resolve relative paths to resources (css, images, javascript, etc). Must be a http:// or https:// publicly available url.
     * @param stream The output stream where the resulted PDF will be written.
     */
    public void convertHtmlStringToStreamAsync(String htmlString, String baseUrl, OutputStream stream) throws IOException
    {
        byte[] result = convertHtmlStringAsync(htmlString, baseUrl);
        stream.write(result);
    }

    /**
     * Convert the specified HTML string to PDF with an asynchronous call and writes the resulted PDF to a local file.
     * @param htmlString HTML string with the content being converted.
     * @param filePath Local file including path if necessary.
     * @throws IOException
     */
    public void convertHtmlStringToFileAsync(String htmlString, String filePath) throws IOException
    {
        convertHtmlStringToFileAsync(htmlString, "", filePath);
    }

    /**
     * Convert the specified HTML string to PDF with an asynchronous call and writes the resulted PDF to a local file. Use a base url to resolve relative paths to resources.
     * @param htmlString HTML string with the content being converted.
     * @param baseUrl Base url used to resolve relative paths to resources (css, images, javascript, etc). Must be a http:// or https:// publicly available url.
     * @param filePath Local file including path if necessary.
     * @throws IOException
     */
    public void convertHtmlStringToFileAsync(String htmlString, String baseUrl, String filePath) throws IOException
    {
        FileOutputStream outputFile = new FileOutputStream(filePath);

        try {
            byte[] result = convertHtmlStringAsync(htmlString, baseUrl);
            outputFile.write(result);
            outputFile.close();
        }
        catch(ApiException ex) {
            outputFile.close();
            new File(filePath).delete();
            throw ex;
        }
    }

    /**
     * Set PDF page size. Default value is A4. If page size is set to Custom, use setPageWidth and setPageHeight methods to set the custom width/height of the PDF pages.
     * @param pageSize PDF page size.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setPageSize(ApiEnums.PageSize pageSize)
    {
        parameters.put("page_size", pageSize.toString());
        return this;
    }

    /**
     * Set PDF page width in points. Default value is 595pt (A4 page width in points). 1pt = 1/72 inch. 
     * This is taken into account only if page size is set to Custom using setPageSize method.
     * @param pageWidth Page width in points.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setPageWidth(int pageWidth)
    {
        parameters.put("page_width", Integer.toString(pageWidth));
        return this;
    }
    
    /**
     * Set PDF page height in points. Default value is 842pt (A4 page height in points). 1pt = 1/72 inch.
     * This is taken into account only if page size is set to Custom using setPageSize method.
     * @param pageHeight Page height in points.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setPageHeight(int pageHeight)
    {
        parameters.put("page_height", Integer.toString(pageHeight));
        return this;
    }

    /**
     * Set PDF page orientation. Default value is Portrait.
     * @param pageOrientation PDF page orientation.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setPageOrientation(ApiEnums.PageOrientation pageOrientation)
    {
        parameters.put("page_orientation", pageOrientation.toString());
        return this;
    }

    /**
     * Set top margin of the PDF pages. Default value is 5pt.
     * @param marginTop Margin value in points. 1pt = 1/72 inch.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setMarginTop(int marginTop)
    {
        parameters.put("margin_top", Integer.toString(marginTop));
        return this;
    }

    /**
     * Set right margin of the PDF pages. Default value is 5pt.
     * @param marginRight Margin value in points. 1pt = 1/72 inch.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setMarginRight(int marginRight)
    {
        parameters.put("margin_right", Integer.toString(marginRight));
        return this;
    }

    /**
     * Set bottom margin of the PDF pages. Default value is 5pt.
     * @param marginBottom Margin value in points. 1pt = 1/72 inch.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setMarginBottom(int marginBottom)
    {
        parameters.put("margin_bottom", Integer.toString(marginBottom));
        return this;
    }

    /**
     * Set left margin of the PDF pages. Default value is 5pt.
     * @param marginLeft Margin value in points. 1pt = 1/72 inch.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setMarginLeft(int marginLeft)
    {
        parameters.put("margin_left", Integer.toString(marginLeft));
        return this;
    }

    /**
     * Set all margins of the PDF pages to the same value. Default value is 5pt.
     * @param margin Margin value in points. 1pt = 1/72 inch.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setMargins(int margin)
    {
        return setMarginTop(margin).setMarginRight(margin).setMarginBottom(margin).setMarginLeft(margin);
    }

    /**
     * Specify the name of the pdf document that will be created. The default value is Document.pdf.
     * @param pdfName Name of the generated PDF document.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setPdfName(String pdfName)
    {
        parameters.put("pdf_name", pdfName);
        return this;
    }

    /**
     * Set the rendering engine used for the HTML to PDF conversion. Default value is WebKit.
     * @param renderingEngine HTML rendering engine.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setRenderingEngine(ApiEnums.RenderingEngine renderingEngine)
    {
        parameters.put("engine", renderingEngine.toString());
        return this;
    }

    /**
     * Set PDF user password.
     * @param userPassword PDF user password.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setUserPassword(String userPassword)
    {
        parameters.put("user_password", userPassword);
        return this;
    }

    /**
     * Set PDF owner password.
     * @param ownerPassword PDF owner password.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setOwnerPassword(String ownerPassword)
    {
        parameters.put("owner_password", ownerPassword);
        return this;
    }

    /**
     * Set the width used by the converter's internal browser window in pixels. The default value is 1024px.
     * @param webPageWidth Browser window width in pixels.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setWebPageWidth(int webPageWidth)
    {
        parameters.put("web_page_width", Integer.toString(webPageWidth));
        return this;
    }    

    /**
     * Set the height used by the converter's internal browser window in pixels. The default value is 0px and it means that the page height is automatically calculated by the converter.
     * @param webPageHeight Browser window height in pixels. Set it to 0px to automatically calculate page height.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setWebPageHeight(int webPageHeight)
    {
        parameters.put("web_page_height", Integer.toString(webPageHeight));
        return this;
    }

    /**
     * Introduce a delay (in seconds) before the actual conversion to allow the web page to fully load. 
     * This method is an alias for setConversionDelay. The default value is 1 second. 
     * Use a larger value if the web page has content that takes time to render when it is displayed in the browser.
     * @param minLoadTime Delay in seconds.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setMinLoadTime(int minLoadTime)
    {
        parameters.put("min_load_time", Integer.toString(minLoadTime));
        return this;
    }

    /**
     * Introduce a delay (in seconds) before the actual conversion to allow the web page to fully load.
     * This method is an alias for setMinLoadTime. The default value is 1 second. 
     * Use a larger value if the web page has content that takes time to render when it is displayed in the browser.
     * @param delay Delay in seconds.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setConversionDelay(int delay)
    {
        return setMinLoadTime(delay);
    }    

    /**
     * Set the maximum amount of time (in seconds) that the convert will wait for the page to load.
     * This method is an alias for setNavigationTimeout. A timeout error is displayed when this time elapses.
     * The default value is 30 seconds. Use a larger value (up to 120 seconds allowed) for pages that take a long time to load.
     * @param maxLoadTime Timeout in seconds.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setMaxLoadTime(int maxLoadTime)
    {
        parameters.put("max_load_time", Integer.toString(maxLoadTime));
        return this;
    }

    /**
     * Set the maximum amount of time (in seconds) that the convert will wait for the page to load.
     * This method is an alias for setMaxLoadTime. A timeout error is displayed when this time elapses.
     * The default value is 30 seconds. Use a larger value (up to 120 seconds allowed) for pages that take a long time to load.
     * @param timeout Timeout in seconds.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setNavigationTimeout(int timeout)
    {
        return setMaxLoadTime(timeout);
    }

    /**
     * Set the protocol used for secure (HTTPS) connections.
     * Set this only if you have an older server that only works with older SSL connections.
     * @param secureProtocol Secure protocol.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setSecureProtocol(ApiEnums.SecureProtocol secureProtocol)
    {
        parameters.put("protocol", secureProtocol.getValueAsString());
        return this;
    }

    /**
     * Specify if the CSS Print media type is used instead of the Screen media type. The default value is False.
     * @param useCssPrint Use CSS Print media or not.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setUseCssPrint(Boolean useCssPrint)
    {
        parameters.put("use_css_print", Boolean.toString(useCssPrint));
        return this;
    }

    /**
     * Specify the background color of the PDF page in RGB html format. The default is #FFFFFF.
     * @param backgroundColor Background color in #RRGGBB format.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setBackgroundColor(String backgroundColor)
    {
        if (!backgroundColor.matches("^#?[0-9a-fA-F]{6}$"))
            throw new ApiException("Color value must be in #RRGGBB format.");

        parameters.put("background_color", backgroundColor);
        return this;
    }

    /**
     * Set a flag indicating if the web page background is rendered in PDF. The default value is True.
     * @param drawHtmlBackground Draw the HTML background or not.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setDrawHtmlBackground(Boolean drawHtmlBackground)
    {
        parameters.put("draw_html_background", Boolean.toString(drawHtmlBackground));
        return this;
    }

    /**
     * Do not run JavaScript in web pages. The default value is False and javascript is executed.
     * @param disableJavascript Disable javascript or not.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setDisableJavascript(Boolean disableJavascript)
    {
        parameters.put("disable_javascript", Boolean.toString(disableJavascript));
        return this;
    }

    /**
     * Do not create internal links in the PDF. The default value is False and internal links are created.
     * @param disableInternalLinks Disable internal links or not.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setDisableInternalLinks(Boolean disableInternalLinks)
    {
        parameters.put("disable_internal_links", Boolean.toString(disableInternalLinks));
        return this;
    }

    /**
     * Do not create external links in the PDF. The default value is False and external links are created.
     * @param disableExternalLinks Disable external links or not.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setDisableExternalLinks(Boolean disableExternalLinks)
    {
        parameters.put("disable_external_links", Boolean.toString(disableExternalLinks));
        return this;
    }

    /**
     * Try to render the PDF even in case of the web page loading timeout. The default value is False and an exception is raised in case of web page navigation timeout.
     * @param renderOnTimeout Render in case of timeout or not.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setRenderOnTimeout(Boolean renderOnTimeout)
    {
        parameters.put("render_on_timeout", Boolean.toString(renderOnTimeout));
        return this;
    }

    /**
     * Avoid breaking images between PDF pages. The default value is False and images are split between pages if larger.
     * @param keepImagesTogether Try to keep images on same page or not.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setKeepImagesTogether(Boolean keepImagesTogether)
    {
        parameters.put("keep_images_together", Boolean.toString(keepImagesTogether));
        return this;
    }

    /**
     * Set the PDF document title.
     * @param docTitle Document title.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setDocTitle(String docTitle)
    {
        parameters.put("doc_title", docTitle);
        return this;
    }

    /**
     * Set the subject of the PDF document.
     * @param docSubject Document subject.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setDocSubject(String docSubject)
    {
        parameters.put("doc_subject", docSubject);
        return this;
    }

    /**
     * Set the PDF document keywords.
     * @param docKeywords Document keywords.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setDocKeywords(String docKeywords)
    {
        parameters.put("doc_keywords", docKeywords);
        return this;
    }

    /**
     * Set the name of the PDF document author.
     * @param docAuthor Document author.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setDocAuthor(String docAuthor)
    {
        parameters.put("doc_author", docAuthor);
        return this;
    }

    /**
     * Add the date and time when the PDF document was created to the PDF document information. The default value is False.
     * @param docAddCreationDate Add creation date to the document metadata or not.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setDocAddCreationDate(Boolean docAddCreationDate)
    {
        parameters.put("doc_add_creation_date", Boolean.toString(docAddCreationDate));
        return this;
    }

    /**
     * Set the page layout to be used when the document is opened in a PDF viewer. The default value is PageLayout.OneColumn.
     * @param pageLayout Page layout.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setViewerPageLayout(ApiEnums.PageLayout pageLayout)
    {
        parameters.put("viewer_page_layout", pageLayout.getValueAsString());
        return this;
    }

    /**
     * Set the document page mode when the pdf document is opened in a PDF viewer. The default value is PageMode.UseNone.
     * @param pageMode Page mode.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setViewerPageMode(ApiEnums.PageMode pageMode)
    {
        parameters.put("viewer_page_mode", pageMode.getValueAsString());
        return this;
    }

    /**
     * Set a flag specifying whether to position the document's window in the center of the screen. The default value is False.
     * @param viewerCenterWindow Center window or not.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setViewerCenterWindow(Boolean viewerCenterWindow)
    {
        parameters.put("viewer_center_window", Boolean.toString(viewerCenterWindow));
        return this;
    }

    /**
     * Set a flag specifying whether the window's title bar should display the document title taken from document information. The default value is False.
     * @param viewerDisplayDocTitle Display title or not.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setViewerDisplayDocTitle(Boolean viewerDisplayDocTitle)
    {
        parameters.put("viewer_display_doc_title", Boolean.toString(viewerDisplayDocTitle));
        return this;
    }

    /**
     * Set a flag specifying whether to resize the document's window to fit the size of the first displayed page. The default value is False.
     * @param viewerFitWindow Fit window or not.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setViewerFitWindow(Boolean viewerFitWindow)
    {
        parameters.put("viewer_fit_window", Boolean.toString(viewerFitWindow));
        return this;
    }

    /**
     * Set a flag specifying whether to hide the pdf viewer application's menu bar when the document is active. The default value is False.
     * @param viewerHideMenuBar Hide menu bar or not.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setViewerHideMenuBar(Boolean viewerHideMenuBar)
    {
        parameters.put("viewer_hide_menu_bar", Boolean.toString(viewerHideMenuBar));
        return this;
    }

    /**
     * Set a flag specifying whether to hide the pdf viewer application's tool bars when the document is active. The default value is False.
     * @param viewerHideToolbar Hide tool bars or not.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setViewerHideToolbar(Boolean viewerHideToolbar)
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
    public HtmlToPdfClient setViewerHideWindowUI(Boolean viewerHideWindowUI)
    {
        parameters.put("viewer_hide_window_ui", Boolean.toString(viewerHideWindowUI));
        return this;
    }

    /**
     * Control if a custom header is displayed in the generated PDF document. The default value is False.
     * @param showHeader Show header or not.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setShowHeader(Boolean showHeader)
    {
        parameters.put("show_header", Boolean.toString(showHeader));
        return this;
    }

    /**
     * The height of the pdf document header. This height is specified in points. 1 point is 1/72 inch. The default value is 50.
     * @param height Header height.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setHeaderHeight(int height)
    {
        parameters.put("header_height", Integer.toString(height));
        return this;
    }

    /**
     * Set the url of the web page that is converted and rendered in the PDF document header.
     * @param url The url of the web page that is converted and rendered in the pdf document header.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setHeaderUrl(String url)
    {
        if (!url.startsWith("http://", 0) && !url.startsWith("https://", 0))
        {
            throw new ApiException("The supported protocols for the url are http:// and https://.");
        }
        if (url.startsWith("http://localhost", 0))
        {
            throw new ApiException("Cannot convert local urls. SelectPdf online API can only convert publicly available urls.");
        }


        parameters.put("header_url", url);
        return this;
    }

    /**
     * Set the raw html that is converted and rendered in the pdf document header.
     * @param html The raw html that is converted and rendered in the pdf document header.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setHeaderHtml(String html)
    {
        parameters.put("header_html", html);
        return this;
    }

    /**
     * Set an optional base url parameter can be used together with the header HTML to resolve relative paths from the html string.
     * @param baseUrl Header base url.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setHeaderBaseUrl(String baseUrl)
    {
        if (!baseUrl.startsWith("http://", 0) && !baseUrl.startsWith("https://", 0))
        {
            throw new ApiException("The supported protocols for the base url are http:// and https://.");
        }
        if (baseUrl.startsWith("http://localhost", 0))
        {
            throw new ApiException("Cannot convert local urls. SelectPdf online API can only convert publicly available urls.");
        }

        parameters.put("header_base_url", baseUrl);
        return this;
    }

    /**
     * Control the visibility of the header on the first page of the generated pdf document. The default value is True.
     * @param displayOnFirstPage Display header on the first page or not.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setHeaderDisplayOnFirstPage(Boolean displayOnFirstPage)
    {
        parameters.put("header_display_on_first_page", Boolean.toString(displayOnFirstPage));
        return this;
    }

    /**
     * Control the visibility of the header on the odd numbered pages of the generated pdf document. The default value is True.
     * @param displayOnOddPages Display header on odd pages or not.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setHeaderDisplayOnOddPages(Boolean displayOnOddPages)
    {
        parameters.put("header_display_on_odd_pages", Boolean.toString(displayOnOddPages));
        return this;
    }

    /**
     * Control the visibility of the header on the even numbered pages of the generated pdf document. The default value is True.
     * @param displayOnEvenPages Display header on even pages or not.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setHeaderDisplayOnEvenPages(Boolean displayOnEvenPages)
    {
        parameters.put("header_display_on_even_pages", Boolean.toString(displayOnEvenPages));
        return this;
    }

    /**
     * Set the width in pixels used by the converter's internal browser window during the conversion of the header content. The default value is 1024px.
     * @param headerWebPageWidth Browser window width in pixels.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setHeaderWebPageWidth(int headerWebPageWidth)
    {
        parameters.put("header_web_page_width", Integer.toString(headerWebPageWidth));
        return this;
    }

    /**
     * Set the height in pixels used by the converter's internal browser window during the conversion of the header content.
     * The default value is 0px and it means that the page height is automatically calculated by the converter.
     * @param headerWebPageHeight Browser window height in pixels. Set it to 0px to automatically calculate page height.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setHeaderWebPageHeight(int headerWebPageHeight)
    {
        parameters.put("header_web_page_height", Integer.toString(headerWebPageHeight));
        return this;
    }
    
    /**
     * Control if a custom footer is displayed in the generated PDF document. The default value is False.
     * @param showFooter Show footer or not.
     * @return Reference to the current object.
     */
     public HtmlToPdfClient setShowFooter(Boolean showFooter)
    {
        parameters.put("show_footer", Boolean.toString(showFooter));
        return this;
    }

    /**
     * The height of the pdf document footer. This height is specified in points. 1 point is 1/72 inch. The default value is 50.
     * @param height Footer height.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setFooterHeight(int height)
    {
        parameters.put("footer_height", Integer.toString(height));
        return this;
    }

    /**
     * Set the url of the web page that is converted and rendered in the PDF document footer.
     * @param url The url of the web page that is converted and rendered in the pdf document footer.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setFooterUrl(String url)
    {
        if (!url.startsWith("http://", 0) && !url.startsWith("https://", 0))
        {
            throw new ApiException("The supported protocols for the url are http:// and https://.");
        }
        if (url.startsWith("http://localhost", 0))
        {
            throw new ApiException("Cannot convert local urls. SelectPdf online API can only convert publicly available urls.");
        }

        parameters.put("footer_url", url);
        return this;
    }

    /**
     * Set the raw html that is converted and rendered in the pdf document footer.
     * @param html The raw html that is converted and rendered in the pdf document footer.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setFooterHtml(String html)
    {
        parameters.put("footer_html", html);
        return this;
    }

    /**
     * Set an optional base url parameter can be used together with the footer HTML to resolve relative paths from the html string.
     * @param baseUrl Footer base url.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setFooterBaseUrl(String baseUrl)
    {
        if (!baseUrl.startsWith("http://", 0) && !baseUrl.startsWith("https://", 0))
        {
            throw new ApiException("The supported protocols for the base url are http:// and https://.");
        }
        if (baseUrl.startsWith("http://localhost", 0))
        {
            throw new ApiException("Cannot convert local urls. SelectPdf online API can only convert publicly available urls.");
        }

        parameters.put("footer_base_url", baseUrl);
        return this;
    }

    /**
     * Control the visibility of the footer on the first page of the generated pdf document. The default value is True.
     * @param displayOnFirstPage Display footer on the first page or not.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setFooterDisplayOnFirstPage(Boolean displayOnFirstPage)
    {
        parameters.put("footer_display_on_first_page", Boolean.toString(displayOnFirstPage));
        return this;
    }

    /**
     * Control the visibility of the footer on the odd numbered pages of the generated pdf document. The default value is True.
     * @param displayOnOddPages Display footer on odd pages or not.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setFooterDisplayOnOddPages(Boolean displayOnOddPages)
    {
        parameters.put("footer_display_on_odd_pages", Boolean.toString(displayOnOddPages));
        return this;
    }

    /**
     * Control the visibility of the footer on the even numbered pages of the generated pdf document. The default value is True.
     * @param displayOnEvenPages Display footer on even pages or not.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setFooterDisplayOnEvenPages(Boolean displayOnEvenPages)
    {
        parameters.put("footer_display_on_even_pages", Boolean.toString(displayOnEvenPages));
        return this;
    }

    /**
     * Add a special footer on the last page of the generated pdf document only. The default value is False.
     * Use setFooterUrl or setFooterHtml and setFooterBaseUrl to specify the content of the last page footer.
     * Use setFooterHeight to specify the height of the special last page footer.
     * @param displayOnLastPage Display special footer on the last page or not.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setFooterDisplayOnLastPage(Boolean displayOnLastPage)
    {
        parameters.put("footer_display_on_last_page", Boolean.toString(displayOnLastPage));
        return this;
    }

    /**
     * Set the width in pixels used by the converter's internal browser window during the conversion of the footer content. The default value is 1024px.
     * @param footerWebPageWidth Browser window width in pixels.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setFooterWebPageWidth(int footerWebPageWidth)
    {
        parameters.put("footer_web_page_width", Integer.toString(footerWebPageWidth));
        return this;
    }

    /**
     * Set the height in pixels used by the converter's internal browser window during the conversion of the footer content.
     * The default value is 0px and it means that the page height is automatically calculated by the converter.
     * @param footerWebPageHeight Browser window height in pixels. Set it to 0px to automatically calculate page height.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setFooterWebPageHeight(int footerWebPageHeight)
    {
        parameters.put("footer_web_page_height", Integer.toString(footerWebPageHeight));
        return this;
    }

    /**
     * Show page numbers. Default value is True. Page numbers will be displayed in the footer of the PDF document.
     * @param showPageNumbers Show page numbers or not.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setShowPageNumbers(Boolean showPageNumbers)
    {
        parameters.put("page_numbers", Boolean.toString(showPageNumbers));
        return this;
    }

    /**
     * Control the page number for the first page being rendered. The default value is 1.
     * @param firstPageNumber First page number.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setPageNumbersFirst(int firstPageNumber)
    {
        parameters.put("page_numbers_first", Integer.toString(firstPageNumber));
        return this;
    }

    /**
     * Control the total number of pages offset in the generated pdf document. The default value is 0.
     * @param totalPagesOffset Offset for the total number of pages in the generated pdf document.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setPageNumbersOffset(int totalPagesOffset)
    {
        parameters.put("page_numbers_offset", Integer.toString(totalPagesOffset));
        return this;
    }

    /**
     * Set the text that is used to display the page numbers.
     * It can contain the placeholder {page_number} for the current page number and {total_pages} for the total number of pages. 
     * The default value is "Page: {page_number} of {total_pages}".
     * @param template Page numbers template.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setPageNumbersTemplate(String template)
    {
        parameters.put("page_numbers_template", template);
        return this;
    }

    /**
     * Set the font used to display the page numbers text. The default value is "Helvetica".
     * @param fontName The font used to display the page numbers text.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setPageNumbersFontName(String fontName)
    {
        parameters.put("page_numbers_font_name", fontName);
        return this;
    }

    /**
     * Set the size of the font used to display the page numbers. The default value is 10 points.
     * @param fontSize The size in points of the font used to display the page numbers.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setPageNumbersFontSize(int fontSize)
    {
        parameters.put("page_numbers_font_size", Integer.toString(fontSize));
        return this;
    }

    /**
     * Set the alignment of the page numbers text. The default value is PageNumbersAlignment.Right.
     * @param alignment The alignment of the page numbers text.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setPageNumbersAlignment(ApiEnums.PageNumbersAlignment alignment)
    {
        parameters.put("page_numbers_alignment", alignment.getValueAsString());
        return this;
    }

    /**
     * Specify the color of the page numbers text in #RRGGBB html format. The default value is #333333.
     * @param color Page numbers color.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setPageNumbersColor(String color)
    {
        if (!color.matches("^#?[0-9a-fA-F]{6}$"))
            throw new ApiException("Color value must be in #RRGGBB format.");

        parameters.put("page_numbers_color", color);
        return this;
    }

    /**
     * Specify the position in points on the vertical where the page numbers text is displayed in the footer. The default value is 10 points.
     * @param position Page numbers Y position in points.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setPageNumbersVerticalPosition(int position)
    {
        parameters.put("page_numbers_pos_y", Integer.toString(position));
        return this;
    }

    /**
     * Generate automatic bookmarks in pdf. The elements that will be bookmarked are defined using CSS selectors. 
     * For example, the selector for all the H1 elements is "H1", the selector for all the elements with the CSS class name 'myclass' is "*.myclass" and 
     * the selector for the elements with the id 'myid' is "*#myid". Read more about CSS selectors <a href="http://www.w3schools.com/cssref/css_selectors.asp" target="_blank">here</a>.
     * @param selectors CSS selectors used to identify HTML elements, comma separated..
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setPdfBookmarksSelectors(String selectors)
    {
        parameters.put("pdf_bookmarks_selectors", selectors);
        return this;
    }

    /**
     * Exclude page elements from the conversion. The elements that will be excluded are defined using CSS selectors. 
     * For example, the selector for all the H1 elements is "H1", the selector for all the elements with the CSS class name 'myclass' is "*.myclass" and 
     * the selector for the elements with the id 'myid' is "*#myid". Read more about CSS selectors <a href="http://www.w3schools.com/cssref/css_selectors.asp" target="_blank">here</a>.
     * @param selectors CSS selectors used to identify HTML elements, comma separated..
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setPdfHideElements(String selectors)
    {
        parameters.put("pdf_hide_elements", selectors);
        return this;
    }

    /**
     * Convert only a specific section of the web page to pdf. 
     * The section that will be converted to pdf is specified by the html element ID. 
     * The element can be anything (image, table, table row, div, text, etc).
     * @param elementID HTML element ID.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setPdfShowOnlyElementID(String elementID)
    {
        parameters.put("pdf_show_only_element_id", elementID);
        return this;
    }

    /**
     * Get the locations of page elements from the conversion. The elements that will have their locations retrieved are defined using CSS selectors. 
     * For example, the selector for all the H1 elements is "H1", the selector for all the elements with the CSS class name 'myclass' is "*.myclass" and 
     * the selector for the elements with the id 'myid' is "*#myid". Read more about CSS selectors <a href="http://www.w3schools.com/cssref/css_selectors.asp" target="_blank">here</a>.
     * @param selectors CSS selectors used to identify HTML elements, comma separated..
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setPdfWebElementsSelectors(String selectors)
    {
        parameters.put("pdf_web_elements_selectors", selectors);
        return this;
    }

    /**
     * Set converter startup mode. The default value is StartupMode.Automatic and the conversion is started immediately.
     * By default this is set to StartupMode.Automatic and the conversion is started as soon as the page loads (and conversion delay set with setConversionDelay elapses). 
     * If set to StartupMode.Manual, the conversion is started only by a javascript call to <i>SelectPdf.startConversion()</i> from within the web page.
     * @param startupMode Converter startup mode.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setStartupMode(ApiEnums.StartupMode startupMode)
    {
        parameters.put("startup_mode", startupMode.toString());
        return this;
    }

    /**
     * Internal use only.
     * @param skipDecoding The default value is True.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setSkipDecoding(Boolean skipDecoding)
    {
        parameters.put("skip_decoding", Boolean.toString(skipDecoding));
        return this;
    }

    /**
     * Set a flag indicating if the images from the page are scaled during the conversion process. The default value is False and images are not scaled.
     * @param scaleImages Scale images or not.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setScaleImages(Boolean scaleImages)
    {
        parameters.put("scale_images", Boolean.toString(scaleImages));
        return this;
    }

    /**
     * Generate a single page PDF. The converter will automatically resize the PDF page to fit all the content in a single page.
     * The default value of this property is False and the PDF will contain several pages if the content is large.
     * @param generateSinglePagePdf Generate a single page PDF or not.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setSinglePagePdf(Boolean generateSinglePagePdf)
    {
        parameters.put("single_page_pdf", Boolean.toString(generateSinglePagePdf));
        return this;
    }

    /**
     * Get or set a flag indicating if an enhanced custom page breaks algorithm is used. 
     * The enhanced algorithm is a little bit slower but it will prevent the appearance of hidden text in the PDF when custom page breaks are used.
     * The default value for this property is False.
     * @param enableEnhancedPageBreaksAlgorithm Enable enhanced page breaks algorithm or not.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setPageBreaksEnhancedAlgorithm(Boolean enableEnhancedPageBreaksAlgorithm)
    {
        parameters.put("page_breaks_enhanced_algorithm", Boolean.toString(enableEnhancedPageBreaksAlgorithm));
        return this;
    }

    /**
     * Set HTTP cookies for the web page being converted.
     * @param cookies HTTP cookies that will be sent to the page being converted.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setCookies(HashMap<String, String> cookies) {
        parameters.put("cookies_string", serializeDictionary(cookies));
        return this;
    }

    /**
     * Set a custom parameter. Do not use this method unless advised by SelectPdf.
     * @param parameterName Parameter name.
     * @param parameterValue Parameter value.
     * @return Reference to the current object.
     */
    public HtmlToPdfClient setCustomParameter(String parameterName, String parameterValue) {
        parameters.put(parameterName, parameterValue);
        return this;
    }

    /**
     * Get the locations of certain web elements. This is retrieved if pdf_web_elements_selectors parameter is set and elements were found to match the selectors.
     * @return List of web elements locations.
     */
    public String getWebElements() {
        WebElementsClient webElementsClient = new WebElementsClient(parameters.get("key"), jobId);
        webElementsClient.setApiAsyncEndpoint(apiWebElementsEndpoint);

        String webElements = webElementsClient.getWebElements();
        return webElements;
    }
}