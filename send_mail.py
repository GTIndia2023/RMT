import os
import smtplib
from email.message import EmailMessage

email_user = os.getenv("EMAIL_USER")
email_pass = os.getenv("EMAIL_PASS")
email_to = os.getenv("EMAIL_TO")
report_zip = os.getenv("REPORT_ZIP", "allure-report.zip")

missing = [name for name, value in {
    "EMAIL_USER": email_user,
    "EMAIL_PASS": email_pass,
    "EMAIL_TO": email_to,
}.items() if not value]

if missing:
    raise RuntimeError(f"Missing required email environment variables: {', '.join(missing)}")

msg = EmailMessage()
msg['Subject'] = 'RMT Automation Test Report - UAT'
msg['From'] = email_user
msg['To'] = email_to

msg.set_content("Please find attached the latest automation test report.")

with open(report_zip, 'rb') as f:
    msg.add_attachment(f.read(), maintype='application', subtype='zip', filename=report_zip)

with smtplib.SMTP_SSL('smtp.gmail.com', 465) as smtp:
    smtp.login(email_user, email_pass)
    smtp.send_message(msg)
