FROM node:12

WORKDIR /usr/src/app

COPY package*.json ./

RUN npm install

COPY . .

ARG SERVER_URL

ENV SERVER_URL $SERVER_URL

ARG REACT_APP_GAME_URL

ENV REACT_APP_GAME_URL $REACT_APP_GAME_URL

ARG TARGET

ENV TARGET $TARGET

ENV SECURE=true

ENV LIMIT_GAMES=false

RUN npm run build

EXPOSE $PORT

CMD ["npm", "run", "server"]