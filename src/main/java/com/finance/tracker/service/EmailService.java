package com.finance.tracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendFamilyCode(String toEmail, String username, String familyCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("Finance Tracker <noreply@financetracker.com>");
        message.setTo(toEmail);
        message.setSubject("Selamat Datang di Finance Tracker! Kode Keluarga Anda");

        String text = "Halo " + username + ",\n\n" +
                "Terima kasih telah mendaftar sebagai Kepala Keluarga (Parent).\n" +
                "Berikut adalah KODE KELUARGA Anda:\n\n" +
                "👉 " + familyCode + " 👈\n\n" +
                "Bagikan kode ini kepada anggota keluarga (Anak/Pasangan) agar mereka bisa bergabung ke dalam dompet keluarga Anda.\n\n" +
                "Salam,\n" +
                "Tim Finance Tracker";

        message.setText(text);

        try {
            mailSender.send(message);
            System.out.println("Email berhasil dikirim ke " + toEmail);
        } catch (Exception e) {
            System.err.println("Gagal mengirim email: " + e.getMessage());
        }
    }
}