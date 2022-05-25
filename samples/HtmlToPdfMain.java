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
