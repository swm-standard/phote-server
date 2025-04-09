#!/bin/bash

IS_GREEN=$(docker ps | grep green)
DEFAULT_CONF=" /etc/nginx/nginx.conf"

if [ -z $IS_GREEN  ];then

  echo "### GREEN => BLUE ###"

  echo "1. get blue image"
  docker compose pull blue

  echo "2. blue container up"
  docker compose up -d blue

  while [ 1 = 1 ]; do
  echo "3. blue health check..."
  sleep 3

  REQUEST=$(curl ${server.address})
    if [ -n "$REQUEST" ]; then
            echo "health check success"
            break ;
            fi
  done;

  echo "4. reload nginx"
  sudo cp /etc/nginx/nginx.blue.conf /etc/nginx/nginx.conf
  sudo nginx -s rel

  echo "5. green container down"
  docker compose stop green
else
  echo "### BLUE => GREEN ###"

  echo "1. get green image"
  docker compose pull green

  echo "2. green container up"
