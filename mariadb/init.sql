-- Create the pfe database

CREATE DATABASE IF NOT EXISTS pfe;

-- Create the root user with the specified password

CREATE USER IF NOT EXISTS 'root'@'172.18.0.3' IDENTIFIED BY 'root';

-- Grant all privileges to the root user on the pfe database

GRANT ALL PRIVILEGES ON pfe.* TO 'root'@'172.18.0.3';

-- Reload privileges to apply changes

FLUSH PRIVILEGES;