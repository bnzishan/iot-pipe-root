(help: https://ropenscilabs.github.io/r-docker-tutorial/04-Dockerhub.html)

https://cloud.docker.com

equinoxbnz (username)

bushra.nazeer@gmail.com (primaary email)

bnz_repo (private repo)

-----------------------------------------------------

To push a new tag to this repository:
        docker push equinoxbnz/bnz_repo:tagname
        
        
-----------------------------------------------------        

Docker commands:

REPOSITORY              TAG       IMAGE ID         CREATED           SIZE
verse_gapminder_gsl     latest    023ab91c6291     3 minutes ago     1.975 GB
verse_gapminder         latest    bb38976d03cf     13 minutes ago    1.955 GB


docker login --username=yourhubusername --email=youremail@company.com

docker images

docker tag bb38976d03cf yourhubusername/verse_gapminder:firsttry

docker push yourhubusername/verse_gapminder

-----------------------------------------------------

To save the Docker container locally as a a tar archive, and then you can easily load that to an image when needed.to save the Docker container locally as a a tar archive, 
and then you can easily load that to an image when needed.
        docker save verse_gapminder > verse_gapminder.tar
        docker load --input verse_gapminder.tar