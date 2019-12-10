#!/bin/sh

if [ $TRAVIS_BRANCH != "master" ] || [ $TRAVIS_PULL_REQUEST = "true" ]; then
  echo "We're not on the master branch or this is a pull request, skipping docker deployment."
  exit 0
fi

docker build -t trasier/trasier-server .
docker login -e $DOCKER_EMAIL -u $DOCKER_USER -p $DOCKER_PASS
docker push trasier/trasier-server
docker logout
