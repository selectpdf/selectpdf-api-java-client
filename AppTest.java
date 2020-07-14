package com.selectpdf;

/**
 * Test for SelectPdf API Client for Java.
 */
public class AppTest 
{
    public static void main(String[] args) throws Exception {
        String url = "https://selectpdf.com";
        String outFile = "test.pdf";

        try {
            HtmlToPdfClient api = new HtmlToPdfClient("Your key here");

            api
                .setPageSize(ApiEnums.PageSize.A4)
                .setPageOrientation(ApiEnums.PageOrientation.Portrait)
                .setMargins(0)
                .setNavigationTimeout(30)
                .setShowPageNumbers(false)
                .setPageBreaksEnhancedAlgorithm(true)
            ;

            System.out.println("Starting conversion ...");

            api.convertUrlToFile(url, outFile);

            System.out.println("Conversion finished successfully!");

            UsageClient usage = new UsageClient("Your key here");
            String info = usage.getUsage(false);
            System.out.println(info);
        }
        catch (Exception ex) {
            System.out.println("An error occured: " + ex.getMessage());
        }
        
    }
}
