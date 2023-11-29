FROM node:lts-alpine as build

WORKDIR /app

COPY ./package*.json ./

RUN npm config set proxy http://192.168.4.8:3128
RUN npm config set https-proxy http://192.168.4.8:3128

RUN npm install

COPY . .

RUN npm run build

FROM node:lts-alpine AS production

RUN mkdir -p /etc/apt/apt.conf.d/ && \
    echo 'Acquire::http::Proxy "http://192.168.4.8:3128";' > /etc/apt/apt.conf.d/proxy

COPY --from=build /app/build .
COPY --from=build /app/package.json .
COPY --from=build /app/package-lock.json .

RUN npm config set proxy http://192.168.4.8:3128
RUN npm config set https-proxy http://192.168.4.8:3128

RUN npm ci --omit dev

EXPOSE 3000

CMD ["node", "."]