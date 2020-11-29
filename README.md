# HummingInsideDocker

## Install docker
https://docs.docker.com/get-docker/

The official Docker homepage provides installation instructions for Windows, Mac, and linux respectively. You can install Docker by referring to the link above.

## Get Dockerfile
```bash
$ git clone https://github.com/HummingInside/HummingInsideDocker.git
```


## Build Docker Image
The Dockerfile must be built as a docker image. The Dockerfile is in the HummingInsideDocker directory. You can build the Dockerfile with below commands

```bash
$ cd HummingInsideDocker	
// move to HummingInsideDocker dir
$ docker build -t humming . 	
// build the Dockerfile of the current dir
// don’t forget the last dot
// In some cases, this requires sudo
```

## Run Docker Container
Now you need to run a docker container of the image. You can run the container with commands below.
```bash
$ docker run --name humming_container -p 80:80 humming
```

## Check With Your Chrome Browser
If you followed the steps above well, you can access the humming inside service hosted on your computer by entering the localhost address to your chrome browser. For easy testing, you can access the service containing some test data.


Enter this link!
http://localhost/
