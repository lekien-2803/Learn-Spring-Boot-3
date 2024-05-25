# Cài đặt WSL và Docker

## Cài đặt WSL

Mở PowerShell và nhập:

```pwsh
wsl --install -d Ubuntu
```

## Cài đặt Docker trên WSL

Cập nhật các gói hệ thống bằng cách chạy lệnh:

```bash
sudo apt update
sudo apt upgrade
```

Cài đặt gói để cho phép apt sử dụng HTTPS:

```bash
sudo apt install apt-transport-https ca-certificates curl software-properties-common
```

Thêm GPG key cho Docker repository:

```bash
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -

```

Thêm Docker repository vào danh sách sources của apt:

```bash
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"
```

Cập nhật danh sách gói:

```bash
sudo apt update
```

Cài đặt Docker:

```bash
sudo apt install docker-ce
```

Kiểm tra xem Docker đã được cài đặt thành công chưa bằng lệnh:

```bash
docker --version
```

## Cài đặt Docker Compose trên WSL

Tải xuống binary của Docker Compose:

*Lưu ý: Hãy kiểm tra phiên bản mới nhất của Docker Compose và thay thế 1.29.2 trong URL trên nếu có phiên bản mới hơn.*

```bash
sudo chmod +x /usr/local/bin/docker-compose
```

Kiểm tra xem Docker Compose đã được cài đặt thành công chưa:

```bash
docker-compose --version
```

## Docker-compose

```yaml
version: '3.7'

services:
  mysql:
    image: mysql:latest
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: abc123-
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

  portainer:
    image: portainer/portainer-ce:latest
    restart: always
    ports:
      - "9000:9000"
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
      - portainer_data:/data

volumes:
  mysql_data:
  portainer_data:
```