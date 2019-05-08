FROM lolhens/ammonite

RUN apt-get update && apt-get install -y \
  mongodb

COPY . .

RUN chmod +x docker-entrypoint.sh

ENTRYPOINT ["./docker-entrypoint.sh"]