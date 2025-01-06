# Generates self-signed ssl certificate and key; (should be replaced with let's encrypt/cerbot later)
sudo openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
    -keyout nginx-selfsigned.key \
    -out nginx-selfsigned.crt