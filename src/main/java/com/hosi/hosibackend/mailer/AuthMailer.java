package com.hosi.hosibackend.mailer;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthMailer {
    private final JavaMailSender javaMailSender;

    public void sendActiveMail(String email, String token) throws MessagingException {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        String subject = "Verify OTP";
        String content =
                "<div style=\"background-color:#d5d9e2; padding: 15px 0 15px 0;\">\n" +
                "<div style=\"background-color:#ffffff; padding: 45px 0 34px 0; border-radius: 24px; margin:40px auto; max-width: 600px;\">\n" +
                "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" height=\"auto\" style=\"border-collapse:collapse\">\n" +
                        "    <tbody>\n" +
                        "    <tr>\n" +
                        "      <td align=\"center\" valign=\"center\" style=\"text-align:center; padding-bottom: 10px\">\n" +
                        "        <div style=\"text-align:center; margin:0 15px 34px 15px\">\n" +
                        "          <div style=\"margin-bottom: 10px\">\n" +
                        "              <img alt=\"Logo\" src=\"https://hopgiaysi.com/stores/4/medium/hopgiaysi-v3.png\" style=\"height: 35px\">\n" +
                        "          </div>\n" +
                        "          <div style=\"margin-bottom: 15px\">\n" +
                        "            <img alt=\"Logo\" src=\"https://i.ibb.co/MMsmcTr/icon-positive-vote-1.png\">\n" +
                        "          </div>\n" +
                        "          <div style=\"font-size: 14px; font-weight: 500; margin-bottom: 27px; font-family:Arial,Helvetica,sans-serif;\">\n" +
                        "            <p style=\"margin-bottom:9px; color:#181C32; font-size: 22px; font-weight:700\">Xin chào, cảm ơn bạn đã đăng ký!</p>\n" +
                        "            <p style=\"margin-bottom:2px; color:#7E8299\">Xưởng sản xuất hộp cứng cao cấp</p>\n" +
                        "            <p style=\"margin-bottom:2px; color:#7E8299\">CHUYÊN NGHIỆP - UY TÍN - TIẾT KIỆM</p>\n" +
                        "          </div>\n" +
                        "          <a href=\"http://localhost:3000/verify-email?token=" +token+ "\" target=\"_blank\" style=\"background-color:#3E97FF; border-radius:6px;display:inline-block; padding:11px 19px; color: #FFFFFF; font-size: 14px; font-weight:500;\">\n" +
                        "            Kích Hoạt Tài Khoản\n" +
                        "          </a>\n" +
                        "        </div>\n" +
                        "      </td>\n" +
                        "    </tr>\n" +
                        "    <tr>\n" +
                        "      <td align=\"center\" valign=\"center\" style=\"font-size: 13px; padding:0 15px; text-align:center; font-weight: 500; color: #A1A5B7;font-family:Arial,Helvetica,sans-serif\">\n" +
                        "        <p>\n" +
                        "          © Copyright HopGiaySi.\n" +
                        "        </p>\n" +
                        "      </td>\n" +
                        "    </tr>\n" +
                        "    </tbody>\n" +
                        "  </table>" +
                "</div>" +
                "</div>";

        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(content, true);

        javaMailSender.send(mimeMessage);
    }
}
