FROM node:carbon
WORKDIR /usr/src/app

COPY package.json ./
COPY src/ ./
RUN mkdir settings
COPY settings/ ./settings/

RUN npm install

EXPOSE 8080
CMD ["node", "server.js"]