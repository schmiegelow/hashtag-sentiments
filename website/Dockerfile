FROM node:carbon
WORKDIR /usr/src/app

COPY package.json ./
COPY src/ ./
RUN mkdir settings
COPY settings/ ./settings/

# needs verification and merge of MyJetAp scaffold with src
RUN npm install
RUN npm install npm install oracle/ojet-cli 
RUN	ojet create MyJETApp --template=basic


EXPOSE 8000
CMD ["node", "ojet serve"]