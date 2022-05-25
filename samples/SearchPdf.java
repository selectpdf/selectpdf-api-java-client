package com.selectpdf;

public class SearchPdf {
    public static void main(String[] args) throws Exception {
        String testUrl = "https://selectpdf.com/demo/files/selectpdf.pdf";
        String testPdf = "Input.pdf";
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

            System.out.println("Starting search pdf...");

            // search local pdf
            String results = client.searchFile(testPdf, "pdf");

            // search pdf from public url
            // String results = client.searchUrl(testUrl, "pdf");

            System.out.printf("Search results:\r\n%s\r\n", results);

            // org.json.JSONArray textPositions = new org.json.JSONArray(results);
            // System.out.printf("Search results count: %d\r\n", textPositions.length());

            System.out.println(String.format("Finished! Number of pages processed: %d.", client.getNumberOfPages()));

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
