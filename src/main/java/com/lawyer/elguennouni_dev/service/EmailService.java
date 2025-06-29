package com.lawyer.elguennouni_dev.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String to, String otp) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

            String htmlContent = generateEmailTemplate(otp);

            helper.setTo(to);
            helper.setSubject("Your OTP Verification Code - Moroccan AI Law Assistant");

            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void sendOtpLogin(String to, String otp) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            String htmlContent = generateEmailTemplateLoginVerification(otp);
            helper.setTo(to);
            helper.setSubject("Your OTP Login Verification Code - Moroccan AI Law Assistant");
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    private String generateEmailTemplateLoginVerification(String otp) {
        return String.format("""
   <!DOCTYPE html>
   <html lang="en">
   <head>
       <meta charset="UTF-8">
       <meta name="viewport" content="width=device-width, initial-scale=1.0">
       <title>Login Verification Code</title>
   </head>
   <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;">
       <table width="100%%" border="0" cellpadding="0" cellspacing="0">
           <tr>
               <td align="center" style="padding: 20px 0;">
                   <table width="100%%" border="0" cellpadding="0" cellspacing="0" style="max-width: 500px; border-collapse: collapse;">
                       <tr>
                           <td bgcolor="#ffffff" style="padding: 30px; border: 1px solid #ddd; border-radius: 10px;">
                               <table width="100%%" border="0" cellpadding="0" cellspacing="0">
                                   <tr>
                                       <td align="center" style="padding-bottom: 20px;">
                                           <h2 style="margin: 0; color: #2c3e50; font-family: Arial, sans-serif;">Login Verification Code</h2>
                                       </td>
                                   </tr>
                                   <tr>
                                       <td style="color: #333333; font-family: Arial, sans-serif; font-size: 16px; line-height: 1.5;">
                                           <p style="margin: 0 0 15px 0;">Hello,</p>
                                           <p style="margin: 0 0 15px 0;">Here is your one-time password (OTP) to complete your login to <strong>Moroccan AI Law Assistant</strong>:</p>
                                       </td>
                                   </tr>
                                   <tr>
                                       <td align="center" style="padding: 20px 0;">
                                           <div style="background-color: #eaf4ff; padding: 15px; font-size: 24px; font-weight: bold; text-align: center; letter-spacing: 5px; border-radius: 8px; color: #1a73e8; font-family: 'Courier New', Courier, monospace;">
                                               %s
                                           </div>
                                       </td>
                                   </tr>
                                   <tr>
                                       <td style="color: #333333; font-family: Arial, sans-serif; font-size: 16px; line-height: 1.5;">
                                           <p style="margin: 20px 0 15px 0;">This code is valid for the next 5 minutes.</p>
                                           <p style="margin: 0;">If you did not attempt to log in, please secure your account immediately.</p>
                                       </td>
                                   </tr>
                                   <tr>
                                       <td align="center" style="padding: 30px 0 0 0;">
                                           <p style="font-size: 12px; color: #777; margin: 0;">&copy; 2025 Moroccan AI Law Assistant</p>
                                       </td>
                                   </tr>
                               </table>
                           </td>
                       </tr>
                   </table>
               </td>
           </tr>
       </table>
   </body>
   </html>
   """, otp);
    }

    private String generateEmailTemplate(String otp) {
        return String.format("""
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Your OTP Code</title>
        </head>
        <body style="margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f4f4f4;">
            <table width="100%%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                    <td align="center" style="padding: 20px 0;">
                        <table width="100%%" border="0" cellpadding="0" cellspacing="0" style="max-width: 500px; border-collapse: collapse;">
                            <tr>
                                <td bgcolor="#ffffff" style="padding: 30px; border: 1px solid #ddd; border-radius: 10px;">
                                    <table width="100%%" border="0" cellpadding="0" cellspacing="0">
                                        <tr>
                                            <td align="center" style="padding-bottom: 20px;">
                                                <h2 style="margin: 0; color: #2c3e50; font-family: Arial, sans-serif;">Your OTP Code</h2>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td style="color: #333333; font-family: Arial, sans-serif; font-size: 16px; line-height: 1.5;">
                                                <p style="margin: 0 0 15px 0;">Hello,</p>
                                                <p style="margin: 0 0 15px 0;">Here is your one-time password (OTP) to verify your email on <strong>Moroccan AI Law Assistant</strong>:</p>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td align="center" style="padding: 20px 0;">
                                                <div style="background-color: #eaf4ff; padding: 15px; font-size: 24px; font-weight: bold; text-align: center; letter-spacing: 5px; border-radius: 8px; color: #1a73e8; font-family: 'Courier New', Courier, monospace;">
                                                    %s
                                                </div>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td style="color: #333333; font-family: Arial, sans-serif; font-size: 16px; line-height: 1.5;">
                                                <p style="margin: 20px 0 15px 0;">This code is valid for the next 5 minutes.</p>
                                                <p style="margin: 0;">If you did not request this code, you can safely ignore this email.</p>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td align="center" style="padding: 30px 0 0 0;">
                                                <p style="font-size: 12px; color: #777; margin: 0;">&copy; 2025 Moroccan AI Law Assistant</p>
                                            </td>
                                        </tr>
                                    </table>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
        </body>
        </html>
        """, otp);
    }
}
