#!/bin/sh

case "$1" in
	start)
		docker-compose -f docker-compose.yaml up -d
;;
	stop)
		docker-compose -f docker-compose.yaml stop
;;
	restart)
		docker-compose -f docker-compose.yaml restart
;;
	down)
		docker-compose -f docker-compose.yaml down -v
;;
	recreate)
		docker-compose -f docker-compose.yaml down -v
		docker-compose -f docker-compose.yaml up -d
esac
exit 0