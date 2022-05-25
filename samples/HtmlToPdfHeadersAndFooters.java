package com.selectpdf;

public class HtmlToPdfHeadersAndFooters {
    public static void main(String[] args) throws Exception {
        String url = "https://selectpdf.com";
        String localFile = "Test.pdf";
        String apiKey = "Your API key here";

        System.out.println(String.format("This is SelectPdf-%s.", ApiClient.CLIENT_VERSION));

        try {
            HtmlToPdfClient client = new HtmlToPdfClient(apiKey);

            // set parameters - see full list at https://selectpdf.com/html-to-pdf-api/
            client
                .setMargins(0) // PDF page margins
                .setPageBreaksEnhancedAlgorithm(true) // enhanced page break algorithm

                // header properties
                .setShowHeader(true) // display header
                // .setHeaderHeight(50) // header height
                // .setHeaderUrl(url) // header url
                .setHeaderHtml("This is the <b>HEADER</b>!!!!") // header html

                // footer properties
                .setShowFooter(true) // display footer
                // .setFooterHeight(60) // footer height
                // .setFooterUrl(url) // footer url
                .setFooterHtml("This is the <b>FOOTER</b>!!!!") // footer html

                // footer page numbers
                .setShowPageNumbers(true) // show page numbers in footer
                .setPageNumbersTemplate("{page_number} / {total_pages}") // page numbers template
                .setPageNumbersFontName("Verdana") // page numbers font name
                .setPageNumbersFontSize(12) // page numbers font size
                .setPageNumbersAlignment(ApiEnums.PageNumbersAlignment.Center) // page numbers alignment (2-Center)
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
