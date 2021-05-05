# build the image
docker build -t springio/demo .

# run image
docker run -p 8080:8080 springio/demo