package ui.ApiTest;

import com.sendgrid.*;

import java.io.IOException;

public class SendGridMain {

    public static String emailContent = "<h1><span style=\"background-color: #00ccff;\">Weather Application Daily Report:</span></h1>\n" +
            "<blockquote>\n" +
            "<h1><span style=\"font-size: 14px;\">W</span><span style=\"font-size: 14px;\">eather in ____ is description</span></h1>\n" +
            "</blockquote>\n" +
            "<h1 style=\"text-align: center;\"><span style=\"font-size: 14px; background-color: #ffffff;\"><img src=\"https://www.weatherbit.io/static/img/icons/t01d.png\" alt=\"interactive connection\" width=\"156\" height=\"156\" /></span></h1>\n" +
            "<table style=\"height: 42px; width: 526px; background-color: #0fd8f5; border-color: #ffffff; margin-left: auto; margin-right: auto;\" border=\"2\" cellspacing=\"3\" cellpadding=\"10\">\n" +
            "<tbody>\n" +
            "<tr>\n" +
            "<td style=\"width: 181.2px; text-align: center;\"><span style=\"color: #0000ff;\"><strong>Weather for location:</strong></span></td>\n" +
            "<td style=\"width: 290px; text-align: center;\">a</td>\n" +
            "</tr>\n" +
            "<tr style=\"text-align: center;\">\n" +
            "<td style=\"width: 181.2px;\"><span style=\"color: #0000ff;\"><strong>Longitude:</strong></span></td>\n" +
            "<td style=\"width: 290px;\">a</td>\n" +
            "</tr>\n" +
            "<tr style=\"text-align: center;\">\n" +
            "<td style=\"width: 181.2px;\"><span style=\"color: #0000ff;\"><strong>Latitude:</strong></span></td>\n" +
            "<td style=\"width: 290px;\">a</td>\n" +
            "</tr>\n" +
            "<tr style=\"text-align: center;\">\n" +
            "<td style=\"width: 181.2px;\"><span style=\"color: #0000ff;\"><strong>Temperature: (Celcius)</strong></span></td>\n" +
            "<td style=\"width: 290px;\">aasd</td>\n" +
            "</tr>\n" +
            "<tr style=\"text-align: center;\">\n" +
            "<td style=\"width: 181.2px;\"><span style=\"color: #0000ff;\"><strong>Feels Like:</strong></span></td>\n" +
            "<td style=\"width: 290px;\">a</td>\n" +
            "</tr>\n" +
            "<tr style=\"text-align: center;\">\n" +
            "<td style=\"width: 181.2px;\"><span style=\"color: #0000ff;\"><strong>Rain: (mm/hr)</strong></span></td>\n" +
            "<td style=\"width: 290px;\">a</td>\n" +
            "</tr>\n" +
            "<tr style=\"text-align: center;\">\n" +
            "<td style=\"width: 181.2px;\"><span style=\"color: #0000ff;\"><strong>Snow: (mm/hr)</strong></span></td>\n" +
            "<td style=\"width: 290px;\">sdasd</td>\n" +
            "</tr>\n" +
            "</tbody>\n" +
            "</table>\n" +
            "<p style=\"text-align: center;\">&nbsp;</p>\n" +
            "<p><span style=\"color: #ff0000;\">Click on the following link for more weather information:</span>&nbsp;<a href=\"https://www.theweathernetwork.com/ca/weather/british-columbia/richmond\">https://www.theweathernetwork.com/ca/weather/british-columbia/richmond</a></p>\n" +
            "<p style=\"text-align: center;\">&nbsp;</p>\n" +
            "<p style=\"text-align: left;\">Sent from SendGripAPI:&nbsp;<code class=\"language-bash\"><span class=\"token string\">Authorization: Bearer&nbsp;tHyGmOOkTAKel9lWgWGJ9Q</span></code></p>\n" +
            "<p>&nbsp;</p>\n" +
            "<p style=\"text-align: center;\">&nbsp;</p>\n" +
            "<p>&nbsp;</p>\n" +
            "<p style=\"text-align: left;\">&nbsp;</p>\n" +
            "<p style=\"text-align: center;\">&nbsp;</p>";

    public static void main(String[] args) throws IOException {
        Email from = new Email("ericrkuo@gmail.com");
        String subject = "Weather Application Daily Report";
        Email to = new Email("ericrkuo@gmail.com");
        Content content = new Content("text/html", "<html>"+emailContent+"</html>");
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid("YOUR API KEY");
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException ex) {
            throw ex;
        }
    }
}