FROM		ubuntu:18.04
MAINTAINER	myungsekyo@gmail.com

RUN		apt-get -y update
RUN		apt-get -y install nginx
RUN		apt-get -y install openjdk-8-jdk

ADD		. /humming

RUN		cp /humming/config/humming.service /etc/systemd/system/humming.service
RUN		cp /humming/config/humming /etc/nginx/sites-available/
RUN		ln -s /etc/nginx/sites-available/humming /etc/nginx/sites-enabled/humming


CMD ["/humming/start.sh"]
