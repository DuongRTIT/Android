import * as functions from "firebase-functions";
import * as nodemailer from "nodemailer";

const gmailEmail = functions.config().gmail.email;
const gmailPassword = functions.config().gmail.password;

const transporter = nodemailer.createTransport({
  service: "gmail",
  auth: {
    user: gmailEmail,
    pass: gmailPassword,
  },
});

export const sendInvitationEmail = functions.https.onCall(
  async (data, _context) => {
    const email = data.email;
    const eventName = data.eventName;

    const mailOptions = {
      from: `Sự kiện <${gmailEmail}>`,
      to: email,
      subject: "Lời mời tham gia sự kiện",
      text:
        `Xin chào,\n\n` +
        `Bạn đã được mời tham gia sự kiện: ${eventName}.\n` +
        `Hẹn gặp bạn tại sự kiện!\n\nTrân trọng.`,
    };

    try {
      await transporter.sendMail(mailOptions);
      console.log("✅ Đã gửi email đến:", email);
      return { success: true };
    } catch (error) {
      console.error("❌ Lỗi gửi mail:", error);
      return { success: false, error: error.toString() };
    }
  }
);
