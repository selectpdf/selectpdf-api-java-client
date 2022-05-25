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
